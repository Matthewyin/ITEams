package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 资产主实体类，存储IT资产的核心信息
 * <p>
 * 该实体是系统的核心数据模型，存储资产的基本属性、分类、空间位置和维保信息。
 * 每个资产记录拥有唯一的编号和UUID，可通过这些标识符进行查询和跟踪。
 * 同时，该实体通过关联关系连接空间和维保信息，形成完整的资产数据模型。
 * </p>
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "asset_master")
public class AssetMaster {

    /**
     * 资产ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    /**
     * 资产UUID，业务层面的唯一标识符，用于跨系统引用
     * 格式为标准UUID字符串
     */
    @Column(length = 36, nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private String assetUuid;

    /**
     * 资产编号，业务编码，必须唯一
     * 通常按照公司编码规则生成的资产编号
     */
    @Column(length = 32, nullable = false, unique = true)
    private String assetNo;

    /**
     * 资产名称
     */
    @Column(nullable = false)
    private String assetName;

    /**
     * 当前状态，枚举类型：使用中、库存中、维修中、已报废
     * @see AssetStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus currentStatus;

    /**
     * 分类层级JSON，存储一级、二级、三级分类信息
     * 格式为：{"l1":"服务器","l2":"机架式","l3":"刀片服务器"}
     */
    @Column(columnDefinition = "json")
    private String categoryHierarchy;

    /**
     * 当前空间位置，关联到SpaceTimeline实体
     * 该关联代表资产当前所处的物理位置
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    @ToString.Exclude
    private SpaceTimeline space;

    /**
     * 当前有效维保合约，关联到WarrantyContract实体
     * 该关联代表资产当前的有效维保信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_id")
    @ToString.Exclude
    private WarrantyContract warranty;

    /**
     * 数据指纹，用于检测重复数据
     * 基于关键字段计算的SHA-256哈希值
     */
    @Column(length = 64, columnDefinition = "CHAR(64)")
    private String dataFingerprint;

    /**
     * 导入批次号，标识资产的导入来源
     * 用于关联同一批次导入的资产
     */
    @Column(length = 32)
    private String importBatch;

    /**
     * Excel导入行哈希值，用于防止重复导入
     * 基于Excel行原始数据计算的SHA-256哈希值
     */
    @Column(length = 64, columnDefinition = "CHAR(64)")
    private String excelRowHash;

    /**
     * 版本号，用于乐观锁控制
     * 每次更新资产信息时自动增加
     */
    @Column(nullable = false)
    private Integer version = 0;

    /**
     * 创建时间，由JPA自动设置
     * 记录资产记录的创建时间戳
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间，由JPA自动更新
     * 记录资产记录的最后更新时间戳
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 资产状态枚举
     * <p>
     * IN_USE: 使用中 - 资产已分配并正在使用
     * INVENTORY: 库存中 - 资产在库存中，未分配
     * MAINTENANCE: 维修中 - 资产正在维修
     * RETIRED: 已报废 - 资产已经报废，不再使用
     * </p>
     */
    public enum AssetStatus {
        IN_USE, INVENTORY, MAINTENANCE, RETIRED
    }

    /**
     * 重写equals方法，用于实体比较
     * 该实现考虑了Hibernate代理对象，确保正确比较代理和非代理实例
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
        AssetMaster that = (AssetMaster) o;
        return getAssetId() != null && Objects.equals(getAssetId(), that.getAssetId());
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