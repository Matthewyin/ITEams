package io.nssa.iteams.repository;

import io.nssa.iteams.entity.CategoryMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMetadataRepository extends JpaRepository<CategoryMetadata, Long> {
    
    List<CategoryMetadata> findByParentId(Long parentId);
    
    List<CategoryMetadata> findByLevel(Byte level);
}