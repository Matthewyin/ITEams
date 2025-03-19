package com.iteams.repository;

import com.iteams.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户仓库接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);

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
     * 更新用户最后登录时间
     *
     * @param username 用户名
     * @param lastLoginTime 最后登录时间
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :lastLoginTime WHERE u.username = :username")
    int updateLastLoginTime(String username, LocalDateTime lastLoginTime);
}
