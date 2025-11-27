package com.ridehub.route.web.rest;

import com.ridehub.route.repository.SeatLockRepository;
import com.ridehub.route.service.SeatLockQueryService;
import com.ridehub.route.service.SeatLockService;
import com.ridehub.route.service.criteria.SeatLockCriteria;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.dto.request.SeatLockRequestDTO;
import com.ridehub.route.service.dto.request.SeatLockActionRequestDTO;
import com.ridehub.route.service.dto.request.SeatValidateLockRequestDTO;
import com.ridehub.route.service.dto.request.SeatHoldRequestDTO;
import com.ridehub.route.service.dto.request.AttachBookingRequestDTO;
import com.ridehub.route.service.dto.request.CancelGroupRequestDTO;
import com.ridehub.route.service.dto.request.ConfirmGroupRequestDTO;
import com.ridehub.route.service.dto.response.SeatLockResponseDTO;
import com.ridehub.route.service.dto.response.SeatLockActionResponseDTO;
import com.ridehub.route.service.dto.response.SeatValidateLockResponseDTO;
import com.ridehub.route.service.dto.response.SeatHoldResponseDTO;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.route.domain.SeatLock}.
 */
@RestController
@RequestMapping("/api/seat-locks")
public class SeatLockResource {

    private static final Logger LOG = LoggerFactory.getLogger(SeatLockResource.class);

    private static final String ENTITY_NAME = "msRouteSeatLock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeatLockService seatLockService;

    private final SeatLockRepository seatLockRepository;

    private final SeatLockQueryService seatLockQueryService;

    public SeatLockResource(
            SeatLockService seatLockService,
            SeatLockRepository seatLockRepository,
            SeatLockQueryService seatLockQueryService) {
        this.seatLockService = seatLockService;
        this.seatLockRepository = seatLockRepository;
        this.seatLockQueryService = seatLockQueryService;
    }

    /**
     * {@code POST  /seat-locks} : Create a new seatLock.
     *
     * @param seatLockDTO the seatLockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new seatLockDTO, or with status {@code 400 (Bad Request)} if
     *         the seatLock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SeatLockDTO> createSeatLock(@Valid @RequestBody SeatLockDTO seatLockDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save SeatLock : {}", seatLockDTO);
        if (seatLockDTO.getId() != null) {
            throw new BadRequestAlertException("A new seatLock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        seatLockDTO = seatLockService.save(seatLockDTO);
        return ResponseEntity.created(new URI("/api/seat-locks/" + seatLockDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        seatLockDTO.getId().toString()))
                .body(seatLockDTO);
    }

    /**
     * {@code PUT  /seat-locks/:id} : Updates an existing seatLock.
     *
     * @param id          the id of the seatLockDTO to save.
     * @param seatLockDTO the seatLockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated seatLockDTO,
     *         or with status {@code 400 (Bad Request)} if the seatLockDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the seatLockDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SeatLockDTO> updateSeatLock(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody SeatLockDTO seatLockDTO) throws URISyntaxException {
        LOG.debug("REST request to update SeatLock : {}, {}", id, seatLockDTO);
        if (seatLockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seatLockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seatLockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        seatLockDTO = seatLockService.update(seatLockDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        seatLockDTO.getId().toString()))
                .body(seatLockDTO);
    }

    /**
     * {@code PATCH  /seat-locks/:id} : Partial updates given fields of an existing
     * seatLock, field will ignore if it is null
     *
     * @param id          the id of the seatLockDTO to save.
     * @param seatLockDTO the seatLockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated seatLockDTO,
     *         or with status {@code 400 (Bad Request)} if the seatLockDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the seatLockDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the seatLockDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SeatLockDTO> partialUpdateSeatLock(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody SeatLockDTO seatLockDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update SeatLock partially : {}, {}", id, seatLockDTO);
        if (seatLockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seatLockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seatLockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SeatLockDTO> result = seatLockService.partialUpdate(seatLockDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, seatLockDTO.getId().toString()));
    }

    /**
     * {@code GET  /seat-locks} : get all the seatLocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of seatLocks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SeatLockDTO>> getAllSeatLocks(SeatLockCriteria criteria) {
        LOG.debug("REST request to get SeatLocks by criteria: {}", criteria);

        List<SeatLockDTO> entityList = seatLockQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /seat-locks/count} : count all the seatLocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSeatLocks(SeatLockCriteria criteria) {
        LOG.debug("REST request to count SeatLocks by criteria: {}", criteria);
        return ResponseEntity.ok().body(seatLockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /seat-locks/:id} : get the "id" seatLock.
     *
     * @param id the id of the seatLockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the seatLockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeatLockDTO> getSeatLock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SeatLock : {}", id);
        Optional<SeatLockDTO> seatLockDTO = seatLockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seatLockDTO);
    }

    /**
     * {@code DELETE  /seat-locks/:id} : delete the "id" seatLock.
     *
     * @param id the id of the seatLockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatLock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SeatLock : {}", id);
        seatLockService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    // ===========================
    // NEW: Try-lock endpoint
    // ===========================
    @PostMapping("/try-lock")
    public ResponseEntity<SeatLockResponseDTO> tryLockSeats(@Valid @RequestBody SeatLockRequestDTO request) {
        LOG.debug("REST request to tryLockSeats: bookingId={}, tripId={}, seats={}",
                request.getBookingId(), request.getTripId(), request.getSeatNumbers());

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new BadRequestAlertException("Seat list must not be empty", ENTITY_NAME, "emptyseats");
        }
        if (request.getTripId() == null) {
            throw new BadRequestAlertException("TripId is required", ENTITY_NAME, "tripidnull");
        }
        if (request.getIdemKey() == null || request.getIdemKey().isBlank()) {
            throw new BadRequestAlertException("Idempotency key is required", ENTITY_NAME, "idemkeynull");
        }

        SeatLockResponseDTO result = seatLockService.tryLockSeats(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<SeatLockActionResponseDTO> confirmSeatLocks(@Valid @RequestBody SeatLockActionRequestDTO request) {
        LOG.debug("REST request to confirm seat locks for booking: {}", request.getBookingId());
        SeatLockActionResponseDTO result = seatLockService.confirmSeatLocks(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cancel")
    public ResponseEntity<SeatLockActionResponseDTO> cancelSeatLocks(@Valid @RequestBody SeatLockActionRequestDTO request) {
        LOG.debug("REST request to cancel seat locks for booking: {}", request.getBookingId());
        SeatLockActionResponseDTO result = seatLockService.cancelSeatLocks(request);
        return ResponseEntity.ok(result);
    }

    // ===========================
    // NEW: Validate and lock endpoint
    // ===========================
    @PostMapping("/validate-lock")
    public ResponseEntity<SeatValidateLockResponseDTO> validateAndLockSeats(@Valid @RequestBody SeatValidateLockRequestDTO request) {
        LOG.debug("REST request to validateAndLockSeats: tripId={}, seats={}, promoCode={}",
                request.getTripId(), request.getSeatNumbers(), request.getPromoCode());

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new BadRequestAlertException("Seat list must not be empty", ENTITY_NAME, "emptyseats");
        }
        if (request.getTripId() == null) {
            throw new BadRequestAlertException("TripId is required", ENTITY_NAME, "tripidnull");
        }
        if (request.getIdemKey() == null || request.getIdemKey().isBlank()) {
            throw new BadRequestAlertException("Idempotency key is required", ENTITY_NAME, "idemkeynull");
        }

        SeatValidateLockResponseDTO result = seatLockService.validateAndLockSeats(request);
        return ResponseEntity.ok(result);
    }

    // ===========================
    // NEW: Validate only endpoint
    // ===========================
    @PostMapping("/validate-only")
    public ResponseEntity<SeatValidateLockResponseDTO> validateSeatsOnly(@Valid @RequestBody SeatValidateLockRequestDTO request) {
        LOG.debug("REST request to validateSeatsOnly: tripId={}, seats={}",
                request.getTripId(), request.getSeatNumbers());

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new BadRequestAlertException("Seat list must not be empty", ENTITY_NAME, "emptyseats");
        }
        if (request.getTripId() == null) {
            throw new BadRequestAlertException("TripId is required", ENTITY_NAME, "tripidnull");
        }
        if (request.getIdemKey() == null || request.getIdemKey().isBlank()) {
            throw new BadRequestAlertException("Idempotency key is required", ENTITY_NAME, "idemkeynull");
        }

        SeatValidateLockResponseDTO result = seatLockService.validateSeatsOnly(request);
        return ResponseEntity.ok(result);
    }

    // ===========================
    // NEW: Seat Hold Management Endpoints
    // ===========================

    /**
     * {@code POST  /seat-locks/hold} : Hold seats without bookingId yet.
     *
     * @param request the seat hold request containing trip details, seat numbers, and lock group info.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seat hold response.
     */
    @PostMapping("/hold")
    public ResponseEntity<SeatHoldResponseDTO> holdSeats(@Valid @RequestBody SeatHoldRequestDTO request) {
        LOG.debug("REST request to hold seats: tripId={}, seats={}, lockGroupId={}, userId={}",
                request.getTripId(), request.getSeatNumbers(), request.getLockGroupId(), request.getUserId());

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new BadRequestAlertException("Seat list must not be empty", ENTITY_NAME, "emptyseats");
        }
        if (request.getTripId() == null) {
            throw new BadRequestAlertException("TripId is required", ENTITY_NAME, "tripidnull");
        }
        if (request.getLockGroupId() == null || request.getLockGroupId().isBlank()) {
            throw new BadRequestAlertException("LockGroupId is required", ENTITY_NAME, "lockgroupidnull");
        }
        if (request.getIdemKey() == null || request.getIdemKey().isBlank()) {
            throw new BadRequestAlertException("Idempotency key is required", ENTITY_NAME, "idemkeynull");
        }

        SeatHoldResponseDTO result = seatLockService.tryHold(request);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /seat-locks/attach-booking} : Attach bookingId to a seat hold group.
     *
     * @param request the attach booking request containing lockGroupId and bookingId.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action response.
     */
    @PostMapping("/attach-booking")
    public ResponseEntity<SeatLockActionResponseDTO> attachBooking(@Valid @RequestBody AttachBookingRequestDTO request) {
        LOG.debug("REST request to attach booking: lockGroupId={}, bookingId={}",
                request.getLockGroupId(), request.getBookingId());

        if (request.getLockGroupId() == null || request.getLockGroupId().isBlank()) {
            throw new BadRequestAlertException("LockGroupId is required", ENTITY_NAME, "lockgroupidnull");
        }
        if (request.getBookingId() == null) {
            throw new BadRequestAlertException("BookingId is required", ENTITY_NAME, "bookingidnull");
        }

        SeatLockActionResponseDTO result = seatLockService.attachBooking(request);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /seat-locks/cancel-group} : Cancel seat holds by group or booking.
     *
     * @param request the cancel group request containing lockGroupId or bookingId.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action response.
     */
    @PostMapping("/cancel-group")
    public ResponseEntity<SeatLockActionResponseDTO> cancelGroup(@Valid @RequestBody CancelGroupRequestDTO request) {
        LOG.debug("REST request to cancel group: lockGroupId={}, bookingId={}",
                request.getLockGroupId(), request.getBookingId());

        if ((request.getLockGroupId() == null || request.getLockGroupId().isBlank()) && 
            (request.getBookingId() == null)) {
            throw new BadRequestAlertException("Either lockGroupId or bookingId must be provided", ENTITY_NAME, "nocriteria");
        }

        SeatLockActionResponseDTO result = seatLockService.cancelGroup(request);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /seat-locks/confirm-group} : Confirm seat holds by booking (preferred) or group.
     *
     * @param request the confirm group request containing bookingId (preferred) or lockGroupId.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action response.
     */
    @PostMapping("/confirm-group")
    public ResponseEntity<SeatLockActionResponseDTO> confirmGroup(@Valid @RequestBody ConfirmGroupRequestDTO request) {
        LOG.debug("REST request to confirm group: lockGroupId={}, bookingId={}",
                request.getLockGroupId(), request.getBookingId());

        if ((request.getLockGroupId() == null || request.getLockGroupId().isBlank()) && 
            (request.getBookingId() == null)) {
            throw new BadRequestAlertException("Either lockGroupId or bookingId must be provided", ENTITY_NAME, "nocriteria");
        }

        SeatLockActionResponseDTO result = seatLockService.confirmGroup(request);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /seat-locks/active} : Get active seat locks by trip, booking, or lock group.
     *
     * @param tripId the trip ID to filter by (optional).
     * @param bookingId the booking ID to filter by (optional).
     * @param lockGroupId the lock group ID to filter by (optional).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of active seat locks.
     */
    @GetMapping("/active")
    public ResponseEntity<List<SeatLockDTO>> getActiveSeatLocks(
            @RequestParam(required = false) Long tripId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String lockGroupId) {
        LOG.debug("REST request to get active seat locks: tripId={}, bookingId={}, lockGroupId={}",
                tripId, bookingId, lockGroupId);

        List<SeatLockDTO> result = seatLockService.findActive(tripId, bookingId, lockGroupId);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /seat-locks/reclaim-expired} : Reclaim expired seats for a booking.
     *
     * @param request the reclaim request containing bookingId.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action response.
     */
    @PostMapping("/reclaim-expired")
    public ResponseEntity<SeatLockActionResponseDTO> reclaimExpiredSeats(@Valid @RequestBody ConfirmGroupRequestDTO request) {
        LOG.debug("REST request to reclaim expired seats for booking: {}", request.getBookingId());

        if (request.getBookingId() == null) {
            throw new BadRequestAlertException("BookingId is required", ENTITY_NAME, "bookingidnull");
        }

        SeatLockActionResponseDTO result = seatLockService.reclaimExpiredSeats(request);
        return ResponseEntity.ok(result);
    }
}
