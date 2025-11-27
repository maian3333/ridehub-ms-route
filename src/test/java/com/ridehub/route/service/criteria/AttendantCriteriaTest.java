package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AttendantCriteriaTest {

    @Test
    void newAttendantCriteriaHasAllFiltersNullTest() {
        var attendantCriteria = new AttendantCriteria();
        assertThat(attendantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void attendantCriteriaFluentMethodsCreatesFiltersTest() {
        var attendantCriteria = new AttendantCriteria();

        setAllFilters(attendantCriteria);

        assertThat(attendantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void attendantCriteriaCopyCreatesNullFilterTest() {
        var attendantCriteria = new AttendantCriteria();
        var copy = attendantCriteria.copy();

        assertThat(attendantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(attendantCriteria)
        );
    }

    @Test
    void attendantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var attendantCriteria = new AttendantCriteria();
        setAllFilters(attendantCriteria);

        var copy = attendantCriteria.copy();

        assertThat(attendantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(attendantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var attendantCriteria = new AttendantCriteria();

        assertThat(attendantCriteria).hasToString("AttendantCriteria{}");
    }

    private static void setAllFilters(AttendantCriteria attendantCriteria) {
        attendantCriteria.id();
        attendantCriteria.createdAt();
        attendantCriteria.updatedAt();
        attendantCriteria.isDeleted();
        attendantCriteria.deletedAt();
        attendantCriteria.deletedBy();
        attendantCriteria.staffId();
        attendantCriteria.distinct();
    }

    private static Condition<AttendantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
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

    private static Condition<AttendantCriteria> copyFiltersAre(AttendantCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
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
