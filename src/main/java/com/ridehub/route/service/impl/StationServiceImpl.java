package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Station;
import com.ridehub.route.repository.StationRepository;
import com.ridehub.route.repository.search.StationSearchRepository;
import com.ridehub.route.service.RouteQueryService;
import com.ridehub.route.service.StationService;
import com.ridehub.route.service.dto.StationDTO;
import com.ridehub.route.service.mapper.RouteMapper;
import com.ridehub.route.service.mapper.StationMapper;
import com.ridehub.route.service.vm.StationWithRoutesVM;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Station}.
 */
@Service
@Transactional
public class StationServiceImpl implements StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationServiceImpl.class);

    private final StationRepository stationRepository;

    private final StationMapper stationMapper;

    private final StationSearchRepository stationSearchRepository;
    private final RouteMapper routeMapper;
    private final RouteQueryService routeQueryService;

    public StationServiceImpl(
            StationRepository stationRepository,
            StationMapper stationMapper,
            StationSearchRepository stationSearchRepository, RouteMapper routeMapper, RouteQueryService routeQueryService) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
        this.stationSearchRepository = stationSearchRepository;
        this.routeMapper = routeMapper;
        this.routeQueryService = routeQueryService;
    }

    @Override
    public StationDTO save(StationDTO stationDTO) {
        LOG.debug("Request to save Station : {}", stationDTO);
        Station station = stationMapper.toEntity(stationDTO);
        station = stationRepository.save(station);
        stationSearchRepository.index(station);
        return stationMapper.toDto(station);
    }

    @Override
    public StationDTO update(StationDTO stationDTO) {
        LOG.debug("Request to update Station : {}", stationDTO);
        Station station = stationMapper.toEntity(stationDTO);
        station = stationRepository.save(station);
        stationSearchRepository.index(station);
        return stationMapper.toDto(station);
    }

    @Override
    public Optional<StationDTO> partialUpdate(StationDTO stationDTO) {
        LOG.debug("Request to partially update Station : {}", stationDTO);

        return stationRepository
                .findById(stationDTO.getId())
                .map(existingStation -> {
                    stationMapper.partialUpdate(existingStation, stationDTO);

                    return existingStation;
                })
                .map(stationRepository::save)
                .map(savedStation -> {
                    stationSearchRepository.index(savedStation);
                    return savedStation;
                })
                .map(stationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StationDTO> findOne(Long id) {
        LOG.debug("Request to get Station : {}", id);
        return stationRepository.findById(id).map(stationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Station : {}", id);
        stationRepository.deleteById(id);
        stationSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Stations for query {}", query);
        return stationSearchRepository.search(query, pageable).map(stationMapper::toDto);
    }

    /**
     * Get all stations with their associated routes
     * 
     * @param pageable pagination information
     * @return Page of StationWithRoutesVM
     */
    @Override
    @Transactional(readOnly = true)
    public Page<StationWithRoutesVM> getStationsWithRoutes(Pageable pageable) {
        LOG.debug("Request to get all stations with routes");

        Page<Station> stations = stationRepository.findAll(pageable);
        List<Long> stationIds = stations.stream().map(Station::getId).toList();
        Map<Long, Long> counts = routeQueryService.getRouteCountsByStationIds(stationIds);

        // Map each station to DTO
        List<StationWithRoutesVM> stationWithRoutesDTOs = stations.stream()
                .map(s -> {
                    StationWithRoutesVM dto = stationMapper.toStationWithRoutesDto(s);
                    dto.setRoutresCount(counts.getOrDefault(s.getId(), 0L));
                    return dto;
                })
                .toList();

        return new PageImpl<>(stationWithRoutesDTOs, pageable, stations.getTotalElements());
    }

    /**
     * Get a station by ID with all its associated routes.
     *
     * @param id the ID of the station to retrieve.
     * @return an Optional containing the StationWithRoutesVM with its routes, or
     *         empty if not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StationWithRoutesVM> getStationsWithRoutesAndId(Long id, Pageable pageable) {
        return stationRepository.findById(id).map(station -> {
            StationWithRoutesVM dto = stationMapper.toStationWithRoutesDto(station);

            Page<Route> routePage = routeQueryService.findByOriginOrDestination(station, pageable);
            dto.setRoutes(routePage.getContent().stream().map(routeMapper::toDto).toList());

            return dto;
        });
    }
}
