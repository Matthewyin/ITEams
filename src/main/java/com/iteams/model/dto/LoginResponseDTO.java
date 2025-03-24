package com.iteams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private UserInfoDTO userInfo;
    
    // 登录状态：成功、失败、账户锁定
    private LoginStatus status;
    
    // 登录失败时的剩余尝试次数
    private Integer remainingAttempts;
    
    // 账户锁定时的解锁时间
    private LocalDateTime unlockTime;
    
    /**
     * 登录状态枚举
     */
    public enum LoginStatus {
        SUCCESS,     // 登录成功
        FAILED,      // 登录失败
        LOCKED       // 账户锁定
    }
}
