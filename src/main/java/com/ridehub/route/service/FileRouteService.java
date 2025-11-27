package com.ridehub.route.service;

import com.ridehub.route.service.dto.FileRouteDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.FileRoute}.
 */
public interface FileRouteService {
    /**
     * Save a fileRoute.
     *
     * @param fileRouteDTO the entity to save.
     * @return the persisted entity.
     */
    FileRouteDTO save(FileRouteDTO fileRouteDTO);

    /**
     * Updates a fileRoute.
     *
     * @param fileRouteDTO the entity to update.
     * @return the persisted entity.
     */
    FileRouteDTO update(FileRouteDTO fileRouteDTO);

    /**
     * Partially updates a fileRoute.
     *
     * @param fileRouteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FileRouteDTO> partialUpdate(FileRouteDTO fileRouteDTO);

    /**
     * Get the "id" fileRoute.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FileRouteDTO> findOne(Long id);

    /**
     * Delete the "id" fileRoute.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
