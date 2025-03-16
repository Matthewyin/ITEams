package com.iteams.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "warranty_contract")
public class WarrantyContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warrantyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private AssetMaster asset;

    @Column(length = 50, nullable = false, unique = true)
    private String contractNo;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Byte providerLevel;

    @Column(columnDefinition = "json")
    private String serviceScope;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(length = 100)
    private String provider;
    
    @Column(length = 20)
    private String warrantyStatus;
    
    // 追加字段
    @Column(nullable = false)
    private Integer assetLifeYears;
    
    @Column
    private LocalDate acceptanceDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 