package com.iteams.service;

import com.iteams.model.dto.DepartmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService {

    /**
     * 获取部门分页列表
     *
     * @param name 部门名称（模糊查询）
     * @param code 部门代码（模糊查询）
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 部门分页列表
     */
    Page<DepartmentDTO> getDepartments(String name, String code, Boolean enabled, Pageable pageable);

    /**
     * 获取所有部门列表
     *
     * @param enabled 启用状态，为null时获取全部
     * @return 部门列表
     */
    List<DepartmentDTO> getAllDepartments(Boolean enabled);

    /**
     * 获取部门树结构
     *
     * @param enabled 启用状态，为null时获取全部
     * @return 部门树结构
     */
    List<DepartmentDTO> getDepartmentTree(Boolean enabled);

    /**
     * 根据ID获取部门
     *
     * @param id 部门ID
     * @return 部门
     */
    DepartmentDTO getDepartmentById(Long id);

    /**
     * 创建部门
     *
     * @param departmentDTO 部门DTO
     * @return 创建后的部门
     */
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    /**
     * 更新部门
     *
     * @param id 部门ID
     * @param departmentDTO 部门DTO
     * @return 更新后的部门
     */
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 启用部门
     *
     * @param id 部门ID
     * @return 启用后的部门
     */
    DepartmentDTO enableDepartment(Long id);

    /**
     * 禁用部门
     *
     * @param id 部门ID
     * @return 禁用后的部门
     */
    DepartmentDTO disableDepartment(Long id);

    /**
     * 批量删除部门
     *
     * @param ids 部门ID列表
     */
    void batchDeleteDepartments(List<Long> ids);

    /**
     * 批量启用部门
     *
     * @param ids 部门ID列表
     */
    void batchEnableDepartments(List<Long> ids);

    /**
     * 批量禁用部门
     *
     * @param ids 部门ID列表
     */
    void batchDisableDepartments(List<Long> ids);
}
