package com.ridehub.route.service;

import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.dto.request.SeatLockRequestDTO;
import com.ridehub.route.service.dto.request.AttachBookingRequestDTO;
import com.ridehub.route.service.dto.request.CancelGroupRequestDTO;
import com.ridehub.route.service.dto.request.ConfirmGroupRequestDTO;
import com.ridehub.route.service.dto.request.SeatHoldRequestDTO;
import com.ridehub.route.service.dto.request.SeatLockActionRequestDTO;
import com.ridehub.route.service.dto.request.SeatValidateLockRequestDTO;
import com.ridehub.route.service.dto.response.SeatLockResponseDTO;
import com.ridehub.route.service.dto.response.SeatHoldResponseDTO;
import com.ridehub.route.service.dto.response.SeatLockActionResponseDTO;
import com.ridehub.route.service.dto.response.SeatValidateLockResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.ridehub.route.domain.SeatLock}.
 */
public interface SeatLockService {
    /**
     * Save a seatLock.
     *
     * @param seatLockDTO the entity to save.
     * @return the persisted entity.
     */
    SeatLockDTO save(SeatLockDTO seatLockDTO);

    /**
     * Updates a seatLock.
     *
     * @param seatLockDTO the entity to update.
     * @return the persisted entity.
     */
    SeatLockDTO update(SeatLockDTO seatLockDTO);

    /**
     * Partially updates a seatLock.
     *
     * @param seatLockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SeatLockDTO> partialUpdate(SeatLockDTO seatLockDTO);

    /**
     * Get the "id" seatLock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SeatLockDTO> findOne(Long id);

    /**
     * Delete the "id" seatLock.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Try to lock seats for a booking.
     *
     * @param request the seat lock request containing booking details and seat
     *                numbers.
     * @return the seat lock response with status HELD or REJECTED.
     */
    SeatLockResponseDTO tryLockSeats(SeatLockRequestDTO request);

    /**
     * Confirm seat locks for a booking.
     *
     * @param request the seat lock action request.
     * @return the seat lock action response.
     */
    SeatLockActionResponseDTO confirmSeatLocks(SeatLockActionRequestDTO request);

    /**
     * Cancel seat locks for a booking.
     *
     * @param request the seat lock action request.
     * @return the seat lock action response.
     */
    SeatLockActionResponseDTO cancelSeatLocks(SeatLockActionRequestDTO request);

    /**
     * Validate seat existence and lock seats atomically.
     * This is the recommended approach for seat validation and locking.
     *
     * @param request the seat validate and lock request containing trip details and
     *                seat numbers.
     * @return the seat validate lock response with validation results, lock status,
     *         and pricing.
     */
    SeatValidateLockResponseDTO validateAndLockSeats(SeatValidateLockRequestDTO request);

    /**
     * Validate seat existence without locking seats or calculating pricing.
     * This method only checks if the seats exist and are available for validation.
     *
     * @param request the seat validate request containing trip details and seat
     *                numbers.
     * @return the seat validate response with validation results only.
     */
    SeatValidateLockResponseDTO validateSeatsOnly(SeatValidateLockRequestDTO request);

    SeatHoldResponseDTO tryHold(SeatHoldRequestDTO req);

    SeatLockActionResponseDTO attachBooking(AttachBookingRequestDTO req);

    SeatLockActionResponseDTO cancelGroup(CancelGroupRequestDTO req);

    SeatLockActionResponseDTO confirmGroup(ConfirmGroupRequestDTO req);

    List<SeatLockDTO> findActive(Long tripId, Long bookingId, String lockGroupId);

    /**
     * Reclaim expired seats for a booking.
     * Finds seatlocks by booking and checks for expired seats with same seatNo and trip
     * that are not held by other bookingId and not expired.
     *
     * @param req the reclaim seats request containing booking details.
     * @return the seat lock action response.
     */
    SeatLockActionResponseDTO reclaimExpiredSeats(ConfirmGroupRequestDTO req);
}
