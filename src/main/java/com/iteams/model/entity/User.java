package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用户实体
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "sys_user")
public class User {

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码（加密存储）
     */
    @Column(nullable = false)
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 电子邮件
     */
    @Column(length = 100)
    private String email;

    /**
     * 电话号码
     */
    @Column(length = 20)
    private String phone;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /**
     * 部门
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 账号是否未过期
     */
    @Builder.Default
    @Column(name = "account_non_expired")
    private Boolean accountNonExpired = Boolean.TRUE;

    /**
     * 账号是否未锁定
     */
    @Builder.Default
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked = Boolean.TRUE;

    /**
     * 凭证是否未过期
     */
    @Builder.Default
    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired = Boolean.TRUE;

    /**
     * 账号是否启用
     */
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 登录失败次数
     */
    @Builder.Default
    @Column(name = "login_fail_count")
    private Integer loginFailCount = 0;
    
    /**
     * 账户锁定时间
     */
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 用户角色关联
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * 用户组关联
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_user_group_mapping",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<UserGroup> groups = new HashSet<>();

    // 为与Spring Security兼容，提供以下辅助方法
    public boolean isAccountNonExpired() {
        return accountNonExpired != null && accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked != null && accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired != null && credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}