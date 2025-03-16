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
} 