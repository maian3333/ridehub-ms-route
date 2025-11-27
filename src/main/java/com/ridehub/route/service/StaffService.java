package com.ridehub.route.service;

import com.ridehub.route.service.dto.StaffDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Staff}.
 */
public interface StaffService {
    /**
     * Save a staff.
     *
     * @param staffDTO the entity to save.
     * @return the persisted entity.
     */
    StaffDTO save(StaffDTO staffDTO);

    /**
     * Updates a staff.
     *
     * @param staffDTO the entity to update.
     * @return the persisted entity.
     */
    StaffDTO update(StaffDTO staffDTO);

    /**
     * Partially updates a staff.
     *
     * @param staffDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StaffDTO> partialUpdate(StaffDTO staffDTO);

    /**
     * Get all the StaffDTO where Driver is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<StaffDTO> findAllWhereDriverIsNull();
    /**
     * Get all the StaffDTO where Attendant is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<StaffDTO> findAllWhereAttendantIsNull();

    /**
     * Get the "id" staff.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StaffDTO> findOne(Long id);

    /**
     * Delete the "id" staff.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
