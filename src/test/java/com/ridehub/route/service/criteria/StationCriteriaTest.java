package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StationCriteriaTest {

    @Test
    void newStationCriteriaHasAllFiltersNullTest() {
        var stationCriteria = new StationCriteria();
        assertThat(stationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stationCriteriaFluentMethodsCreatesFiltersTest() {
        var stationCriteria = new StationCriteria();

        setAllFilters(stationCriteria);

        assertThat(stationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stationCriteriaCopyCreatesNullFilterTest() {
        var stationCriteria = new StationCriteria();
        var copy = stationCriteria.copy();

        assertThat(stationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stationCriteria)
        );
    }

    @Test
    void stationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stationCriteria = new StationCriteria();
        setAllFilters(stationCriteria);

        var copy = stationCriteria.copy();

        assertThat(stationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stationCriteria = new StationCriteria();

        assertThat(stationCriteria).hasToString("StationCriteria{}");
    }

    private static void setAllFilters(StationCriteria stationCriteria) {
        stationCriteria.id();
        stationCriteria.name();
        stationCriteria.phoneNumber();
        stationCriteria.description();
        stationCriteria.active();
        stationCriteria.createdAt();
        stationCriteria.updatedAt();
        stationCriteria.isDeleted();
        stationCriteria.deletedAt();
        stationCriteria.deletedBy();
        stationCriteria.addressId();
        stationCriteria.stationImgId();
        stationCriteria.distinct();
    }

    private static Condition<StationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getAddressId()) &&
                condition.apply(criteria.getStationImgId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StationCriteria> copyFiltersAre(StationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getAddressId(), copy.getAddressId()) &&
                condition.apply(criteria.getStationImgId(), copy.getStationImgId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
