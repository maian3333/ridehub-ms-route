package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Floor;
import com.ridehub.route.repository.FloorRepository;
import com.ridehub.route.service.FloorService;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.mapper.FloorMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Floor}.
 */
@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    private static final Logger LOG = LoggerFactory.getLogger(FloorServiceImpl.class);

    private final FloorRepository floorRepository;

    private final FloorMapper floorMapper;

    public FloorServiceImpl(FloorRepository floorRepository, FloorMapper floorMapper) {
        this.floorRepository = floorRepository;
        this.floorMapper = floorMapper;
    }

    @Override
    public FloorDTO save(FloorDTO floorDTO) {
        LOG.debug("Request to save Floor : {}", floorDTO);
        Floor floor = floorMapper.toEntity(floorDTO);
        floor = floorRepository.save(floor);
        return floorMapper.toDto(floor);
    }

    @Override
    public FloorDTO update(FloorDTO floorDTO) {
        LOG.debug("Request to update Floor : {}", floorDTO);
        Floor floor = floorMapper.toEntity(floorDTO);
        floor = floorRepository.save(floor);
        return floorMapper.toDto(floor);
    }

    @Override
    public Optional<FloorDTO> partialUpdate(FloorDTO floorDTO) {
        LOG.debug("Request to partially update Floor : {}", floorDTO);

        return floorRepository
            .findById(floorDTO.getId())
            .map(existingFloor -> {
                floorMapper.partialUpdate(existingFloor, floorDTO);

                return existingFloor;
            })
            .map(floorRepository::save)
            .map(floorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FloorDTO> findOne(Long id) {
        LOG.debug("Request to get Floor : {}", id);
        return floorRepository.findById(id).map(floorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Floor : {}", id);
        floorRepository.deleteById(id);
    }
}
