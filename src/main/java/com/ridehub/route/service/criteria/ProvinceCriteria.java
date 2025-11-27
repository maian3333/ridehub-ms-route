package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Province} entity. This class is used
 * in {@link com.ridehub.route.web.rest.ProvinceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /provinces?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProvinceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter provinceCode;

    private StringFilter name;

    private StringFilter nameEn;

    private StringFilter fullName;

    private StringFilter fullNameEn;

    private StringFilter codeName;

    private IntegerFilter administrativeUnitId;

    private IntegerFilter administrativeRegionId;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter districtsId;

    private Boolean distinct;

    public ProvinceCriteria() {}

    public ProvinceCriteria(ProvinceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.provinceCode = other.optionalProvinceCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.nameEn = other.optionalNameEn().map(StringFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.fullNameEn = other.optionalFullNameEn().map(StringFilter::copy).orElse(null);
        this.codeName = other.optionalCodeName().map(StringFilter::copy).orElse(null);
        this.administrativeUnitId = other.optionalAdministrativeUnitId().map(IntegerFilter::copy).orElse(null);
        this.administrativeRegionId = other.optionalAdministrativeRegionId().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.districtsId = other.optionalDistrictsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProvinceCriteria copy() {
        return new ProvinceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getProvinceCode() {
        return provinceCode;
    }

    public Optional<StringFilter> optionalProvinceCode() {
        return Optional.ofNullable(provinceCode);
    }

    public StringFilter provinceCode() {
        if (provinceCode == null) {
            setProvinceCode(new StringFilter());
        }
        return provinceCode;
    }

    public void setProvinceCode(StringFilter provinceCode) {
        this.provinceCode = provinceCode;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getNameEn() {
        return nameEn;
    }

    public Optional<StringFilter> optionalNameEn() {
        return Optional.ofNullable(nameEn);
    }

    public StringFilter nameEn() {
        if (nameEn == null) {
            setNameEn(new StringFilter());
        }
        return nameEn;
    }

    public void setNameEn(StringFilter nameEn) {
        this.nameEn = nameEn;
    }

    public StringFilter getFullName() {
        return fullName;
    }

    public Optional<StringFilter> optionalFullName() {
        return Optional.ofNullable(fullName);
    }

    public StringFilter fullName() {
        if (fullName == null) {
            setFullName(new StringFilter());
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getFullNameEn() {
        return fullNameEn;
    }

    public Optional<StringFilter> optionalFullNameEn() {
        return Optional.ofNullable(fullNameEn);
    }

    public StringFilter fullNameEn() {
        if (fullNameEn == null) {
            setFullNameEn(new StringFilter());
        }
        return fullNameEn;
    }

    public void setFullNameEn(StringFilter fullNameEn) {
        this.fullNameEn = fullNameEn;
    }

    public StringFilter getCodeName() {
        return codeName;
    }

    public Optional<StringFilter> optionalCodeName() {
        return Optional.ofNullable(codeName);
    }

    public StringFilter codeName() {
        if (codeName == null) {
            setCodeName(new StringFilter());
        }
        return codeName;
    }

    public void setCodeName(StringFilter codeName) {
        this.codeName = codeName;
    }

    public IntegerFilter getAdministrativeUnitId() {
        return administrativeUnitId;
    }

    public Optional<IntegerFilter> optionalAdministrativeUnitId() {
        return Optional.ofNullable(administrativeUnitId);
    }

    public IntegerFilter administrativeUnitId() {
        if (administrativeUnitId == null) {
            setAdministrativeUnitId(new IntegerFilter());
        }
        return administrativeUnitId;
    }

    public void setAdministrativeUnitId(IntegerFilter administrativeUnitId) {
        this.administrativeUnitId = administrativeUnitId;
    }

    public IntegerFilter getAdministrativeRegionId() {
        return administrativeRegionId;
    }

    public Optional<IntegerFilter> optionalAdministrativeRegionId() {
        return Optional.ofNullable(administrativeRegionId);
    }

    public IntegerFilter administrativeRegionId() {
        if (administrativeRegionId == null) {
            setAdministrativeRegionId(new IntegerFilter());
        }
        return administrativeRegionId;
    }

    public void setAdministrativeRegionId(IntegerFilter administrativeRegionId) {
        this.administrativeRegionId = administrativeRegionId;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUIDFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<UUIDFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public UUIDFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new UUIDFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(UUIDFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LongFilter getDistrictsId() {
        return districtsId;
    }

    public Optional<LongFilter> optionalDistrictsId() {
        return Optional.ofNullable(districtsId);
    }

    public LongFilter districtsId() {
        if (districtsId == null) {
            setDistrictsId(new LongFilter());
        }
        return districtsId;
    }

    public void setDistrictsId(LongFilter districtsId) {
        this.districtsId = districtsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProvinceCriteria that = (ProvinceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(provinceCode, that.provinceCode) &&
            Objects.equals(name, that.name) &&
            Objects.equals(nameEn, that.nameEn) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(fullNameEn, that.fullNameEn) &&
            Objects.equals(codeName, that.codeName) &&
            Objects.equals(administrativeUnitId, that.administrativeUnitId) &&
            Objects.equals(administrativeRegionId, that.administrativeRegionId) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(districtsId, that.districtsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            provinceCode,
            name,
            nameEn,
            fullName,
            fullNameEn,
            codeName,
            administrativeUnitId,
            administrativeRegionId,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            districtsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProvinceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProvinceCode().map(f -> "provinceCode=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalNameEn().map(f -> "nameEn=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalFullNameEn().map(f -> "fullNameEn=" + f + ", ").orElse("") +
            optionalCodeName().map(f -> "codeName=" + f + ", ").orElse("") +
            optionalAdministrativeUnitId().map(f -> "administrativeUnitId=" + f + ", ").orElse("") +
            optionalAdministrativeRegionId().map(f -> "administrativeRegionId=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDistrictsId().map(f -> "districtsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
