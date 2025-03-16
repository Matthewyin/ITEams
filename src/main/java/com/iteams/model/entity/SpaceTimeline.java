package com.iteams.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "space_timeline")
public class SpaceTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private AssetMaster asset;

    @Column(length = 500, nullable = false)
    private String locationPath;

    // 我们使用两个经纬度字段代替POINT类型，简化实现
    @Column(precision = 10, scale = 7)
    private Double latitude;
    
    @Column(precision = 10, scale = 7)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @Column(nullable = false)
    private Boolean isCurrent = false;
    
    // 补充额外信息字段，方便查询
    @Column(length = 255)
    private String dataCenter;
    
    @Column(length = 255)
    private String roomName;
    
    @Column(length = 50)
    private String cabinetNo;
    
    @Column(length = 10)
    private String uPosition;
    
    @Column(length = 100)
    private String environment;
    
    @Column(length = 100)
    private String keeper;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 