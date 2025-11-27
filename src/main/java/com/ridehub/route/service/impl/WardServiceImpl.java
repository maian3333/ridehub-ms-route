package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Ward;
import com.ridehub.route.repository.WardRepository;
import com.ridehub.route.service.WardService;
import com.ridehub.route.service.dto.WardDTO;
import com.ridehub.route.service.mapper.WardMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Ward}.
 */
@Service
@Transactional
public class WardServiceImpl implements WardService {

    private static final Logger LOG = LoggerFactory.getLogger(WardServiceImpl.class);

    private final WardRepository wardRepository;

    private final WardMapper wardMapper;

    public WardServiceImpl(WardRepository wardRepository, WardMapper wardMapper) {
        this.wardRepository = wardRepository;
        this.wardMapper = wardMapper;
    }

    @Override
    public WardDTO save(WardDTO wardDTO) {
        LOG.debug("Request to save Ward : {}", wardDTO);
        Ward ward = wardMapper.toEntity(wardDTO);
        ward = wardRepository.save(ward);
        return wardMapper.toDto(ward);
    }

    @Override
    public WardDTO update(WardDTO wardDTO) {
        LOG.debug("Request to update Ward : {}", wardDTO);
        Ward ward = wardMapper.toEntity(wardDTO);
        ward = wardRepository.save(ward);
        return wardMapper.toDto(ward);
    }

    @Override
    public Optional<WardDTO> partialUpdate(WardDTO wardDTO) {
        LOG.debug("Request to partially update Ward : {}", wardDTO);

        return wardRepository
            .findById(wardDTO.getId())
            .map(existingWard -> {
                wardMapper.partialUpdate(existingWard, wardDTO);

                return existingWard;
            })
            .map(wardRepository::save)
            .map(wardMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WardDTO> findOne(Long id) {
        LOG.debug("Request to get Ward : {}", id);
        return wardRepository.findById(id).map(wardMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Ward : {}", id);
        wardRepository.deleteById(id);
    }
}
