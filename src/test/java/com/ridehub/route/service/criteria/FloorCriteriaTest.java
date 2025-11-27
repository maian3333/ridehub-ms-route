package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FloorCriteriaTest {

    @Test
    void newFloorCriteriaHasAllFiltersNullTest() {
        var floorCriteria = new FloorCriteria();
        assertThat(floorCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void floorCriteriaFluentMethodsCreatesFiltersTest() {
        var floorCriteria = new FloorCriteria();

        setAllFilters(floorCriteria);

        assertThat(floorCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void floorCriteriaCopyCreatesNullFilterTest() {
        var floorCriteria = new FloorCriteria();
        var copy = floorCriteria.copy();

        assertThat(floorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(floorCriteria)
        );
    }

    @Test
    void floorCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var floorCriteria = new FloorCriteria();
        setAllFilters(floorCriteria);

        var copy = floorCriteria.copy();

        assertThat(floorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(floorCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var floorCriteria = new FloorCriteria();

        assertThat(floorCriteria).hasToString("FloorCriteria{}");
    }

    private static void setAllFilters(FloorCriteria floorCriteria) {
        floorCriteria.id();
        floorCriteria.floorNo();
        floorCriteria.priceFactorFloor();
        floorCriteria.createdAt();
        floorCriteria.updatedAt();
        floorCriteria.isDeleted();
        floorCriteria.deletedAt();
        floorCriteria.deletedBy();
        floorCriteria.seatMapId();
        floorCriteria.distinct();
    }

    private static Condition<FloorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFloorNo()) &&
                condition.apply(criteria.getPriceFactorFloor()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FloorCriteria> copyFiltersAre(FloorCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFloorNo(), copy.getFloorNo()) &&
                condition.apply(criteria.getPriceFactorFloor(), copy.getPriceFactorFloor()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapId(), copy.getSeatMapId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
