package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SeatCriteriaTest {

    @Test
    void newSeatCriteriaHasAllFiltersNullTest() {
        var seatCriteria = new SeatCriteria();
        assertThat(seatCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void seatCriteriaFluentMethodsCreatesFiltersTest() {
        var seatCriteria = new SeatCriteria();

        setAllFilters(seatCriteria);

        assertThat(seatCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void seatCriteriaCopyCreatesNullFilterTest() {
        var seatCriteria = new SeatCriteria();
        var copy = seatCriteria.copy();

        assertThat(seatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(seatCriteria)
        );
    }

    @Test
    void seatCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var seatCriteria = new SeatCriteria();
        setAllFilters(seatCriteria);

        var copy = seatCriteria.copy();

        assertThat(seatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(seatCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var seatCriteria = new SeatCriteria();

        assertThat(seatCriteria).hasToString("SeatCriteria{}");
    }

    private static void setAllFilters(SeatCriteria seatCriteria) {
        seatCriteria.id();
        seatCriteria.seatNo();
        seatCriteria.rowNo();
        seatCriteria.colNo();
        seatCriteria.priceFactor();
        seatCriteria.type();
        seatCriteria.createdAt();
        seatCriteria.updatedAt();
        seatCriteria.isDeleted();
        seatCriteria.deletedAt();
        seatCriteria.deletedBy();
        seatCriteria.floorId();
        seatCriteria.distinct();
    }

    private static Condition<SeatCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSeatNo()) &&
                condition.apply(criteria.getRowNo()) &&
                condition.apply(criteria.getColNo()) &&
                condition.apply(criteria.getPriceFactor()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getFloorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SeatCriteria> copyFiltersAre(SeatCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSeatNo(), copy.getSeatNo()) &&
                condition.apply(criteria.getRowNo(), copy.getRowNo()) &&
                condition.apply(criteria.getColNo(), copy.getColNo()) &&
                condition.apply(criteria.getPriceFactor(), copy.getPriceFactor()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getFloorId(), copy.getFloorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
