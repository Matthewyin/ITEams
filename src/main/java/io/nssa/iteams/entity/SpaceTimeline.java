package io.nssa.iteams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "space_timeline")
@NoArgsConstructor
@AllArgsConstructor
public class SpaceTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;
    
    @Column(nullable = false)
    private Long assetId;
    
    @Column(length = 500)
    private String locationPath;
    
    // 坐标可以使用JPA规范的方式表示，但真正的空间索引需要数据库支持
    @Column
    private Double latitude;
    
    @Column
    private Double longitude;
    
    @Column
    private LocalDateTime validFrom;
    
    @Column
    private LocalDateTime validTo;
}