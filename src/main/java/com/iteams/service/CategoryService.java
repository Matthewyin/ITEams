package com.iteams.service;

/**
 * 分类服务接口
 * <p>
 * 该服务负责IT资产分类体系的管理和验证，提供对三级分类结构的操作能力。
 * 分类体系是资产管理的基础，用于对资产进行科学分类和编码管理。系统采用三级分类结构
 * （一级分类 -> 二级分类 -> 三级分类），例如：
 * IT设备 -> 服务器设备 -> 机架式服务器
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>分类树的构建与管理</li>
 *   <li>分类路径的验证与查询</li>
 *   <li>分类编码的管理与分配</li>
 * </ul>
 * </p>
 */
public interface CategoryService {

    /**
     * 验证分类路径是否有效
     * <p>
     * 根据分类ID进行验证，检查给定的三级分类ID是否形成有效的分类路径。
     * 验证规则包括：
     * <ul>
     *   <li>检查各级分类ID是否存在</li>
     *   <li>确认二级分类的父ID是否为给定的一级分类</li>
     *   <li>确认三级分类的父ID是否为给定的二级分类</li>
     * </ul>
     * </p>
     * 
     * @param level1Id 一级分类ID，不能为null
     * @param level2Id 二级分类ID，不能为null
     * @param level3Id 三级分类ID，不能为null
     * @return 路径有效返回true，否则返回false
     * @throws IllegalArgumentException 如果任何参数为null
     */
    boolean validateCategoryPath(Long level1Id, Long level2Id, Long level3Id);
    
    /**
     * 验证字符串形式的分类路径是否有效
     * <p>
     * 根据分类名称进行验证，将分类名称转换为对应的ID后，调用ID验证方法进行验证。
     * 此方法主要用于处理从Excel导入的分类数据，其中分类通常以名称形式提供。
     * </p>
     * <p>
     * 验证过程：
     * <ol>
     *   <li>根据名称查找对应的分类实体</li>
     *   <li>检查分类层级关系是否正确</li>
     *   <li>验证分类是否形成有效路径</li>
     * </ol>
     * </p>
     * 
     * @param level1Name 一级分类名称，不能为null或空
     * @param level2Name 二级分类名称，不能为null或空
     * @param level3Name 三级分类名称，不能为null或空
     * @return 路径有效返回true，否则返回false
     * @throws IllegalArgumentException 如果任何参数为null或空字符串
     */
    boolean validateCategoryPathByName(String level1Name, String level2Name, String level3Name);
} 