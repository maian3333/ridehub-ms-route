package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProvinceCriteriaTest {

    @Test
    void newProvinceCriteriaHasAllFiltersNullTest() {
        var provinceCriteria = new ProvinceCriteria();
        assertThat(provinceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void provinceCriteriaFluentMethodsCreatesFiltersTest() {
        var provinceCriteria = new ProvinceCriteria();

        setAllFilters(provinceCriteria);

        assertThat(provinceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void provinceCriteriaCopyCreatesNullFilterTest() {
        var provinceCriteria = new ProvinceCriteria();
        var copy = provinceCriteria.copy();

        assertThat(provinceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(provinceCriteria)
        );
    }

    @Test
    void provinceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var provinceCriteria = new ProvinceCriteria();
        setAllFilters(provinceCriteria);

        var copy = provinceCriteria.copy();

        assertThat(provinceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(provinceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var provinceCriteria = new ProvinceCriteria();

        assertThat(provinceCriteria).hasToString("ProvinceCriteria{}");
    }

    private static void setAllFilters(ProvinceCriteria provinceCriteria) {
        provinceCriteria.id();
        provinceCriteria.provinceCode();
        provinceCriteria.name();
        provinceCriteria.nameEn();
        provinceCriteria.fullName();
        provinceCriteria.fullNameEn();
        provinceCriteria.codeName();
        provinceCriteria.administrativeUnitId();
        provinceCriteria.administrativeRegionId();
        provinceCriteria.createdAt();
        provinceCriteria.updatedAt();
        provinceCriteria.isDeleted();
        provinceCriteria.deletedAt();
        provinceCriteria.deletedBy();
        provinceCriteria.districtsId();
        provinceCriteria.distinct();
    }

    private static Condition<ProvinceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProvinceCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getNameEn()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getFullNameEn()) &&
                condition.apply(criteria.getCodeName()) &&
                condition.apply(criteria.getAdministrativeUnitId()) &&
                condition.apply(criteria.getAdministrativeRegionId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDistrictsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProvinceCriteria> copyFiltersAre(ProvinceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProvinceCode(), copy.getProvinceCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getNameEn(), copy.getNameEn()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getFullNameEn(), copy.getFullNameEn()) &&
                condition.apply(criteria.getCodeName(), copy.getCodeName()) &&
                condition.apply(criteria.getAdministrativeUnitId(), copy.getAdministrativeUnitId()) &&
                condition.apply(criteria.getAdministrativeRegionId(), copy.getAdministrativeRegionId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDistrictsId(), copy.getDistrictsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
