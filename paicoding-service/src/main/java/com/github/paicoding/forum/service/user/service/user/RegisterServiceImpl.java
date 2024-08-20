package com.github.paicoding.forum.service.user.service.user;

import com.github.paicoding.forum.api.model.context.ReqInfoContext;
import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.api.model.enums.user.LoginTypeEnum;
import com.github.paicoding.forum.api.model.vo.notify.NotifyMsgEvent;
import com.github.paicoding.forum.core.cache.local.OHCacheConfig;
import com.github.paicoding.forum.core.util.SpringUtil;
import com.github.paicoding.forum.core.util.TransactionUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.OHCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String registerByUserNameAndPassword(String username, String password, String starNumber) {
        // 1. 判断用户名是否准确
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

    @Override
    public boolean containsUser(String username) {
        log.info("containsUser:{}", username);
        String s = OHCacheConfig.USERNAME_CACHE.get(username);
        if(s!=null){
            return false;
        }
        UserDO userByUserName = userDao.getUserByUserName(username);
        if (userByUserName != null) {
            OHCacheConfig.USERNAME_CACHE.put(username, "1");
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
}
