package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 权限实体
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "sys_permission")
public class Permission {

    /**
     * 权限ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限名称
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 权限编码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 权限描述
     */
    @Column(length = 200)
    private String description;

    /**
     * 权限类型（菜单、按钮等）
     */
    @Column(length = 20)
    private String type;

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
     * 角色关联
     */
    @ManyToMany(mappedBy = "permissions")
    @ToString.Exclude
    @Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Permission that = (Permission) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}