package com.iteams.service.impl;

import com.iteams.model.entity.SpaceTimeline;
import com.iteams.repository.SpaceRepository;
import com.iteams.service.SpaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    
    public SpaceServiceImpl(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }
    
    @Override
    @Transactional
    public SpaceTimeline saveSpace(SpaceTimeline space) {
        return spaceRepository.save(space);
    }
} 