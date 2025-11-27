package com.ridehub.route.service;

import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.dto.request.SimpleAttendantRequestDTO;
import com.ridehub.route.service.dto.response.SimpleAttendantResponseDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Attendant}.
 */
public interface AttendantService {
    /**
     * Save a attendant.
     *
     * @param attendantDTO the entity to save.
     * @return the persisted entity.
     */
    AttendantDTO save(AttendantDTO attendantDTO);

    /**
     * Updates a attendant.
     *
     * @param attendantDTO the entity to update.
     * @return the persisted entity.
     */
    AttendantDTO update(AttendantDTO attendantDTO);

    /**
     * Partially updates a attendant.
     *
     * @param attendantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AttendantDTO> partialUpdate(AttendantDTO attendantDTO);

    /**
     * Get the "id" attendant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AttendantDTO> findOne(Long id);

    /**
     * Delete the "id" attendant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Create a new attendant with simplified input (automatically manages staff).
     *
     * @param requestDTO the simplified attendant data to create.
     * @return the created attendant response.
     */
    SimpleAttendantResponseDTO createSimpleAttendant(SimpleAttendantRequestDTO requestDTO);

    /**
     * Update an existing attendant with simplified input (automatically manages staff).
     *
     * @param id the id of the attendant to update.
     * @param requestDTO the simplified attendant data to update.
     * @return the updated attendant response.
     */
    SimpleAttendantResponseDTO updateSimpleAttendant(Long id, SimpleAttendantRequestDTO requestDTO);

    /**
     * Get an attendant by id with simplified response.
     *
     * @param id the id of the attendant.
     * @return the simplified attendant response.
     */
    Optional<SimpleAttendantResponseDTO> findSimpleAttendantById(Long id);
}
