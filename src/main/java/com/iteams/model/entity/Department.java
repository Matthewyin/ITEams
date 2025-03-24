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
 * 部门实体
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "sys_department")
public class Department {

    /**
     * 部门ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门名称
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 部门代码
     */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /**
     * 部门描述
     */
    @Column(length = 255)
    private String description;

    /**
     * 父部门ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 部门级别（1为顶级部门）
     */
    @Builder.Default
    private Integer level = 1;

    /**
     * 排序号
     */
    @Builder.Default
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

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
     * 部门下的用户
     */
    @OneToMany(mappedBy = "department")
    @ToString.Exclude
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Department department = (Department) o;
        return getId() != null && Objects.equals(getId(), department.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
