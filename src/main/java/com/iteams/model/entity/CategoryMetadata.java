package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 分类元数据实体类，存储资产分类的层级结构
 * <p>
 * 该实体实现了三级分类树状结构，每个分类有自己的级别和可选的父级分类。
 * 分类系统用于对资产进行归类和分组，支持按类别查询和统计资产信息。
 * 分类结构通常包括三个级别：
 * - 一级分类：如"服务器"、"网络设备"等大类
 * - 二级分类：如"机架式服务器"、"塔式服务器"等细分类别
 * - 三级分类：如特定型号系列等更细粒度的分类
 * </p>
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "category_metadata")
public class CategoryMetadata {
    /**
     * 分类ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    /**
     * 分类级别，1-一级分类，2-二级分类，3-三级分类
     */
    @Column(nullable = false)
    private Byte level;

    /**
     * 分类名称
     */
    @Column(length = 100, nullable = false)
    private String name;

    /**
     * 分类代码，例如"IT01"，用于简化标识
     */
    @Column(length = 4, columnDefinition = "CHAR(4)")
    private String code;

    /**
     * 父级分类，自关联
     * 一级分类的parent为null
     * 二级分类的parent指向对应的一级分类
     * 三级分类的parent指向对应的二级分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private CategoryMetadata parent;

    /**
     * 完整分类路径，非持久化字段
     * 用于展示完整的分类层次，格式如"服务器/机架式/Dell R740"
     */
    @Transient
    private String fullPath;
    
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
     * 获取完整分类路径
     * <p>
     * 递归构建分类的完整路径，从顶级分类到当前分类，
     * 各级分类名称用"/"分隔
     * </p>
     * 
     * @return 完整分类路径字符串
     */
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