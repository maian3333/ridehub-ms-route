package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Province;
import com.ridehub.route.repository.ProvinceRepository;
import com.ridehub.route.service.ProvinceService;
import com.ridehub.route.service.dto.ProvinceDTO;
import com.ridehub.route.service.mapper.ProvinceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Province}.
 */
@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    private static final Logger LOG = LoggerFactory.getLogger(ProvinceServiceImpl.class);

    private final ProvinceRepository provinceRepository;

    private final ProvinceMapper provinceMapper;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, ProvinceMapper provinceMapper) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
    }

    @Override
    public ProvinceDTO save(ProvinceDTO provinceDTO) {
        LOG.debug("Request to save Province : {}", provinceDTO);
        Province province = provinceMapper.toEntity(provinceDTO);
        province = provinceRepository.save(province);
        return provinceMapper.toDto(province);
    }

    @Override
    public ProvinceDTO update(ProvinceDTO provinceDTO) {
        LOG.debug("Request to update Province : {}", provinceDTO);
        Province province = provinceMapper.toEntity(provinceDTO);
        province = provinceRepository.save(province);
        return provinceMapper.toDto(province);
    }

    @Override
    public Optional<ProvinceDTO> partialUpdate(ProvinceDTO provinceDTO) {
        LOG.debug("Request to partially update Province : {}", provinceDTO);

        return provinceRepository
            .findById(provinceDTO.getId())
            .map(existingProvince -> {
                provinceMapper.partialUpdate(existingProvince, provinceDTO);

                return existingProvince;
            })
            .map(provinceRepository::save)
            .map(provinceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProvinceDTO> findOne(Long id) {
        LOG.debug("Request to get Province : {}", id);
        return provinceRepository.findById(id).map(provinceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Province : {}", id);
        provinceRepository.deleteById(id);
    }
}
