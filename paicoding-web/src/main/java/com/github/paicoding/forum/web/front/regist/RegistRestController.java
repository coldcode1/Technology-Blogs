package com.github.paicoding.forum.web.front.regist;

import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.api.model.vo.constants.StatusEnum;
import com.github.paicoding.forum.core.util.EmailUtil;
import com.github.paicoding.forum.service.user.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.security.SecureRandom;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegistRestController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @PostMapping("/sendcode")
    public ResVo<Boolean> sendCode(@RequestParam(name = "userName") String userName,
                                 @RequestParam(name = "email") String email,
                                 HttpServletRequest httpServletRequest){

        log.info("开始发送验证码");
        // todo 0 校验用户名和邮箱是否合法
        if(Objects.isNull(userName) || Objects.isNull(email)){
            return ResVo.fail(StatusEnum.REGIST_MAIL_FAILED, "用户名和邮箱不能为空");
        }

        if(registerService.containsUser(userName)){
            return ResVo.fail(StatusEnum.REGIST_MAIL_FAILED, "用户已存在");
        }

        String remoteAddr = httpServletRequest.getRemoteAddr();
        if(redisTemplate.opsForValue().get(remoteAddr+ userName)!=null){
            return ResVo.fail(StatusEnum.REGIST_MAIL_FAILED, "验证码已发送过啦，1分钟后重试。");
        }

        String title = "欢迎注册技术博客园";
        String content = "您的验证码是："+generateCode(6);

        // 发生验证码
        if(!EmailUtil.sendMailByRabbitMQ(title,email,content)){
            return ResVo.fail(StatusEnum.REGIST_MAIL_FAILED, "验证码发送失败");
        }
        redisTemplate.opsForValue().set(remoteAddr+ userName, "1", 60);
        return ResVo.ok(true);
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

    @PostMapping("/registeruser")
    public ResVo<Boolean> regist(@RequestParam(name = "username") String username,
                                 @RequestParam(name = "password") String password,
                                 @RequestParam(name = "email") String email,
                                 @RequestParam(name = "sendcode") String code,
                                 HttpServletRequest httpServletRequest){

        // todo 0 校验用户名/密码/邮箱是否合法
        if(Objects.isNull(username) || Objects.isNull(email)){
            return ResVo.fail(StatusEnum.REGIST_USER_FAILED, "用户名和邮箱不能为空");
        }
        String remoteAddr = httpServletRequest.getRemoteAddr();

        String trueCode = redisTemplate.opsForValue().get(remoteAddr + username);

        if(trueCode==null){
            return ResVo.fail(StatusEnum.REGIST_USER_FAILED, "验证码已过期，请重新获取");
        }

        if(!StringUtils.equals(trueCode, code)){
            return ResVo.fail(StatusEnum.REGIST_USER_FAILED, "验证码错误");
        }

        return ResVo.ok(true);
    }
}
