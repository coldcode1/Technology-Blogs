package com.github.paicoding.forum.service.user.service;

import com.github.paicoding.forum.api.model.vo.user.UserPwdLoginReq;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户注册服务
 *
 * @author YiHui
 * @date 2023/6/26
 */
public interface RegisterService {
    /**
     * 通过用户名/密码进行注册
     *
     * @param loginReq
     * @return
     */
    String registerByUserNameAndPassword(String username, String password, String starNumber);


    String sendRegisterCode(String userName, String email, String remoteAddr);

    /**
     * 注册校验
     * @param username
     * @param password
     * @param starNumber
     * @return
     */
    String registerCheck(String username, String remoteAddr,  String password,  String email, String code);

    /**
     * 通过微信公众号进行注册
     *
     * @param thirdAccount
     * @return
     */
    Long registerByWechat(String thirdAccount);
}
