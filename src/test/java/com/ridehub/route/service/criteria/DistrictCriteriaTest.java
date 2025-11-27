package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DistrictCriteriaTest {

    @Test
    void newDistrictCriteriaHasAllFiltersNullTest() {
        var districtCriteria = new DistrictCriteria();
        assertThat(districtCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void districtCriteriaFluentMethodsCreatesFiltersTest() {
        var districtCriteria = new DistrictCriteria();

        setAllFilters(districtCriteria);

        assertThat(districtCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void districtCriteriaCopyCreatesNullFilterTest() {
        var districtCriteria = new DistrictCriteria();
        var copy = districtCriteria.copy();

        assertThat(districtCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(districtCriteria)
        );
    }

    @Test
    void districtCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var districtCriteria = new DistrictCriteria();
        setAllFilters(districtCriteria);

        var copy = districtCriteria.copy();

        assertThat(districtCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(districtCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var districtCriteria = new DistrictCriteria();

        assertThat(districtCriteria).hasToString("DistrictCriteria{}");
    }

    private static void setAllFilters(DistrictCriteria districtCriteria) {
        districtCriteria.id();
        districtCriteria.districtCode();
        districtCriteria.name();
        districtCriteria.nameEn();
        districtCriteria.fullName();
        districtCriteria.fullNameEn();
        districtCriteria.codeName();
        districtCriteria.administrativeUnitId();
        districtCriteria.createdAt();
        districtCriteria.updatedAt();
        districtCriteria.isDeleted();
        districtCriteria.deletedAt();
        districtCriteria.deletedBy();
        districtCriteria.wardsId();
        districtCriteria.provinceId();
        districtCriteria.distinct();
    }

    private static Condition<DistrictCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDistrictCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getNameEn()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getFullNameEn()) &&
                condition.apply(criteria.getCodeName()) &&
                condition.apply(criteria.getAdministrativeUnitId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getWardsId()) &&
                condition.apply(criteria.getProvinceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DistrictCriteria> copyFiltersAre(DistrictCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDistrictCode(), copy.getDistrictCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getNameEn(), copy.getNameEn()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getFullNameEn(), copy.getFullNameEn()) &&
                condition.apply(criteria.getCodeName(), copy.getCodeName()) &&
                condition.apply(criteria.getAdministrativeUnitId(), copy.getAdministrativeUnitId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getWardsId(), copy.getWardsId()) &&
                condition.apply(criteria.getProvinceId(), copy.getProvinceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
