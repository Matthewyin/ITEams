package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 维保合约实体类，管理资产的保修和维护信息
 * <p>
 * 该实体存储IT资产的维保信息，包括合同编号、维保时间范围、服务级别等。
 * 每个资产可以有多个维保记录，但同一时间只有一个处于激活状态的维保合约。
 * 系统会根据维保结束日期自动判断维保状态，并可记录提供商和服务内容等相关信息。
 * </p>
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "warranty_contract")
public class WarrantyContract {

    /**
     * 维保ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warrantyId;

    /**
     * 关联的资产
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    @ToString.Exclude
    private AssetMaster asset;

    /**
     * 合同编号，唯一索引
     * 维保合同的业务编号，必须唯一
     */
    @Column(length = 50, nullable = false, unique = true)
    private String contractNo;

    /**
     * 维保开始日期
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * 维保结束日期
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * 服务级别
     * 1-基础级别，2-高级级别，3-专属级别
     */
    @Column(nullable = false)
    private Byte providerLevel;

    /**
     * 服务范围，JSON格式
     * 详细描述维保服务包含的内容和范围
     */
    @Column(columnDefinition = "json")
    private String serviceScope;
    
    /**
     * 是否当前有效
     * true表示当前生效的维保合约
     * false表示历史维保记录
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * 维保提供商
     * 提供维保服务的公司或组织名称
     */
    @Column(length = 100)
    private String provider;
    
    /**
     * 维保状态
     * 如"有效"、"即将到期"、"已过期"等
     */
    @Column(length = 20)
    private String warrantyStatus;
    
    /**
     * 资产预计使用年限
     * 表示资产的计划使用寿命，单位为年
     */
    @Column(nullable = false)
    private Integer assetLifeYears;
    
    /**
     * 到货验收日期
     * 资产初始交付并验收合格的日期，用于计算使用寿命
     */
    @Column
    private LocalDate acceptanceDate;
    
    /**
     * 创建时间，由JPA自动设置
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间，由JPA自动更新
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
        WarrantyContract that = (WarrantyContract) o;
        return getWarrantyId() != null && Objects.equals(getWarrantyId(), that.getWarrantyId());
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