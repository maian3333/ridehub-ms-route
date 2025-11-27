package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.enumeration.LockStatus;
import com.ridehub.route.repository.SeatLockRepository;
import com.ridehub.route.service.criteria.SeatLockCriteria;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.mapper.SeatLockMapper;
import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link SeatLock} entities in the
 * database.
 * The main input is a {@link SeatLockCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SeatLockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SeatLockQueryService extends QueryService<SeatLock> {

    private static final Logger LOG = LoggerFactory.getLogger(SeatLockQueryService.class);

    private final SeatLockRepository seatLockRepository;

    private final SeatLockMapper seatLockMapper;

    public SeatLockQueryService(SeatLockRepository seatLockRepository, SeatLockMapper seatLockMapper) {
        this.seatLockRepository = seatLockRepository;
        this.seatLockMapper = seatLockMapper;
    }

    /**
     * Return a {@link List} of {@link SeatLockDTO} which matches the criteria from
     * the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SeatLockDTO> findByCriteria(SeatLockCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<SeatLock> specification = createSpecification(criteria);
        return seatLockMapper.toDto(seatLockRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SeatLockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SeatLock> specification = createSpecification(criteria);
        return seatLockRepository.count(specification);
    }

    /**
     * Function to convert {@link SeatLockCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SeatLock> createSpecification(SeatLockCriteria criteria) {
        Specification<SeatLock> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                    buildRangeSpecification(criteria.getId(), SeatLock_.id),
                    buildStringSpecification(criteria.getSeatNo(), SeatLock_.seatNo),
                    buildRangeSpecification(criteria.getUserId(), SeatLock_.userId),
                    buildSpecification(criteria.getStatus(), SeatLock_.status),
                    buildRangeSpecification(criteria.getExpiresAt(), SeatLock_.expiresAt),
                    buildStringSpecification(criteria.getIdempotencyKey(), SeatLock_.idempotencyKey),
                    buildRangeSpecification(criteria.getBookingId(), SeatLock_.bookingId),
                    buildRangeSpecification(criteria.getCreatedAt(), SeatLock_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), SeatLock_.updatedAt),
                    buildSpecification(criteria.getIsDeleted(), SeatLock_.isDeleted),
                    buildRangeSpecification(criteria.getDeletedAt(), SeatLock_.deletedAt),
                    buildSpecification(criteria.getDeletedBy(), SeatLock_.deletedBy),
                    buildSpecification(criteria.getTripId(),
                            root -> root.join(SeatLock_.trip, JoinType.LEFT).get(Trip_.id)));
        }
        return specification;
    }

    /**
     * Find existing seat lock by idempotency key.
     */
    @Transactional(readOnly = true)
    public Optional<SeatLock> findByIdempotencyKey(String idempotencyKey) {
        LOG.debug("find by idempotency key : {}", idempotencyKey);
        tech.jhipster.service.filter.StringFilter filter = new tech.jhipster.service.filter.StringFilter();
        filter.setEquals(idempotencyKey);
        Specification<SeatLock> specification = Specification.where(
                buildStringSpecification(filter, SeatLock_.idempotencyKey));
        return seatLockRepository.findOne(specification);
    }

    /**
     * Find active seat locks for specific trip and seat numbers.
     */
    @Transactional(readOnly = true)
    public List<SeatLock> findActiveLocksByTripAndSeats(Long tripId, List<String> seatNumbers, LockStatus status,
            Instant now) {
        LOG.debug("find active locks by trip {} and seats {} with status {} after {}", tripId, seatNumbers, status,
                now);

        tech.jhipster.service.filter.LongFilter tripFilter = new tech.jhipster.service.filter.LongFilter();
        tripFilter.setEquals(tripId);

        tech.jhipster.service.filter.StringFilter seatFilter = new tech.jhipster.service.filter.StringFilter();
        seatFilter.setIn(seatNumbers);

        tech.jhipster.service.filter.Filter<LockStatus> statusFilter = new tech.jhipster.service.filter.Filter<>();
        statusFilter.setEquals(status);

        tech.jhipster.service.filter.InstantFilter expiresFilter = new tech.jhipster.service.filter.InstantFilter();
        expiresFilter.setGreaterThan(now);

        tech.jhipster.service.filter.BooleanFilter deletedFilter = new tech.jhipster.service.filter.BooleanFilter();
        deletedFilter.setEquals(false);

        Specification<SeatLock> specification = Specification.<SeatLock>where(
                buildSpecification(tripFilter, root -> root.join(SeatLock_.trip, JoinType.LEFT).get(Trip_.id))).and(
                        buildStringSpecification(seatFilter, SeatLock_.seatNo))
                .and(
                        buildSpecification(statusFilter, SeatLock_.status))
                .and(
                        buildRangeSpecification(expiresFilter, SeatLock_.expiresAt))
                .and(
                        Specification.<SeatLock>where(
                                buildSpecification(deletedFilter, SeatLock_.isDeleted)).or(
                                        (root, query, criteriaBuilder) -> criteriaBuilder
                                                .isNull(root.get(SeatLock_.isDeleted))));

        return seatLockRepository.findAll(specification);
    }

    /**
     * Find all locks for a specific booking.
     */
    @Transactional(readOnly = true)
    public List<SeatLock> findByBookingId(Long bookingId) {
        LOG.debug("find by booking id : {}", bookingId);

        // Build the booking ID filter
        LongFilter bookingFilter = new LongFilter();
        bookingFilter.setEquals(bookingId);

        // Build the main specification
        Specification<SeatLock> specification = Specification.<SeatLock>where(
                buildRangeSpecification(bookingFilter, SeatLock_.bookingId)).and(
                        (root, query, cb) -> cb.or(
                                cb.isFalse(root.get(SeatLock_.isDeleted)),
                                cb.isNull(root.get(SeatLock_.isDeleted))));

        return seatLockRepository.findAll(specification);
    }
    
}
