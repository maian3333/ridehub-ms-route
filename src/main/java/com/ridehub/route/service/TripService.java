package com.ridehub.route.service;

import com.ridehub.route.service.dto.TripDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Trip}.
 */
public interface TripService {
    /**
     * Save a trip.
     *
     * @param tripDTO the entity to save.
     * @return the persisted entity.
     */
    TripDTO save(TripDTO tripDTO);

    /**
     * Updates a trip.
     *
     * @param tripDTO the entity to update.
     * @return the persisted entity.
     */
    TripDTO update(TripDTO tripDTO);

    /**
     * Partially updates a trip.
     *
     * @param tripDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TripDTO> partialUpdate(TripDTO tripDTO);

    /**
     * Get the "id" trip.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TripDTO> findOne(Long id);

    /**
     * Delete the "id" trip.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
