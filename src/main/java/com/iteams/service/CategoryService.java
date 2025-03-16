package com.iteams.service;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 验证分类路径是否有效
     * 
     * @param level1Id 一级分类ID
     * @param level2Id 二级分类ID
     * @param level3Id 三级分类ID
     * @return 路径有效返回true，否则返回false
     */
    boolean validateCategoryPath(Long level1Id, Long level2Id, Long level3Id);
} 