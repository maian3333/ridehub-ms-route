package com.ridehub.route.service;

import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.dto.request.SimpleDriverRequestDTO;
import com.ridehub.route.service.dto.response.SimpleDriverResponseDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Driver}.
 */
public interface DriverService {
    /**
     * Save a driver.
     *
     * @param driverDTO the entity to save.
     * @return the persisted entity.
     */
    DriverDTO save(DriverDTO driverDTO);

    /**
     * Updates a driver.
     *
     * @param driverDTO the entity to update.
     * @return the persisted entity.
     */
    DriverDTO update(DriverDTO driverDTO);

    /**
     * Partially updates a driver.
     *
     * @param driverDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DriverDTO> partialUpdate(DriverDTO driverDTO);

    /**
     * Get the "id" driver.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DriverDTO> findOne(Long id);

    /**
     * Delete the "id" driver.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Create a new driver with simplified input (automatically manages staff).
     *
     * @param requestDTO the simplified driver data to create.
     * @return the created driver response.
     */
    SimpleDriverResponseDTO createSimpleDriver(SimpleDriverRequestDTO requestDTO);

    /**
     * Update an existing driver with simplified input (automatically manages staff).
     *
     * @param id the id of the driver to update.
     * @param requestDTO the simplified driver data to update.
     * @return the updated driver response.
     */
    SimpleDriverResponseDTO updateSimpleDriver(Long id, SimpleDriverRequestDTO requestDTO);

    /**
     * Get a driver by id with simplified response.
     *
     * @param id the id of the driver.
     * @return the simplified driver response.
     */
    Optional<SimpleDriverResponseDTO> findSimpleDriverById(Long id);
}
