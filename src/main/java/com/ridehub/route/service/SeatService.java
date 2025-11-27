package com.ridehub.route.service;

import com.ridehub.route.service.dto.SeatDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.Seat}.
 */
public interface SeatService {
    /**
     * Save a seat.
     *
     * @param seatDTO the entity to save.
     * @return the persisted entity.
     */
    SeatDTO save(SeatDTO seatDTO);

    /**
     * Updates a seat.
     *
     * @param seatDTO the entity to update.
     * @return the persisted entity.
     */
    SeatDTO update(SeatDTO seatDTO);

    /**
     * Partially updates a seat.
     *
     * @param seatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SeatDTO> partialUpdate(SeatDTO seatDTO);

    /**
     * Get the "id" seat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SeatDTO> findOne(Long id);

    /**
     * Delete the "id" seat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
