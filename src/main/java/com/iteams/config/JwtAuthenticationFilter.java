package com.iteams.config;

import com.iteams.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证过滤器
 * <p>
 * 用于验证请求中的JWT令牌
 * </p>
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, ApplicationContext applicationContext) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
    }

    private UserDetailsService getUserDetailsService() {
        if (userDetailsService == null) {
            userDetailsService = applicationContext.getBean(UserDetailsService.class);
        }
        return userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 获取JWT令牌
            String jwt = parseJwt(request);
            if (jwt != null) {
                log.debug("从请求中获取到JWT令牌");
                
                try {
                    // 从JWT中获取用户名
                    String username = jwtUtil.getUsernameFromToken(jwt);

                    // 如果用户名不为空且当前没有认证信息
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 加载用户详情
                        UserDetails userDetails = getUserDetailsService().loadUserByUsername(username);

                        // 验证JWT令牌
                        if (jwtUtil.validateToken(jwt, userDetails)) {
                            // 创建认证令牌
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            
                            // 将token存储在authentication的details中，便于后续使用
                            Map<String, Object> details = new HashMap<>();
                            details.put("token", jwt);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            
                            // 设置认证信息到上下文
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("成功验证JWT令牌并设置Authentication，用户: {}", username);
                        } else {
                            log.warn("JWT令牌验证失败");
                        }
                    }
                } catch (io.jsonwebtoken.MalformedJwtException e) {
                    log.error("JWT令牌格式错误: {}", e.getMessage());
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    log.error("JWT令牌已过期: {}", e.getMessage());
                } catch (io.jsonwebtoken.UnsupportedJwtException e) {
                    log.error("不支持的JWT令牌: {}", e.getMessage());
                } catch (io.jsonwebtoken.security.SignatureException e) {
                    log.error("JWT签名验证失败: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("JWT令牌处理异常: {}", e.getMessage());
                }
            } else {
                log.debug("请求中未找到JWT令牌");
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中解析JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
