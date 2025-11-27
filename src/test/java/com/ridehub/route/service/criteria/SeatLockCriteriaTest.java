package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SeatLockCriteriaTest {

    @Test
    void newSeatLockCriteriaHasAllFiltersNullTest() {
        var seatLockCriteria = new SeatLockCriteria();
        assertThat(seatLockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void seatLockCriteriaFluentMethodsCreatesFiltersTest() {
        var seatLockCriteria = new SeatLockCriteria();

        setAllFilters(seatLockCriteria);

        assertThat(seatLockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void seatLockCriteriaCopyCreatesNullFilterTest() {
        var seatLockCriteria = new SeatLockCriteria();
        var copy = seatLockCriteria.copy();

        assertThat(seatLockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(seatLockCriteria)
        );
    }

    @Test
    void seatLockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var seatLockCriteria = new SeatLockCriteria();
        setAllFilters(seatLockCriteria);

        var copy = seatLockCriteria.copy();

        assertThat(seatLockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(seatLockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var seatLockCriteria = new SeatLockCriteria();

        assertThat(seatLockCriteria).hasToString("SeatLockCriteria{}");
    }

    private static void setAllFilters(SeatLockCriteria seatLockCriteria) {
        seatLockCriteria.id();
        seatLockCriteria.seatNo();
        seatLockCriteria.userId();
        seatLockCriteria.status();
        seatLockCriteria.expiresAt();
        seatLockCriteria.idempotencyKey();
        seatLockCriteria.bookingId();
        seatLockCriteria.createdAt();
        seatLockCriteria.updatedAt();
        seatLockCriteria.isDeleted();
        seatLockCriteria.deletedAt();
        seatLockCriteria.deletedBy();
        seatLockCriteria.tripId();
        seatLockCriteria.distinct();
    }

    private static Condition<SeatLockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSeatNo()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getIdempotencyKey()) &&
                condition.apply(criteria.getBookingId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getTripId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SeatLockCriteria> copyFiltersAre(SeatLockCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSeatNo(), copy.getSeatNo()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getIdempotencyKey(), copy.getIdempotencyKey()) &&
                condition.apply(criteria.getBookingId(), copy.getBookingId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getTripId(), copy.getTripId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
