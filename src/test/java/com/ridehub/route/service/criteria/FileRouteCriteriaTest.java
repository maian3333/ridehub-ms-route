package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FileRouteCriteriaTest {

    @Test
    void newFileRouteCriteriaHasAllFiltersNullTest() {
        var fileRouteCriteria = new FileRouteCriteria();
        assertThat(fileRouteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void fileRouteCriteriaFluentMethodsCreatesFiltersTest() {
        var fileRouteCriteria = new FileRouteCriteria();

        setAllFilters(fileRouteCriteria);

        assertThat(fileRouteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void fileRouteCriteriaCopyCreatesNullFilterTest() {
        var fileRouteCriteria = new FileRouteCriteria();
        var copy = fileRouteCriteria.copy();

        assertThat(fileRouteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(fileRouteCriteria)
        );
    }

    @Test
    void fileRouteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var fileRouteCriteria = new FileRouteCriteria();
        setAllFilters(fileRouteCriteria);

        var copy = fileRouteCriteria.copy();

        assertThat(fileRouteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(fileRouteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var fileRouteCriteria = new FileRouteCriteria();

        assertThat(fileRouteCriteria).hasToString("FileRouteCriteria{}");
    }

    private static void setAllFilters(FileRouteCriteria fileRouteCriteria) {
        fileRouteCriteria.id();
        fileRouteCriteria.bucket();
        fileRouteCriteria.objectKey();
        fileRouteCriteria.contentType();
        fileRouteCriteria.size();
        fileRouteCriteria.createdAt();
        fileRouteCriteria.updatedAt();
        fileRouteCriteria.isDeleted();
        fileRouteCriteria.deletedAt();
        fileRouteCriteria.deletedBy();
        fileRouteCriteria.distinct();
    }

    private static Condition<FileRouteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBucket()) &&
                condition.apply(criteria.getObjectKey()) &&
                condition.apply(criteria.getContentType()) &&
                condition.apply(criteria.getSize()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FileRouteCriteria> copyFiltersAre(FileRouteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBucket(), copy.getBucket()) &&
                condition.apply(criteria.getObjectKey(), copy.getObjectKey()) &&
                condition.apply(criteria.getContentType(), copy.getContentType()) &&
                condition.apply(criteria.getSize(), copy.getSize()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
