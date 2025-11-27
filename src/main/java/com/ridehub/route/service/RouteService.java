package com.ridehub.route.service;

import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.request.RouteStationRequestDTO;
import com.ridehub.route.service.dto.response.RouteStationResponseDTO;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Route}.
 */
public interface RouteService {
    /**
     * Save a route.
     *
     * @param routeDTO the entity to save.
     * @return the persisted entity.
     */
    RouteDTO save(RouteDTO routeDTO);

    /**
     * Updates a route.
     *
     * @param routeDTO the entity to update.
     * @return the persisted entity.
     */
    RouteDTO update(RouteDTO routeDTO);

    /**
     * Partially updates a route.
     *
     * @param routeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RouteDTO> partialUpdate(RouteDTO routeDTO);

    /**
     * Get the "id" route.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RouteDTO> findOne(Long id);

    /**
     * Delete the "id" route.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the route corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RouteDTO> search(String query, Pageable pageable);

    /**
     * Create a new route based on station ID with flag-based logic.
     *
     * @param requestDTO the route station request containing station ID and flag
     * @return the created route response
     */
    RouteStationResponseDTO createRouteWithStation(RouteStationRequestDTO requestDTO);

    /**
     * Update an existing route based on station ID with flag-based logic.
     *
     * @param routeId the ID of the route to update
     * @param requestDTO the route station request containing station ID and flag
     * @return the updated route response
     */
    RouteStationResponseDTO updateRouteWithStation(Long routeId, RouteStationRequestDTO requestDTO);
}
