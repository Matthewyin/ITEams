package com.iteams.service.impl;

import com.iteams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 * <p>
 * 实现Spring Security的UserDetailsService接口，用于用户认证
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始加载用户详情: {}", username);
        try {
            UserDetails userDetails = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
            log.info("用户详情加载成功: {}, 权限: {}", username, userDetails.getAuthorities());
            return userDetails;
        } catch (Exception e) {
            log.error("加载用户详情失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
