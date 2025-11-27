package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Station} entity. This
 * class is used
 * in {@link com.ridehub.route.web.rest.StationResource} to receive all the
 * possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter phoneNumber;

    private StringFilter description;

    private BooleanFilter active;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter addressId;

    private LongFilter stationImgId;

    private StringFilter provinceCode;
    private StringFilter districtCode;

    private Boolean distinct;

    public StationCriteria() {
    }

    public StationCriteria(StationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.addressId = other.optionalAddressId().map(LongFilter::copy).orElse(null);
        this.stationImgId = other.optionalStationImgId().map(LongFilter::copy).orElse(null);
        this.provinceCode = other.optionalProvinceCode().map(StringFilter::copy).orElse(null);
        this.districtCode = other.optionalDistrictCode().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StationCriteria copy() {
        return new StationCriteria(this);
    }

    public StringFilter getProvinceCode() {
        if (provinceCode == null)
            provinceCode = new StringFilter();
        return provinceCode;
    }

    public void setProvinceCode(StringFilter provinceCode) {
        this.provinceCode = provinceCode;
    }

    public StringFilter getDistrictCode() {
        if (districtCode == null)
            districtCode = new StringFilter();
        return districtCode;
    }

    public void setDistrictCode(StringFilter districtCode) {
        this.districtCode = districtCode;
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<StringFilter> optionalDistrictCode() {
        return Optional.ofNullable(districtCode);
    }

    public Optional<StringFilter> optionalProvinceCode() {
        return Optional.ofNullable(provinceCode);
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

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
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

    public LongFilter getAddressId() {
        return addressId;
    }

    public Optional<LongFilter> optionalAddressId() {
        return Optional.ofNullable(addressId);
    }

    public LongFilter addressId() {
        if (addressId == null) {
            setAddressId(new LongFilter());
        }
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
    }

    public LongFilter getStationImgId() {
        return stationImgId;
    }

    public Optional<LongFilter> optionalStationImgId() {
        return Optional.ofNullable(stationImgId);
    }

    public LongFilter stationImgId() {
        if (stationImgId == null) {
            setStationImgId(new LongFilter());
        }
        return stationImgId;
    }

    public void setStationImgId(LongFilter stationImgId) {
        this.stationImgId = stationImgId;
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
        final StationCriteria that = (StationCriteria) o;
        return (Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(description, that.description) &&
                Objects.equals(active, that.active) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(deletedAt, that.deletedAt) &&
                Objects.equals(deletedBy, that.deletedBy) &&
                Objects.equals(addressId, that.addressId) &&
                Objects.equals(stationImgId, that.stationImgId) &&
                Objects.equals(distinct, that.distinct));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                phoneNumber,
                description,
                active,
                createdAt,
                updatedAt,
                isDeleted,
                deletedAt,
                deletedBy,
                addressId,
                stationImgId,
                distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StationCriteria{" +
                optionalId().map(f -> "id=" + f + ", ").orElse("") +
                optionalName().map(f -> "name=" + f + ", ").orElse("") +
                optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
                optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
                optionalActive().map(f -> "active=" + f + ", ").orElse("") +
                optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
                optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
                optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
                optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
                optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
                optionalAddressId().map(f -> "addressId=" + f + ", ").orElse("") +
                optionalStationImgId().map(f -> "stationImgId=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
