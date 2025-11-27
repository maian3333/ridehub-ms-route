package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Seat;
import com.ridehub.route.repository.SeatRepository;
import com.ridehub.route.service.SeatService;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.mapper.SeatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Seat}.
 */
@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    private static final Logger LOG = LoggerFactory.getLogger(SeatServiceImpl.class);

    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    @Override
    public SeatDTO save(SeatDTO seatDTO) {
        LOG.debug("Request to save Seat : {}", seatDTO);
        Seat seat = seatMapper.toEntity(seatDTO);
        seat = seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    @Override
    public SeatDTO update(SeatDTO seatDTO) {
        LOG.debug("Request to update Seat : {}", seatDTO);
        Seat seat = seatMapper.toEntity(seatDTO);
        seat = seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    @Override
    public Optional<SeatDTO> partialUpdate(SeatDTO seatDTO) {
        LOG.debug("Request to partially update Seat : {}", seatDTO);

        return seatRepository
            .findById(seatDTO.getId())
            .map(existingSeat -> {
                seatMapper.partialUpdate(existingSeat, seatDTO);

                return existingSeat;
            })
            .map(seatRepository::save)
            .map(seatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeatDTO> findOne(Long id) {
        LOG.debug("Request to get Seat : {}", id);
        return seatRepository.findById(id).map(seatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Seat : {}", id);
        seatRepository.deleteById(id);
    }
}
