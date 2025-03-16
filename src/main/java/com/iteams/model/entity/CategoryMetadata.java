package com.iteams.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "category_metadata")
public class CategoryMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private Byte level;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 4, columnDefinition = "CHAR(4)")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryMetadata parent;

    @Transient
    private String fullPath;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getFullPath() {
        if (this.fullPath != null) {
            return this.fullPath;
        }
        
        if (this.parent == null) {
            return this.name;
        } else {
            return this.parent.getFullPath() + "/" + this.name;
        }
    }
} 