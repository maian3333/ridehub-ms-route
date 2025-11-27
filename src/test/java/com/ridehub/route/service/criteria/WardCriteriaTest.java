package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WardCriteriaTest {

    @Test
    void newWardCriteriaHasAllFiltersNullTest() {
        var wardCriteria = new WardCriteria();
        assertThat(wardCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void wardCriteriaFluentMethodsCreatesFiltersTest() {
        var wardCriteria = new WardCriteria();

        setAllFilters(wardCriteria);

        assertThat(wardCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void wardCriteriaCopyCreatesNullFilterTest() {
        var wardCriteria = new WardCriteria();
        var copy = wardCriteria.copy();

        assertThat(wardCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(wardCriteria)
        );
    }

    @Test
    void wardCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var wardCriteria = new WardCriteria();
        setAllFilters(wardCriteria);

        var copy = wardCriteria.copy();

        assertThat(wardCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(wardCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var wardCriteria = new WardCriteria();

        assertThat(wardCriteria).hasToString("WardCriteria{}");
    }

    private static void setAllFilters(WardCriteria wardCriteria) {
        wardCriteria.id();
        wardCriteria.wardCode();
        wardCriteria.name();
        wardCriteria.nameEn();
        wardCriteria.fullName();
        wardCriteria.fullNameEn();
        wardCriteria.codeName();
        wardCriteria.administrativeUnitId();
        wardCriteria.createdAt();
        wardCriteria.updatedAt();
        wardCriteria.isDeleted();
        wardCriteria.deletedAt();
        wardCriteria.deletedBy();
        wardCriteria.addressesId();
        wardCriteria.districtId();
        wardCriteria.distinct();
    }

    private static Condition<WardCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getWardCode()) &&
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
                condition.apply(criteria.getAddressesId()) &&
                condition.apply(criteria.getDistrictId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WardCriteria> copyFiltersAre(WardCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getWardCode(), copy.getWardCode()) &&
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
                condition.apply(criteria.getAddressesId(), copy.getAddressesId()) &&
                condition.apply(criteria.getDistrictId(), copy.getDistrictId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
