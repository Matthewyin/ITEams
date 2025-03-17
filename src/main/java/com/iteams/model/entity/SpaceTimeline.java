package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 空间轨迹实体类，记录资产物理位置的完整历史
 * <p>
 * 该实体采用时间轴模式设计，不仅记录资产当前的位置信息，
 * 还保留历史位置记录，形成完整的资产空间移动轨迹。
 * 每条记录包含位置有效的时间段和详细的位置描述，
 * 支持数据中心、机房、机柜和U位的多级位置表示。
 * </p>
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "space_timeline")
public class SpaceTimeline {

    /**
     * 空间ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    /**
     * 关联的资产
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    @ToString.Exclude
    private AssetMaster asset;

    /**
     * 位置路径，格式为：数据中心/机房/机柜/U位
     * 完整的位置表示，用于快速检索和展示
     */
    @Column(length = 500, nullable = false)
    private String locationPath;

    /**
     * 纬度坐标，用于地图定位
     * 采用小数形式，支持7位小数精度
     */
    @Column(columnDefinition = "DOUBLE(10,7)")
    private Double latitude;
    
    /**
     * 经度坐标，用于地图定位
     * 采用小数形式，支持7位小数精度
     */
    @Column(columnDefinition = "DOUBLE(10,7)")
    private Double longitude;

    /**
     * 位置有效开始时间
     * 表示资产从何时开始位于该位置
     */
    @Column(nullable = false)
    private LocalDateTime validFrom;

    /**
     * 位置有效结束时间
     * 如果为null，表示当前仍在该位置
     */
    private LocalDateTime validTo;

    /**
     * 是否为当前有效位置
     * true表示该记录是资产的当前位置
     * false表示这是历史位置记录
     */
    @Column(nullable = false)
    private Boolean isCurrent = false;
    
    /**
     * 数据中心名称
     * 位置的第一级，通常是数据中心或园区的名称
     */
    @Column(length = 255)
    private String dataCenter;
    
    /**
     * 机房名称
     * 位置的第二级，通常是具体机房的标识
     */
    @Column(length = 255)
    private String roomName;
    
    /**
     * 机柜编号
     * 位置的第三级，标识具体的机柜
     */
    @Column(length = 50)
    private String cabinetNo;
    
    /**
     * U位信息
     * 位置的第四级，标识资产在机柜中的具体位置
     */
    @Column(length = 10)
    private String uPosition;
    
    /**
     * 使用环境描述
     * 如生产环境、测试环境、开发环境等
     */
    @Column(length = 100)
    private String environment;
    
    /**
     * 保管人
     * 负责管理该位置设备的人员
     */
    @Column(length = 100)
    private String keeper;
    
    /**
     * 创建时间，由JPA自动设置
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
        SpaceTimeline that = (SpaceTimeline) o;
        return getSpaceId() != null && Objects.equals(getSpaceId(), that.getSpaceId());
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