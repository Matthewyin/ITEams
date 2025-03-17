package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 变更溯源实体类，记录资产信息的全部变更历史
 * <p>
 * 该实体用于追踪资产生命周期中的所有变更，包括空间变更、状态变更、维保变更等。
 * 每次变更都会记录变更前后的差异、操作人员和操作时间，保证资产数据变更的可追溯性。
 * 变更记录采用JSON格式存储差异数据，支持不同类型变更的特定数据结构。
 * </p>
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "change_trace")
public class ChangeTrace {

    /**
     * 变更记录ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long traceId;

    /**
     * 关联的资产，表示该变更属于哪个资产
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    @ToString.Exclude
    private AssetMaster asset;

    /**
     * 变更类型，如空间变更、状态变更等
     * @see ChangeType
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChangeType changeType;

    /**
     * 变更差异快照，JSON格式
     * 存储变更前后的差异数据，格式随变更类型不同而变化
     */
    @Column(columnDefinition = "json", nullable = false)
    private String deltaSnapshot;

    /**
     * 操作链路，JSON格式
     * 可选字段，用于记录复杂操作流程（如审批流）的中间状态
     */
    @Column(columnDefinition = "json")
    private String operationTree;
    
    /**
     * 操作时间，记录变更发生的时间点
     */
    @Column(nullable = false)
    private LocalDateTime operatedAt = LocalDateTime.now();
    
    /**
     * 操作人，记录执行变更的用户标识
     */
    @Column(length = 50)
    private String operatedBy;
    
    /**
     * 创建时间，由JPA自动设置
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 变更类型枚举
     * <p>
     * SPACE: 空间位置变更 - 资产物理位置的变动
     * STATUS: 状态变更 - 资产状态的改变（如从"使用中"变为"维修中"）
     * WARRANTY: 维保变更 - 资产维保信息的更新
     * PROPERTY: 属性变更 - 资产基本属性（如名称、编号等）的修改
     * OWNER: 所有者变更 - 资产归属部门或责任人的变更
     * INITIAL: 初始状态 - 资产初次录入系统时的状态记录
     * </p>
     */
    public enum ChangeType {
        SPACE, STATUS, WARRANTY, PROPERTY, OWNER, INITIAL
    }

    /**
     * 重写equals方法，用于实体比较
     * 考虑了Hibernate代理对象的情况
     *
     * @param o 比较对象
     * @return 如果ID相同则返回true，否则返回false
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ChangeTrace that = (ChangeTrace) o;
        return getTraceId() != null && Objects.equals(getTraceId(), that.getTraceId());
    }

    /**
     * 重写hashCode方法，与equals方法保持一致
     * 考虑了Hibernate代理对象
     *
     * @return 哈希码
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}