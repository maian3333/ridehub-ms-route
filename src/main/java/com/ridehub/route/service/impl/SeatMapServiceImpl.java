package com.ridehub.route.service.impl;

import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.repository.SeatMapRepository;
import com.ridehub.route.service.SeatMapService;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.mapper.SeatMapMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.SeatMap}.
 */
@Service
@Transactional
public class SeatMapServiceImpl implements SeatMapService {

    private static final Logger LOG = LoggerFactory.getLogger(SeatMapServiceImpl.class);

    private final SeatMapRepository seatMapRepository;

    private final SeatMapMapper seatMapMapper;

    public SeatMapServiceImpl(SeatMapRepository seatMapRepository, SeatMapMapper seatMapMapper) {
        this.seatMapRepository = seatMapRepository;
        this.seatMapMapper = seatMapMapper;
    }

    @Override
    public SeatMapDTO save(SeatMapDTO seatMapDTO) {
        LOG.debug("Request to save SeatMap : {}", seatMapDTO);
        SeatMap seatMap = seatMapMapper.toEntity(seatMapDTO);
        seatMap = seatMapRepository.save(seatMap);
        return seatMapMapper.toDto(seatMap);
    }

    @Override
    public SeatMapDTO update(SeatMapDTO seatMapDTO) {
        LOG.debug("Request to update SeatMap : {}", seatMapDTO);
        SeatMap seatMap = seatMapMapper.toEntity(seatMapDTO);
        seatMap = seatMapRepository.save(seatMap);
        return seatMapMapper.toDto(seatMap);
    }

    @Override
    public Optional<SeatMapDTO> partialUpdate(SeatMapDTO seatMapDTO) {
        LOG.debug("Request to partially update SeatMap : {}", seatMapDTO);

        return seatMapRepository
                .findById(seatMapDTO.getId())
                .map(existingSeatMap -> {
                    seatMapMapper.partialUpdate(existingSeatMap, seatMapDTO);

                    return existingSeatMap;
                })
                .map(seatMapRepository::save)
                .map(seatMapMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeatMapDTO> findOne(Long id) {
        LOG.debug("Request to get SeatMap : {}", id);
        return seatMapRepository.findById(id).map(seatMapMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SeatMap : {}", id);
        seatMapRepository.deleteById(id);
    }
}
