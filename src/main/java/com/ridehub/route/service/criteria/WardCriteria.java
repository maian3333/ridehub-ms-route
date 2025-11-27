package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Ward} entity. This class is used
 * in {@link com.ridehub.route.web.rest.WardResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /wards?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WardCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter wardCode;

    private StringFilter name;

    private StringFilter nameEn;

    private StringFilter fullName;

    private StringFilter fullNameEn;

    private StringFilter codeName;

    private IntegerFilter administrativeUnitId;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter addressesId;

    private LongFilter districtId;

    private Boolean distinct;

    public WardCriteria() {}

    public WardCriteria(WardCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.wardCode = other.optionalWardCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.nameEn = other.optionalNameEn().map(StringFilter::copy).orElse(null);
        this.fullName = other.optionalFullName().map(StringFilter::copy).orElse(null);
        this.fullNameEn = other.optionalFullNameEn().map(StringFilter::copy).orElse(null);
        this.codeName = other.optionalCodeName().map(StringFilter::copy).orElse(null);
        this.administrativeUnitId = other.optionalAdministrativeUnitId().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.addressesId = other.optionalAddressesId().map(LongFilter::copy).orElse(null);
        this.districtId = other.optionalDistrictId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WardCriteria copy() {
        return new WardCriteria(this);
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

    public StringFilter getWardCode() {
        return wardCode;
    }

    public Optional<StringFilter> optionalWardCode() {
        return Optional.ofNullable(wardCode);
    }

    public StringFilter wardCode() {
        if (wardCode == null) {
            setWardCode(new StringFilter());
        }
        return wardCode;
    }

    public void setWardCode(StringFilter wardCode) {
        this.wardCode = wardCode;
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

    public LongFilter getAddressesId() {
        return addressesId;
    }

    public Optional<LongFilter> optionalAddressesId() {
        return Optional.ofNullable(addressesId);
    }

    public LongFilter addressesId() {
        if (addressesId == null) {
            setAddressesId(new LongFilter());
        }
        return addressesId;
    }

    public void setAddressesId(LongFilter addressesId) {
        this.addressesId = addressesId;
    }

    public LongFilter getDistrictId() {
        return districtId;
    }

    public Optional<LongFilter> optionalDistrictId() {
        return Optional.ofNullable(districtId);
    }

    public LongFilter districtId() {
        if (districtId == null) {
            setDistrictId(new LongFilter());
        }
        return districtId;
    }

    public void setDistrictId(LongFilter districtId) {
        this.districtId = districtId;
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
        final WardCriteria that = (WardCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(wardCode, that.wardCode) &&
            Objects.equals(name, that.name) &&
            Objects.equals(nameEn, that.nameEn) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(fullNameEn, that.fullNameEn) &&
            Objects.equals(codeName, that.codeName) &&
            Objects.equals(administrativeUnitId, that.administrativeUnitId) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(addressesId, that.addressesId) &&
            Objects.equals(districtId, that.districtId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            wardCode,
            name,
            nameEn,
            fullName,
            fullNameEn,
            codeName,
            administrativeUnitId,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            addressesId,
            districtId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WardCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalWardCode().map(f -> "wardCode=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalNameEn().map(f -> "nameEn=" + f + ", ").orElse("") +
            optionalFullName().map(f -> "fullName=" + f + ", ").orElse("") +
            optionalFullNameEn().map(f -> "fullNameEn=" + f + ", ").orElse("") +
            optionalCodeName().map(f -> "codeName=" + f + ", ").orElse("") +
            optionalAdministrativeUnitId().map(f -> "administrativeUnitId=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalAddressesId().map(f -> "addressesId=" + f + ", ").orElse("") +
            optionalDistrictId().map(f -> "districtId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
