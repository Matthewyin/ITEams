package com.iteams.repository;

import com.iteams.model.entity.CategoryMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryMetadata, Long> {
    
    List<CategoryMetadata> findByLevel(Byte level);
    
    List<CategoryMetadata> findByParentCategoryId(Long parentId);
    
    Optional<CategoryMetadata> findByCategoryIdAndLevel(Long categoryId, Byte level);
    
    @Query("SELECT COUNT(c) > 0 FROM CategoryMetadata c WHERE c.categoryId = :categoryId AND c.level = :level")
    boolean existsByCategoryIdAndLevel(@Param("categoryId") Long categoryId, @Param("level") Byte level);
    
    /**
     * 通过分类名称和级别查找分类
     * 
     * @param name 分类名称
     * @param level 分类级别
     * @return 分类对象（如果存在）
     */
    Optional<CategoryMetadata> findByNameAndLevel(String name, Byte level);
} 