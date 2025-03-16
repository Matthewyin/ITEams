package io.nssa.iteams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "change_trace")
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long traceId;
    
    @Column(nullable = false)
    private Long assetId;
    
    @Column(nullable = false, length = 50)
    private String changeType;
    
    @Column(columnDefinition = "json")
    private String changeDetails;
    
    @Column(nullable = false)
    private Long operatorId;
    
    @Column(nullable = false)
    private LocalDateTime operateTime;
    
    @Column(length = 500)
    private String remarks;
}