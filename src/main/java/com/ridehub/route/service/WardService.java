package com.ridehub.route.service;

import com.ridehub.route.service.dto.WardDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Ward}.
 */
public interface WardService {
    /**
     * Save a ward.
     *
     * @param wardDTO the entity to save.
     * @return the persisted entity.
     */
    WardDTO save(WardDTO wardDTO);

    /**
     * Updates a ward.
     *
     * @param wardDTO the entity to update.
     * @return the persisted entity.
     */
    WardDTO update(WardDTO wardDTO);

    /**
     * Partially updates a ward.
     *
     * @param wardDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WardDTO> partialUpdate(WardDTO wardDTO);

    /**
     * Get the "id" ward.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WardDTO> findOne(Long id);

    /**
     * Delete the "id" ward.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
