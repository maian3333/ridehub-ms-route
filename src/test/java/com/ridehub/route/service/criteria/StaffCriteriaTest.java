package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StaffCriteriaTest {

    @Test
    void newStaffCriteriaHasAllFiltersNullTest() {
        var staffCriteria = new StaffCriteria();
        assertThat(staffCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void staffCriteriaFluentMethodsCreatesFiltersTest() {
        var staffCriteria = new StaffCriteria();

        setAllFilters(staffCriteria);

        assertThat(staffCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void staffCriteriaCopyCreatesNullFilterTest() {
        var staffCriteria = new StaffCriteria();
        var copy = staffCriteria.copy();

        assertThat(staffCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(staffCriteria)
        );
    }

    @Test
    void staffCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var staffCriteria = new StaffCriteria();
        setAllFilters(staffCriteria);

        var copy = staffCriteria.copy();

        assertThat(staffCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(staffCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var staffCriteria = new StaffCriteria();

        assertThat(staffCriteria).hasToString("StaffCriteria{}");
    }

    private static void setAllFilters(StaffCriteria staffCriteria) {
        staffCriteria.id();
        staffCriteria.name();
        staffCriteria.age();
        staffCriteria.gender();
        staffCriteria.phoneNumber();
        staffCriteria.status();
        staffCriteria.createdAt();
        staffCriteria.updatedAt();
        staffCriteria.isDeleted();
        staffCriteria.deletedAt();
        staffCriteria.deletedBy();
        staffCriteria.driverId();
        staffCriteria.attendantId();
        staffCriteria.distinct();
    }

    private static Condition<StaffCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getAge()) &&
                condition.apply(criteria.getGender()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDriverId()) &&
                condition.apply(criteria.getAttendantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StaffCriteria> copyFiltersAre(StaffCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getAge(), copy.getAge()) &&
                condition.apply(criteria.getGender(), copy.getGender()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDriverId(), copy.getDriverId()) &&
                condition.apply(criteria.getAttendantId(), copy.getAttendantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
