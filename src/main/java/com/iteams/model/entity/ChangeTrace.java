package com.iteams.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "change_trace")
public class ChangeTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long traceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private AssetMaster asset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChangeType changeType;

    @Column(columnDefinition = "json", nullable = false)
    private String deltaSnapshot;

    @Column(columnDefinition = "json")
    private String operationTree;
    
    @Column(nullable = false)
    private LocalDateTime operatedAt = LocalDateTime.now();
    
    @Column(length = 50)
    private String operatedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ChangeType {
        SPACE, STATUS, WARRANTY, PROPERTY, OWNER, INITIAL
    }
} 