package com.iteams.service;

import com.iteams.model.dto.UserInfoDTO;
import com.iteams.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    Optional<User> getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * 分页获取用户列表
     * 
     * @param query 查询条件
     * @param role 角色筛选
     * @param status 状态筛选
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<User> getUsers(String query, String role, Integer status, Pageable pageable);
    
    /**
     * 创建用户
     * 
     * @param user 用户对象
     * @return 创建后的用户
     */
    User createUser(User user);
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户对象
     * @return 更新后的用户
     */
    User updateUser(Long id, User user);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 重置用户密码
     * 
     * @param id 用户ID
     * @return 重置后的密码
     */
    String resetPassword(Long id);
    
    /**
     * 获取所有角色列表
     * 
     * @return 角色列表
     */
    List<RoleInfo> getAllRoles();
    
    /**
     * 角色信息
     */
    class RoleInfo {
        private String code;
        private String name;
        
        public RoleInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
} 