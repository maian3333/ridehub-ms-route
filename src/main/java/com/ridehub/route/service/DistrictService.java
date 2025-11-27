package com.ridehub.route.service;

import com.ridehub.route.service.dto.DistrictDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.District}.
 */
public interface DistrictService {
    /**
     * Save a district.
     *
     * @param districtDTO the entity to save.
     * @return the persisted entity.
     */
    DistrictDTO save(DistrictDTO districtDTO);

    /**
     * Updates a district.
     *
     * @param districtDTO the entity to update.
     * @return the persisted entity.
     */
    DistrictDTO update(DistrictDTO districtDTO);

    /**
     * Partially updates a district.
     *
     * @param districtDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DistrictDTO> partialUpdate(DistrictDTO districtDTO);

    /**
     * Get the "id" district.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DistrictDTO> findOne(Long id);

    /**
     * Delete the "id" district.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
