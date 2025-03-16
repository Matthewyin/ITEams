package com.iteams.service.impl;

import com.iteams.model.entity.CategoryMetadata;
import com.iteams.repository.CategoryRepository;
import com.iteams.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCategoryPath(Long level1Id, Long level2Id, Long level3Id) {
        // 验证一级分类存在
        if (level1Id == null || !categoryRepository.existsByCategoryIdAndLevel(level1Id, (byte) 1)) {
            return false;
        }
        
        // 如果没有二级分类ID，则路径有效（只使用一级分类）
        if (level2Id == null) {
            return true;
        }
        
        // 验证二级分类存在并且其父级是指定的一级分类
        CategoryMetadata level2 = categoryRepository.findByCategoryIdAndLevel(level2Id, (byte) 2)
                .orElse(null);
        if (level2 == null || level2.getParent() == null || !level2.getParent().getCategoryId().equals(level1Id)) {
            return false;
        }
        
        // 如果没有三级分类ID，则路径有效（只使用一级和二级分类）
        if (level3Id == null) {
            return true;
        }
        
        // 验证三级分类存在并且其父级是指定的二级分类
        CategoryMetadata level3 = categoryRepository.findByCategoryIdAndLevel(level3Id, (byte) 3)
                .orElse(null);
        return level3 != null && level3.getParent() != null && level3.getParent().getCategoryId().equals(level2Id);
    }
} 