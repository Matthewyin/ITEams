package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.DepartmentDTO;
import com.iteams.model.entity.Department;
import com.iteams.repository.DepartmentRepository;
import com.iteams.repository.UserRepository;
import com.iteams.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    /**
     * 获取部门分页列表
     */
    @Override
    public Page<DepartmentDTO> getDepartments(String name, String code, Boolean enabled, Pageable pageable) {
        Page<Department> departments = departmentRepository.findByConditions(name, code, enabled, pageable);
        return departments.map(this::convertToDto);
    }

    /**
     * 获取所有部门列表
     */
    @Override
    public List<DepartmentDTO> getAllDepartments(Boolean enabled) {
        List<Department> departments;
        if (enabled != null) {
            departments = departmentRepository.findAll().stream()
                    .filter(d -> d.getEnabled().equals(enabled))
                    .collect(Collectors.toList());
        } else {
            departments = departmentRepository.findAll();
        }
        return departments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 获取部门树结构
     */
    @Override
    public List<DepartmentDTO> getDepartmentTree(Boolean enabled) {
        List<Department> allDepartments;
        if (enabled != null) {
            allDepartments = departmentRepository.findAll().stream()
                    .filter(d -> d.getEnabled().equals(enabled))
                    .collect(Collectors.toList());
        } else {
            allDepartments = departmentRepository.findAll();
        }

        // 转换为DTO
        List<DepartmentDTO> dtoList = allDepartments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 构建树结构
        Map<Long, DepartmentDTO> dtoMap = new HashMap<>();
        List<DepartmentDTO> rootDepartments = new ArrayList<>();

        // 将所有部门放入Map
        for (DepartmentDTO dto : dtoList) {
            dtoMap.put(dto.getId(), dto);
        }

        // 构建树结构
        for (DepartmentDTO dto : dtoList) {
            if (dto.getParentId() == null) {
                // 根部门
                rootDepartments.add(dto);
            } else {
                // 子部门
                DepartmentDTO parentDto = dtoMap.get(dto.getParentId());
                if (parentDto != null) {
                    // 设置父部门名称
                    dto.setParentName(parentDto.getName());
                }
            }
        }

        return rootDepartments;
    }

    /**
     * 根据ID获取部门
     */
    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + id));
        return convertToDto(department);
    }

    /**
     * 创建部门
     */
    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        // 检查部门名称和代码是否已存在
        if (departmentRepository.existsByName(departmentDTO.getName())) {
            throw new IllegalArgumentException("部门名称已存在: " + departmentDTO.getName());
        }
        if (departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new IllegalArgumentException("部门代码已存在: " + departmentDTO.getCode());
        }

        // 检查父部门是否存在
        if (departmentDTO.getParentId() != null) {
            departmentRepository.findById(departmentDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父部门不存在: " + departmentDTO.getParentId()));
        }

        // 创建部门
        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setDescription(departmentDTO.getDescription());
        department.setParentId(departmentDTO.getParentId());
        department.setLevel(departmentDTO.getLevel() != null ? departmentDTO.getLevel() : 1);
        department.setSortOrder(departmentDTO.getSortOrder() != null ? departmentDTO.getSortOrder() : 0);
        department.setEnabled(departmentDTO.getEnabled() != null ? departmentDTO.getEnabled() : true);
        department.setCreatedAt(LocalDateTime.now());

        // 保存部门
        Department savedDepartment = departmentRepository.save(department);
        return convertToDto(savedDepartment);
    }

    /**
     * 更新部门
     */
    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + id));

        // 检查部门名称和代码是否已被其他部门使用
        if (!department.getName().equals(departmentDTO.getName()) && 
                departmentRepository.existsByName(departmentDTO.getName())) {
            throw new IllegalArgumentException("部门名称已存在: " + departmentDTO.getName());
        }
        if (!department.getCode().equals(departmentDTO.getCode()) && 
                departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new IllegalArgumentException("部门代码已存在: " + departmentDTO.getCode());
        }

        // 检查父部门是否存在，并防止循环引用
        if (departmentDTO.getParentId() != null) {
            if (departmentDTO.getParentId().equals(id)) {
                throw new IllegalArgumentException("部门不能将自己设为父部门");
            }
            departmentRepository.findById(departmentDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父部门不存在: " + departmentDTO.getParentId()));
        }

        // 更新部门信息
        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setDescription(departmentDTO.getDescription());
        department.setParentId(departmentDTO.getParentId());
        if (departmentDTO.getLevel() != null) {
            department.setLevel(departmentDTO.getLevel());
        }
        if (departmentDTO.getSortOrder() != null) {
            department.setSortOrder(departmentDTO.getSortOrder());
        }
        if (departmentDTO.getEnabled() != null) {
            department.setEnabled(departmentDTO.getEnabled());
        }
        department.setUpdatedAt(LocalDateTime.now());

        // 保存部门
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDto(updatedDepartment);
    }

    /**
     * 删除部门
     */
    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + id));

        // 检查是否有子部门
        List<Department> children = departmentRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("无法删除有子部门的部门，请先删除子部门");
        }

        // 检查是否有用户关联
        long userCount = userRepository.countByDepartmentId(id);
        if (userCount > 0) {
            throw new IllegalArgumentException("无法删除有用户关联的部门，请先移除用户关联");
        }

        // 删除部门
        departmentRepository.delete(department);
    }

    /**
     * 启用部门
     */
    @Override
    @Transactional
    public DepartmentDTO enableDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + id));
        department.setEnabled(true);
        department.setUpdatedAt(LocalDateTime.now());
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDto(updatedDepartment);
    }

    /**
     * 禁用部门
     */
    @Override
    @Transactional
    public DepartmentDTO disableDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + id));
        department.setEnabled(false);
        department.setUpdatedAt(LocalDateTime.now());
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDto(updatedDepartment);
    }

    /**
     * 批量删除部门
     */
    @Override
    @Transactional
    public void batchDeleteDepartments(List<Long> ids) {
        for (Long id : ids) {
            deleteDepartment(id);
        }
    }

    /**
     * 批量启用部门
     */
    @Override
    @Transactional
    public void batchEnableDepartments(List<Long> ids) {
        List<Department> departments = departmentRepository.findAllById(ids);
        for (Department department : departments) {
            department.setEnabled(true);
            department.setUpdatedAt(LocalDateTime.now());
        }
        departmentRepository.saveAll(departments);
    }

    /**
     * 批量禁用部门
     */
    @Override
    @Transactional
    public void batchDisableDepartments(List<Long> ids) {
        List<Department> departments = departmentRepository.findAllById(ids);
        for (Department department : departments) {
            department.setEnabled(false);
            department.setUpdatedAt(LocalDateTime.now());
        }
        departmentRepository.saveAll(departments);
    }

    /**
     * 转换部门实体为DTO
     */
    private DepartmentDTO convertToDto(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setParentId(department.getParentId());
        dto.setLevel(department.getLevel());
        dto.setSortOrder(department.getSortOrder());
        dto.setEnabled(department.getEnabled());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());
        
        // 设置用户数量
        dto.setUserCount((int) userRepository.countByDepartmentId(department.getId()));
        
        // 如果有父部门，设置父部门名称
        if (department.getParentId() != null) {
            departmentRepository.findById(department.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getName()));
        }
        
        return dto;
    }
}
