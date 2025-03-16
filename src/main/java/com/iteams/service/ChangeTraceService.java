package com.iteams.service;

import com.iteams.model.entity.ChangeTrace;

/**
 * 变更溯源服务接口
 */
public interface ChangeTraceService {

    /**
     * 保存变更记录
     * 
     * @param changeTrace 变更记录
     * @return 保存后的变更记录
     */
    ChangeTrace saveChange(ChangeTrace changeTrace);
} 