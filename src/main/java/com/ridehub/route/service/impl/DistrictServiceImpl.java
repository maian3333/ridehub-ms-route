package com.ridehub.route.service.impl;

import com.ridehub.route.domain.District;
import com.ridehub.route.repository.DistrictRepository;
import com.ridehub.route.service.DistrictService;
import com.ridehub.route.service.dto.DistrictDTO;
import com.ridehub.route.service.mapper.DistrictMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.District}.
 */
@Service
@Transactional
public class DistrictServiceImpl implements DistrictService {

    private static final Logger LOG = LoggerFactory.getLogger(DistrictServiceImpl.class);

    private final DistrictRepository districtRepository;

    private final DistrictMapper districtMapper;

    public DistrictServiceImpl(DistrictRepository districtRepository, DistrictMapper districtMapper) {
        this.districtRepository = districtRepository;
        this.districtMapper = districtMapper;
    }

    @Override
    public DistrictDTO save(DistrictDTO districtDTO) {
        LOG.debug("Request to save District : {}", districtDTO);
        District district = districtMapper.toEntity(districtDTO);
        district = districtRepository.save(district);
        return districtMapper.toDto(district);
    }

    @Override
    public DistrictDTO update(DistrictDTO districtDTO) {
        LOG.debug("Request to update District : {}", districtDTO);
        District district = districtMapper.toEntity(districtDTO);
        district = districtRepository.save(district);
        return districtMapper.toDto(district);
    }

    @Override
    public Optional<DistrictDTO> partialUpdate(DistrictDTO districtDTO) {
        LOG.debug("Request to partially update District : {}", districtDTO);

        return districtRepository
            .findById(districtDTO.getId())
            .map(existingDistrict -> {
                districtMapper.partialUpdate(existingDistrict, districtDTO);

                return existingDistrict;
            })
            .map(districtRepository::save)
            .map(districtMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DistrictDTO> findOne(Long id) {
        LOG.debug("Request to get District : {}", id);
        return districtRepository.findById(id).map(districtMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete District : {}", id);
        districtRepository.deleteById(id);
    }
}
