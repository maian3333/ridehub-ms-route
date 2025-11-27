package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SeatMapCriteriaTest {

    @Test
    void newSeatMapCriteriaHasAllFiltersNullTest() {
        var seatMapCriteria = new SeatMapCriteria();
        assertThat(seatMapCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void seatMapCriteriaFluentMethodsCreatesFiltersTest() {
        var seatMapCriteria = new SeatMapCriteria();

        setAllFilters(seatMapCriteria);

        assertThat(seatMapCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void seatMapCriteriaCopyCreatesNullFilterTest() {
        var seatMapCriteria = new SeatMapCriteria();
        var copy = seatMapCriteria.copy();

        assertThat(seatMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(seatMapCriteria)
        );
    }

    @Test
    void seatMapCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var seatMapCriteria = new SeatMapCriteria();
        setAllFilters(seatMapCriteria);

        var copy = seatMapCriteria.copy();

        assertThat(seatMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(seatMapCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var seatMapCriteria = new SeatMapCriteria();

        assertThat(seatMapCriteria).hasToString("SeatMapCriteria{}");
    }

    private static void setAllFilters(SeatMapCriteria seatMapCriteria) {
        seatMapCriteria.id();
        seatMapCriteria.name();
        seatMapCriteria.createdAt();
        seatMapCriteria.updatedAt();
        seatMapCriteria.isDeleted();
        seatMapCriteria.deletedAt();
        seatMapCriteria.deletedBy();
        seatMapCriteria.seatMapImgId();
        seatMapCriteria.distinct();
    }

    private static Condition<SeatMapCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapImgId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SeatMapCriteria> copyFiltersAre(SeatMapCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapImgId(), copy.getSeatMapImgId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
