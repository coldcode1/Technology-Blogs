package com.github.paicoding.forum.web.front.register;

import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.api.model.vo.constants.StatusEnum;

import com.github.paicoding.forum.core.util.SessionUtil;
import com.github.paicoding.forum.service.user.repository.dao.UserDao;
import com.github.paicoding.forum.service.user.service.LoginService;
import com.github.paicoding.forum.service.user.service.RegisterService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.SecureRandom;


@Slf4j
@RestController
@RequestMapping("/register")
public class RegistRestController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Qualifier("userDao")
    @Autowired
    private UserDao userDao;


    @PostMapping("/sendcode")
    public ResVo<Boolean> sendCode(@RequestParam(name = "username") String userName,
                                 @RequestParam(name = "email") String email,
                                 HttpServletRequest httpServletRequest){
        String remoteAddr = httpServletRequest.getRemoteAddr();
        String sendCodeResp = registerService.sendRegisterCode(userName, email, remoteAddr);
        if(!"ok".equals(sendCodeResp)){
            return ResVo.fail(StatusEnum.REGIST_MAIL_FAILED, sendCodeResp);
        }
        return ResVo.ok(true);
    }



    @PostMapping("/user")
    public ResVo<Boolean> regist(@RequestParam(name = "username") String username,
                                 @RequestParam(name = "password") String password,
                                 @RequestParam(name = "email") String email,
                                 @RequestParam(name = "sendcode") String code,
                                 HttpServletRequest httpServletRequest,
                                 HttpServletResponse response){

        String remoteAddr = httpServletRequest.getRemoteAddr();
        String checkResp = registerService.registerCheck(username, remoteAddr, password, email, code.trim()+email.trim());
        if(!"ok".equals(checkResp)){
            return ResVo.fail(StatusEnum.REGIST_USER_FAILED,checkResp);
        }

        String session = registerService.registerByUserNameAndPassword(username, password, email);
        response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
        return ResVo.ok(true);
    }
}
