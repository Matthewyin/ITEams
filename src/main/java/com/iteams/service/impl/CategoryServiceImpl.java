package com.iteams.service.impl;

import com.iteams.model.entity.CategoryMetadata;
import com.iteams.repository.CategoryRepository;
import com.iteams.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分类服务实现类
 * <p>
 * 该类实现了{@link CategoryService}接口定义的分类服务功能，提供对IT资产
 * 三级分类体系的验证操作。主要负责验证分类ID和分类名称对应的分类路径是否有效。
 * </p>
 * <p>
 * 分类路径验证规则包括：
 * <ul>
 *   <li>检查各级分类是否存在</li>
 *   <li>验证各级分类的层级关系是否正确</li>
 *   <li>确认上下级分类的父子关系是否匹配</li>
 * </ul>
 * </p>
 * <p>
 * 主要应用场景：
 * <ul>
 *   <li>资产创建和编辑时的分类验证</li>
 *   <li>Excel导入时的分类数据校验</li>
 *   <li>分类树构建和展示</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    /**
     * 分类数据访问对象
     */
    private final CategoryRepository categoryRepository;

    /**
     * 构造函数，通过依赖注入获取CategoryRepository实例
     * 
     * @param categoryRepository 分类数据访问对象
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>首先验证一级分类ID是否存在且层级为1</li>
     *   <li>如果没有提供二级分类ID，则认为只使用一级分类，返回true</li>
     *   <li>验证二级分类是否存在且其父级ID是否等于提供的一级分类ID</li>
     *   <li>如果没有提供三级分类ID，则认为只使用一、二级分类，返回true</li>
     *   <li>验证三级分类是否存在且其父级ID是否等于提供的二级分类ID</li>
     * </ol>
     * </p>
     * <p>
     * 该方法使用了只读事务，不会修改数据库内容。
     * </p>
     */
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
    
    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>检查输入的分类名称是否为空</li>
     *   <li>处理特殊情况：如果一级分类是"未分类"，则直接允许</li>
     *   <li>根据名称查找对应的分类实体</li>
     *   <li>检查分类是否存在并验证父子关系是否正确</li>
     *   <li>层层验证，允许只使用部分层级（如只用一级分类或一二级分类）</li>
     * </ol>
     * </p>
     * <p>
     * 该方法使用了只读事务，日志记录了验证失败的原因，便于调试和问题排查。
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validateCategoryPathByName(String level1Name, String level2Name, String level3Name) {
        // 如果一级分类为空，则路径无效，但允许未分类
        if (level1Name == null || level1Name.trim().isEmpty()) {
            log.warn("一级分类为空，将使用默认分类");
            return false;
        }
        
        // 如果一级分类是 "未分类"，直接允许
        if ("未分类".equals(level1Name.trim())) {
            return true;
        }
        
        // 查找一级分类
        CategoryMetadata level1 = categoryRepository.findByNameAndLevel(level1Name, (byte) 1)
                .orElse(null);
        if (level1 == null) {
            log.warn("一级分类不存在: {}", level1Name);
            return false;
        }
        
        // 如果没有二级分类名称，则路径有效（只使用一级分类）
        if (level2Name == null || level2Name.trim().isEmpty()) {
            return true;
        }
        
        // 查找二级分类
        CategoryMetadata level2 = categoryRepository.findByNameAndLevel(level2Name, (byte) 2)
                .orElse(null);
        if (level2 == null) {
            log.warn("二级分类不存在: {}", level2Name);
            return false;
        }
        
        // 验证二级分类的父级是否是指定的一级分类
        if (level2.getParent() == null || !level2.getParent().getCategoryId().equals(level1.getCategoryId())) {
            log.warn("二级分类 {} 的父级不是 {}", level2Name, level1Name);
            return false;
        }
        
        // 如果没有三级分类名称，则路径有效（只使用一级和二级分类）
        if (level3Name == null || level3Name.trim().isEmpty()) {
            return true;
        }
        
        // 查找三级分类
        CategoryMetadata level3 = categoryRepository.findByNameAndLevel(level3Name, (byte) 3)
                .orElse(null);
        if (level3 == null) {
            log.warn("三级分类不存在: {}", level3Name);
            return false;
        }
        
        // 验证三级分类的父级是否是指定的二级分类
        if (level3.getParent() == null || !level3.getParent().getCategoryId().equals(level2.getCategoryId())) {
            log.warn("三级分类 {} 的父级不是 {}", level3Name, level2Name);
            return false;
        }
        
        return true;
    }
} 