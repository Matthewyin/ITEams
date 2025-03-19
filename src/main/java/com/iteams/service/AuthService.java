package com.iteams.service;

import com.iteams.model.dto.LoginRequestDTO;
import com.iteams.model.dto.LoginResponseDTO;
import com.iteams.model.dto.UserInfoDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoDTO getCurrentUserInfo();

    /**
     * 用户登出
     */
    void logout();
}
