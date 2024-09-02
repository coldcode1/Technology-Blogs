package com.github.paicoding.forum.service.user.service.user;

import com.github.paicoding.forum.api.model.context.ReqInfoContext;
import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.api.model.enums.user.LoginTypeEnum;
import com.github.paicoding.forum.api.model.vo.notify.NotifyMsgEvent;
import com.github.paicoding.forum.core.bo.MailBO;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.common.MsgLogStatuesConstants;
import com.github.paicoding.forum.core.common.MyConstants;
import com.github.paicoding.forum.core.config.RabbitmqProperties;
import com.github.paicoding.forum.core.util.*;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import com.github.paicoding.forum.service.user.converter.UserAiConverter;
import com.github.paicoding.forum.service.user.help.UserSessionHelper;
import com.github.paicoding.forum.service.user.repository.dao.UserAiDao;
import com.github.paicoding.forum.service.user.repository.dao.UserDao;
import com.github.paicoding.forum.service.user.repository.entity.UserAiDO;
import com.github.paicoding.forum.service.user.repository.entity.UserDO;
import com.github.paicoding.forum.service.user.repository.entity.UserInfoDO;
import com.github.paicoding.forum.service.user.service.RegisterService;
import com.github.paicoding.forum.service.user.help.UserPwdEncoder;
import com.github.paicoding.forum.service.user.help.UserRandomGenHelper;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户注册服务
 *
 * @author YiHui
 * @date 2023/6/26
 */
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAiDao userAiDao;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private MsgLogService msgLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String registerByUserNameAndPassword(String username, String password, String starNumber) {

        UserDO user = new UserDO();
        user.setUserName(username);
        user.setPassword(userPwdEncoder.encPwd(password));
        user.setThirdAccountId("");
        // 用户名密码注册
        user.setLoginType(LoginTypeEnum.USER_PWD.getType());
        userDao.saveUser(user);

        // 3. 保存用户信息
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName("用户" + user.getUserName());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userInfo);

        // 4. 保存ai相互信息
        UserAiDO userAiDO = UserAiConverter.initAi(user.getId(),"");
        userAiDao.saveOrUpdateAiBindInfo(userAiDO, "999999");
        processAfterUserRegister(user.getId());
        ReqInfoContext.getReqInfo().setUserId(user.getId());
        String session = userSessionHelper.genSession(user.getId());
        return session;
    }

    @Override
    public String sendRegisterCode(String userName, String email, String remoteAddr) {
        log.info("开始发送验证码");
        // todo 0 校验用户名和邮箱是否合法
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(email)){
            return "用户名和邮箱不能为空";
        }

        if(containsUser(userName)){
            return "用户已存在";
        }

        if(redisTemplate.opsForValue().get(remoteAddr+ userName)!=null){
            return "验证码已发送过啦，1分钟后重试。";
        }

        String trueCode = generateCode(6);
        String title = "欢迎注册技术博客园";
        String content = "您的验证码是："+ trueCode + " . 欢迎帅气美丽的你注册技术博客园,,输入验证码,便可成为尊敬的技术博客大王~一起在技术博客园里发展自己的技术吧~";

        // 发送验证码
        if(rabbitmqProperties.getSwitchFlag()){
            String maidId = UUID.randomUUID().toString().replaceAll("-", "");
            MailBO mailBO = MailBO.builder().to(email).title(title).content(content).msgId(maidId).build();
            rabbitmqService.publishMailerMsg(CommonConstants.EXCHANGE_EMAIL_DIRECT, BuiltinExchangeType.DIRECT, CommonConstants.QUERE_KEY_EMAIL, mailBO);
        } else if (!EmailUtil.sendMailByRabbitMQ(title,email,content)) {
            return "验证码发送失败";
        }

        redisTemplate.opsForValue().set(remoteAddr+ userName, trueCode, 60, TimeUnit.SECONDS);
        return "ok";
    }

    @Override
    public String registerCheck(String username, String remoteAddr, String password, String email, String code ) {
        // todo 0 校验用户名/密码/邮箱是否合法
        if(StringUtils.isBlank(username) || StringUtils.isBlank(email) || StringUtils.isBlank(password) || StringUtils.isBlank(code)){
            return  "注册参数不能为空！";
        }
        String trueCode = redisTemplate.opsForValue().get(remoteAddr + username);
        if(trueCode==null){
            return "请重新获取验证码!";
        }
        // 验证码+邮箱双重一致。
        if(!StringUtils.equals(trueCode+email, code)){
            return "验证码错误";
        }
        if(redisTemplate.opsForValue().get(username)!=null){
            return "该用户已经存在";
        }
        redisTemplate.opsForValue().set("user_"+username, "1");
        return "ok";

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByWechat(String thirdAccount) {
        // 用户不存在，则需要注册
        // 1. 保存用户登录信息
        UserDO user = new UserDO();
        user.setThirdAccountId(thirdAccount);
        user.setLoginType(LoginTypeEnum.WECHAT.getType());
        userDao.saveUser(user);


        // 2. 初始化用户信息，随机生成用户昵称 + 头像
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(UserRandomGenHelper.genNickName());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userInfo);

        // 3. 保存ai相互信息
        UserAiDO userAiDO = UserAiConverter.initAi(user.getId());
        userAiDao.saveOrUpdateAiBindInfo(userAiDO, null);
        processAfterUserRegister(user.getId());

        return user.getId();
    }


    public boolean containsUser(String userName) {
        Object o = redisTemplate.opsForValue().get(userName);
        if(o!=null){
            return true;
        }
        if (userDao.getUserByUserName(userName) != null) {
            redisTemplate.opsForValue().set(userName, "1");
            return true;
        }
        return false;
    }

    /**
     * 用户注册完毕之后触发的动作
     *
     * @param userId
     */
    private void processAfterUserRegister(Long userId) {
        TransactionUtil.registryAfterCommitOrImmediatelyRun(new Runnable() {
            @Override
            public void run() {
                // 用户注册事件
                SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REGISTER, userId));
            }
        });
    }

    private static String generateCode(int length) {
        // 使用 SecureRandom 生成更安全的随机数
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 生成 0 到 9 的随机整数
            code.append(digit);
        }
        return code.toString();
    }
}
