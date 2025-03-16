package com.iteams.service;

import com.iteams.model.entity.SpaceTimeline;

/**
 * 空间轨迹服务接口
 */
public interface SpaceService {

    /**
     * 保存空间记录
     * 
     * @param space 空间轨迹记录
     * @return 保存后的空间轨迹记录
     */
    SpaceTimeline saveSpace(SpaceTimeline space);
} 