package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VehicleCriteriaTest {

    @Test
    void newVehicleCriteriaHasAllFiltersNullTest() {
        var vehicleCriteria = new VehicleCriteria();
        assertThat(vehicleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void vehicleCriteriaFluentMethodsCreatesFiltersTest() {
        var vehicleCriteria = new VehicleCriteria();

        setAllFilters(vehicleCriteria);

        assertThat(vehicleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void vehicleCriteriaCopyCreatesNullFilterTest() {
        var vehicleCriteria = new VehicleCriteria();
        var copy = vehicleCriteria.copy();

        assertThat(vehicleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(vehicleCriteria)
        );
    }

    @Test
    void vehicleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var vehicleCriteria = new VehicleCriteria();
        setAllFilters(vehicleCriteria);

        var copy = vehicleCriteria.copy();

        assertThat(vehicleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(vehicleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var vehicleCriteria = new VehicleCriteria();

        assertThat(vehicleCriteria).hasToString("VehicleCriteria{}");
    }

    private static void setAllFilters(VehicleCriteria vehicleCriteria) {
        vehicleCriteria.id();
        vehicleCriteria.type();
        vehicleCriteria.typeFactor();
        vehicleCriteria.plateNumber();
        vehicleCriteria.brand();
        vehicleCriteria.description();
        vehicleCriteria.status();
        vehicleCriteria.createdAt();
        vehicleCriteria.updatedAt();
        vehicleCriteria.isDeleted();
        vehicleCriteria.deletedAt();
        vehicleCriteria.deletedBy();
        vehicleCriteria.seatMapId();
        vehicleCriteria.vehicleImgId();
        vehicleCriteria.distinct();
    }

    private static Condition<VehicleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getTypeFactor()) &&
                condition.apply(criteria.getPlateNumber()) &&
                condition.apply(criteria.getBrand()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapId()) &&
                condition.apply(criteria.getVehicleImgId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VehicleCriteria> copyFiltersAre(VehicleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getTypeFactor(), copy.getTypeFactor()) &&
                condition.apply(criteria.getPlateNumber(), copy.getPlateNumber()) &&
                condition.apply(criteria.getBrand(), copy.getBrand()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getSeatMapId(), copy.getSeatMapId()) &&
                condition.apply(criteria.getVehicleImgId(), copy.getVehicleImgId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
