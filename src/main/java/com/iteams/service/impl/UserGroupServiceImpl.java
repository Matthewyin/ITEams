package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.UserGroupDTO;
import com.iteams.model.entity.User;
import com.iteams.model.entity.UserGroup;
import com.iteams.repository.UserGroupRepository;
import com.iteams.repository.UserRepository;
import com.iteams.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户组服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    /**
     * 获取用户组分页列表
     */
    @Override
    public Page<UserGroupDTO> getUserGroups(String name, String code, Boolean enabled, Pageable pageable) {
        Page<UserGroup> userGroups = userGroupRepository.findByConditions(name, code, enabled, pageable);
        return userGroups.map(this::convertToDto);
    }

    /**
     * 获取所有用户组列表
     */
    @Override
    public List<UserGroupDTO> getAllUserGroups(Boolean enabled) {
        List<UserGroup> userGroups;
        if (enabled != null) {
            userGroups = userGroupRepository.findByEnabled(enabled);
        } else {
            userGroups = userGroupRepository.findAll();
        }
        return userGroups.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 根据ID获取用户组
     */
    @Override
    public UserGroupDTO getUserGroupById(Long id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + id));
        return convertToDto(userGroup);
    }

    /**
     * 创建用户组
     */
    @Override
    @Transactional
    public UserGroupDTO createUserGroup(UserGroupDTO userGroupDTO) {
        // 检查用户组名称和代码是否已存在
        if (userGroupRepository.existsByName(userGroupDTO.getName())) {
            throw new IllegalArgumentException("用户组名称已存在: " + userGroupDTO.getName());
        }
        if (userGroupRepository.existsByCode(userGroupDTO.getCode())) {
            throw new IllegalArgumentException("用户组代码已存在: " + userGroupDTO.getCode());
        }

        // 创建用户组
        UserGroup userGroup = new UserGroup();
        userGroup.setName(userGroupDTO.getName());
        userGroup.setCode(userGroupDTO.getCode());
        userGroup.setDescription(userGroupDTO.getDescription());
        userGroup.setEnabled(userGroupDTO.getEnabled() != null ? userGroupDTO.getEnabled() : true);
        userGroup.setCreatedAt(LocalDateTime.now());

        // 保存用户组
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);
        return convertToDto(savedUserGroup);
    }

    /**
     * 更新用户组
     */
    @Override
    @Transactional
    public UserGroupDTO updateUserGroup(Long id, UserGroupDTO userGroupDTO) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + id));

        // 检查用户组名称和代码是否已被其他用户组使用
        if (!userGroup.getName().equals(userGroupDTO.getName()) && 
                userGroupRepository.existsByName(userGroupDTO.getName())) {
            throw new IllegalArgumentException("用户组名称已存在: " + userGroupDTO.getName());
        }
        if (!userGroup.getCode().equals(userGroupDTO.getCode()) && 
                userGroupRepository.existsByCode(userGroupDTO.getCode())) {
            throw new IllegalArgumentException("用户组代码已存在: " + userGroupDTO.getCode());
        }

        // 更新用户组信息
        userGroup.setName(userGroupDTO.getName());
        userGroup.setCode(userGroupDTO.getCode());
        userGroup.setDescription(userGroupDTO.getDescription());
        if (userGroupDTO.getEnabled() != null) {
            userGroup.setEnabled(userGroupDTO.getEnabled());
        }
        userGroup.setUpdatedAt(LocalDateTime.now());

        // 保存用户组
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        return convertToDto(updatedUserGroup);
    }

    /**
     * 删除用户组
     */
    @Override
    @Transactional
    public void deleteUserGroup(Long id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + id));

        // 检查是否有用户关联
        long userCount = userRepository.countByGroupId(id);
        if (userCount > 0) {
            throw new IllegalArgumentException("无法删除有用户关联的用户组，请先移除用户关联");
        }

        // 删除用户组
        userGroupRepository.delete(userGroup);
    }

    /**
     * 启用用户组
     */
    @Override
    @Transactional
    public UserGroupDTO enableUserGroup(Long id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + id));
        userGroup.setEnabled(true);
        userGroup.setUpdatedAt(LocalDateTime.now());
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        return convertToDto(updatedUserGroup);
    }

    /**
     * 禁用用户组
     */
    @Override
    @Transactional
    public UserGroupDTO disableUserGroup(Long id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + id));
        userGroup.setEnabled(false);
        userGroup.setUpdatedAt(LocalDateTime.now());
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        return convertToDto(updatedUserGroup);
    }

    /**
     * 批量删除用户组
     */
    @Override
    @Transactional
    public void batchDeleteUserGroups(List<Long> ids) {
        for (Long id : ids) {
            deleteUserGroup(id);
        }
    }

    /**
     * 批量启用用户组
     */
    @Override
    @Transactional
    public void batchEnableUserGroups(List<Long> ids) {
        List<UserGroup> userGroups = userGroupRepository.findAllById(ids);
        for (UserGroup userGroup : userGroups) {
            userGroup.setEnabled(true);
            userGroup.setUpdatedAt(LocalDateTime.now());
        }
        userGroupRepository.saveAll(userGroups);
    }

    /**
     * 批量禁用用户组
     */
    @Override
    @Transactional
    public void batchDisableUserGroups(List<Long> ids) {
        List<UserGroup> userGroups = userGroupRepository.findAllById(ids);
        for (UserGroup userGroup : userGroups) {
            userGroup.setEnabled(false);
            userGroup.setUpdatedAt(LocalDateTime.now());
        }
        userGroupRepository.saveAll(userGroups);
    }

    /**
     * 为用户组分配用户
     */
    @Override
    @Transactional
    public UserGroupDTO assignUsers(Long groupId, List<Long> userIds) {
        UserGroup userGroup = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + groupId));
        
        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("未找到指定用户");
        }
        
        // 添加用户到用户组
        Set<User> currentUsers = userGroup.getUsers();
        if (currentUsers == null) {
            currentUsers = new HashSet<>();
        }
        currentUsers.addAll(users);
        userGroup.setUsers(currentUsers);
        
        // 更新用户组
        userGroup.setUpdatedAt(LocalDateTime.now());
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        
        return convertToDto(updatedUserGroup);
    }

    /**
     * 从用户组移除用户
     */
    @Override
    @Transactional
    public UserGroupDTO removeUsers(Long groupId, List<Long> userIds) {
        UserGroup userGroup = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("用户组不存在: " + groupId));
        
        // 获取当前用户组的用户
        Set<User> currentUsers = userGroup.getUsers();
        if (currentUsers == null || currentUsers.isEmpty()) {
            return convertToDto(userGroup);
        }
        
        // 移除指定用户
        currentUsers.removeIf(user -> userIds.contains(user.getId()));
        userGroup.setUsers(currentUsers);
        
        // 更新用户组
        userGroup.setUpdatedAt(LocalDateTime.now());
        UserGroup updatedUserGroup = userGroupRepository.save(userGroup);
        
        return convertToDto(updatedUserGroup);
    }

    /**
     * 转换用户组实体为DTO
     */
    private UserGroupDTO convertToDto(UserGroup userGroup) {
        UserGroupDTO dto = new UserGroupDTO();
        dto.setId(userGroup.getId());
        dto.setName(userGroup.getName());
        dto.setCode(userGroup.getCode());
        dto.setDescription(userGroup.getDescription());
        dto.setEnabled(userGroup.getEnabled());
        dto.setCreatedAt(userGroup.getCreatedAt());
        dto.setUpdatedAt(userGroup.getUpdatedAt());
        
        // 设置用户数量
        if (userGroup.getUsers() != null) {
            dto.setUserCount(userGroup.getUsers().size());
        } else {
            dto.setUserCount(0);
        }
        
        return dto;
    }
}
