package com.iteams.model.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "asset_master")
public class AssetMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    @Column(length = 36, nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private String assetUuid;

    @Column(length = 32, nullable = false, unique = true)
    private String assetNo;

    @Column(length = 255, nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus currentStatus;

    @Column(columnDefinition = "json")
    private String categoryHierarchy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    @ToString.Exclude
    private SpaceTimeline space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_id")
    @ToString.Exclude
    private WarrantyContract warranty;

    @Column(length = 64, columnDefinition = "CHAR(64)")
    private String dataFingerprint;

    @Column(length = 32)
    private String importBatch;

    @Column(length = 64, columnDefinition = "CHAR(64)")
    private String excelRowHash;

    @Column(nullable = false)
    private Integer version = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AssetStatus {
        IN_USE, INVENTORY, MAINTENANCE, RETIRED
    }

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

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}