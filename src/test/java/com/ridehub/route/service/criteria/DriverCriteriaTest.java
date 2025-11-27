package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DriverCriteriaTest {

    @Test
    void newDriverCriteriaHasAllFiltersNullTest() {
        var driverCriteria = new DriverCriteria();
        assertThat(driverCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void driverCriteriaFluentMethodsCreatesFiltersTest() {
        var driverCriteria = new DriverCriteria();

        setAllFilters(driverCriteria);

        assertThat(driverCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void driverCriteriaCopyCreatesNullFilterTest() {
        var driverCriteria = new DriverCriteria();
        var copy = driverCriteria.copy();

        assertThat(driverCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(driverCriteria)
        );
    }

    @Test
    void driverCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var driverCriteria = new DriverCriteria();
        setAllFilters(driverCriteria);

        var copy = driverCriteria.copy();

        assertThat(driverCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(driverCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var driverCriteria = new DriverCriteria();

        assertThat(driverCriteria).hasToString("DriverCriteria{}");
    }

    private static void setAllFilters(DriverCriteria driverCriteria) {
        driverCriteria.id();
        driverCriteria.licenseClass();
        driverCriteria.yearsExperience();
        driverCriteria.createdAt();
        driverCriteria.updatedAt();
        driverCriteria.isDeleted();
        driverCriteria.deletedAt();
        driverCriteria.deletedBy();
        driverCriteria.staffId();
        driverCriteria.distinct();
    }

    private static Condition<DriverCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLicenseClass()) &&
                condition.apply(criteria.getYearsExperience()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getStaffId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DriverCriteria> copyFiltersAre(DriverCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLicenseClass(), copy.getLicenseClass()) &&
                condition.apply(criteria.getYearsExperience(), copy.getYearsExperience()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getStaffId(), copy.getStaffId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
