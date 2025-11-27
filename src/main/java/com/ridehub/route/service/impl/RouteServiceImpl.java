package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Station;
import com.ridehub.route.repository.RouteRepository;
import com.ridehub.route.repository.StationRepository;
import com.ridehub.route.repository.search.RouteSearchRepository;
import com.ridehub.route.service.RouteService;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.request.RouteStationRequestDTO;
import com.ridehub.route.service.dto.response.RouteStationResponseDTO;
import com.ridehub.route.service.mapper.RouteMapper;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Route}.
 */
@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private static final Logger LOG = LoggerFactory.getLogger(RouteServiceImpl.class);

    private final RouteRepository routeRepository;

    private final RouteMapper routeMapper;

    private final RouteSearchRepository routeSearchRepository;

    private final StationRepository stationRepository;

    public RouteServiceImpl(RouteRepository routeRepository, RouteMapper routeMapper, RouteSearchRepository routeSearchRepository, StationRepository stationRepository) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.routeSearchRepository = routeSearchRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public RouteDTO save(RouteDTO routeDTO) {
        LOG.debug("Request to save Route : {}", routeDTO);
        Route route = routeMapper.toEntity(routeDTO);
        route = routeRepository.save(route);
        routeSearchRepository.index(route);
        return routeMapper.toDto(route);
    }

    @Override
    public RouteDTO update(RouteDTO routeDTO) {
        LOG.debug("Request to update Route : {}", routeDTO);
        Route route = routeMapper.toEntity(routeDTO);
        route = routeRepository.save(route);
        routeSearchRepository.index(route);
        return routeMapper.toDto(route);
    }

    @Override
    public Optional<RouteDTO> partialUpdate(RouteDTO routeDTO) {
        LOG.debug("Request to partially update Route : {}", routeDTO);

        return routeRepository
            .findById(routeDTO.getId())
            .map(existingRoute -> {
                routeMapper.partialUpdate(existingRoute, routeDTO);

                return existingRoute;
            })
            .map(routeRepository::save)
            .map(savedRoute -> {
                routeSearchRepository.index(savedRoute);
                return savedRoute;
            })
            .map(routeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RouteDTO> findOne(Long id) {
        LOG.debug("Request to get Route : {}", id);
        return routeRepository.findById(id).map(routeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Route : {}", id);
        routeRepository.deleteById(id);
        routeSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RouteDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Routes for query {}", query);
        return routeSearchRepository.search(query, pageable).map(routeMapper::toDto);
    }

    @Override
    public RouteStationResponseDTO createRouteWithStation(RouteStationRequestDTO requestDTO) {
        LOG.debug("Request to create Route with station : {}", requestDTO);

        // Validate station exists
        Station station = stationRepository.findById(requestDTO.getStationId())
            .orElseThrow(() -> new BadRequestAlertException("Station not found", "station", "notfound"));

        // Create new route
        Route route = new Route();
        route.setRouteCode(requestDTO.getRouteCode());
        route.setDistanceKm(requestDTO.getDistanceKm());
        route.setCreatedAt(Instant.now());
        route.setIsDeleted(false);

        // Set origin or destination based on flag
        // For create operation, we'll create a temporary route with the same station as both origin and destination
        // This allows the route to be created and later updated with the proper destination/origin
        if (requestDTO.getIsOrigin()) {
            route.setOrigin(station);
            route.setDestination(station); // Temporary - will be updated later
        } else {
            route.setOrigin(station); // Temporary - will be updated later
            route.setDestination(station);
        }

        route = routeRepository.save(route);
        routeSearchRepository.index(route);

        return convertToRouteStationResponseDTO(route);
    }

    @Override
    public RouteStationResponseDTO updateRouteWithStation(Long routeId, RouteStationRequestDTO requestDTO) {
        LOG.debug("Request to update Route {} with station : {}", routeId, requestDTO);

        // Find existing route
        Route existingRoute = routeRepository.findById(routeId)
            .orElseThrow(() -> new BadRequestAlertException("Route not found", "route", "notfound"));

        // Validate station exists
        Station station = stationRepository.findById(requestDTO.getStationId())
            .orElseThrow(() -> new BadRequestAlertException("Station not found", "station", "notfound"));

        // Update route fields
        if (requestDTO.getRouteCode() != null) {
            existingRoute.setRouteCode(requestDTO.getRouteCode());
        }
        if (requestDTO.getDistanceKm() != null) {
            existingRoute.setDistanceKm(requestDTO.getDistanceKm());
        }
        existingRoute.setUpdatedAt(Instant.now());

        // Auto-detect if station is already origin or destination, or use flag
        boolean isCurrentOrigin = existingRoute.getOrigin() != null &&
                                 existingRoute.getOrigin().getId().equals(requestDTO.getStationId());
        boolean isCurrentDestination = existingRoute.getDestination() != null &&
                                      existingRoute.getDestination().getId().equals(requestDTO.getStationId());

        if (isCurrentOrigin) {
            // Station is already the origin, update it
            existingRoute.setOrigin(station);
        } else if (isCurrentDestination) {
            // Station is already the destination, update it
            existingRoute.setDestination(station);
        } else {
            // Station is new, use flag to determine placement
            if (requestDTO.getIsOrigin()) {
                existingRoute.setOrigin(station);
            } else {
                existingRoute.setDestination(station);
            }
        }

        existingRoute = routeRepository.save(existingRoute);
        routeSearchRepository.index(existingRoute);

        return convertToRouteStationResponseDTO(existingRoute);
    }

    /**
     * Convert Route entity to RouteStationResponseDTO.
     *
     * @param route the route entity
     * @return the route station response DTO
     */
    private RouteStationResponseDTO convertToRouteStationResponseDTO(Route route) {
        RouteStationResponseDTO responseDTO = new RouteStationResponseDTO();

        responseDTO.setId(route.getId());
        responseDTO.setRouteCode(route.getRouteCode());
        responseDTO.setDistanceKm(route.getDistanceKm());
        responseDTO.setCreatedAt(route.getCreatedAt());
        responseDTO.setUpdatedAt(route.getUpdatedAt());
        responseDTO.setIsDeleted(route.getIsDeleted());
        responseDTO.setDeletedAt(route.getDeletedAt());
        responseDTO.setDeletedBy(route.getDeletedBy());

        if (route.getOrigin() != null) {
            responseDTO.setOriginId(route.getOrigin().getId());
            responseDTO.setOriginName(route.getOrigin().getName());
        }

        if (route.getDestination() != null) {
            responseDTO.setDestinationId(route.getDestination().getId());
            responseDTO.setDestinationName(route.getDestination().getName());
        }

        return responseDTO;
    }
}
