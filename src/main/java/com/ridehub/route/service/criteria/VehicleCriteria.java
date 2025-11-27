package com.ridehub.route.service.criteria;

import com.ridehub.route.domain.enumeration.VehicleStatus;
import com.ridehub.route.domain.enumeration.VehicleType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Vehicle} entity. This class is used
 * in {@link com.ridehub.route.web.rest.VehicleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /vehicles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VehicleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering VehicleType
     */
    public static class VehicleTypeFilter extends Filter<VehicleType> {

        public VehicleTypeFilter() {}

        public VehicleTypeFilter(VehicleTypeFilter filter) {
            super(filter);
        }

        @Override
        public VehicleTypeFilter copy() {
            return new VehicleTypeFilter(this);
        }
    }

    /**
     * Class for filtering VehicleStatus
     */
    public static class VehicleStatusFilter extends Filter<VehicleStatus> {

        public VehicleStatusFilter() {}

        public VehicleStatusFilter(VehicleStatusFilter filter) {
            super(filter);
        }

        @Override
        public VehicleStatusFilter copy() {
            return new VehicleStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private VehicleTypeFilter type;

    private BigDecimalFilter typeFactor;

    private StringFilter plateNumber;

    private StringFilter brand;

    private StringFilter description;

    private VehicleStatusFilter status;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter seatMapId;

    private LongFilter vehicleImgId;

    private Boolean distinct;

    public VehicleCriteria() {}

    public VehicleCriteria(VehicleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(VehicleTypeFilter::copy).orElse(null);
        this.typeFactor = other.optionalTypeFactor().map(BigDecimalFilter::copy).orElse(null);
        this.plateNumber = other.optionalPlateNumber().map(StringFilter::copy).orElse(null);
        this.brand = other.optionalBrand().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(VehicleStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.seatMapId = other.optionalSeatMapId().map(LongFilter::copy).orElse(null);
        this.vehicleImgId = other.optionalVehicleImgId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VehicleCriteria copy() {
        return new VehicleCriteria(this);
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

    public VehicleTypeFilter getType() {
        return type;
    }

    public Optional<VehicleTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public VehicleTypeFilter type() {
        if (type == null) {
            setType(new VehicleTypeFilter());
        }
        return type;
    }

    public void setType(VehicleTypeFilter type) {
        this.type = type;
    }

    public BigDecimalFilter getTypeFactor() {
        return typeFactor;
    }

    public Optional<BigDecimalFilter> optionalTypeFactor() {
        return Optional.ofNullable(typeFactor);
    }

    public BigDecimalFilter typeFactor() {
        if (typeFactor == null) {
            setTypeFactor(new BigDecimalFilter());
        }
        return typeFactor;
    }

    public void setTypeFactor(BigDecimalFilter typeFactor) {
        this.typeFactor = typeFactor;
    }

    public StringFilter getPlateNumber() {
        return plateNumber;
    }

    public Optional<StringFilter> optionalPlateNumber() {
        return Optional.ofNullable(plateNumber);
    }

    public StringFilter plateNumber() {
        if (plateNumber == null) {
            setPlateNumber(new StringFilter());
        }
        return plateNumber;
    }

    public void setPlateNumber(StringFilter plateNumber) {
        this.plateNumber = plateNumber;
    }

    public StringFilter getBrand() {
        return brand;
    }

    public Optional<StringFilter> optionalBrand() {
        return Optional.ofNullable(brand);
    }

    public StringFilter brand() {
        if (brand == null) {
            setBrand(new StringFilter());
        }
        return brand;
    }

    public void setBrand(StringFilter brand) {
        this.brand = brand;
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

    public VehicleStatusFilter getStatus() {
        return status;
    }

    public Optional<VehicleStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public VehicleStatusFilter status() {
        if (status == null) {
            setStatus(new VehicleStatusFilter());
        }
        return status;
    }

    public void setStatus(VehicleStatusFilter status) {
        this.status = status;
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

    public LongFilter getSeatMapId() {
        return seatMapId;
    }

    public Optional<LongFilter> optionalSeatMapId() {
        return Optional.ofNullable(seatMapId);
    }

    public LongFilter seatMapId() {
        if (seatMapId == null) {
            setSeatMapId(new LongFilter());
        }
        return seatMapId;
    }

    public void setSeatMapId(LongFilter seatMapId) {
        this.seatMapId = seatMapId;
    }

    public LongFilter getVehicleImgId() {
        return vehicleImgId;
    }

    public Optional<LongFilter> optionalVehicleImgId() {
        return Optional.ofNullable(vehicleImgId);
    }

    public LongFilter vehicleImgId() {
        if (vehicleImgId == null) {
            setVehicleImgId(new LongFilter());
        }
        return vehicleImgId;
    }

    public void setVehicleImgId(LongFilter vehicleImgId) {
        this.vehicleImgId = vehicleImgId;
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
        final VehicleCriteria that = (VehicleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(typeFactor, that.typeFactor) &&
            Objects.equals(plateNumber, that.plateNumber) &&
            Objects.equals(brand, that.brand) &&
            Objects.equals(description, that.description) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(seatMapId, that.seatMapId) &&
            Objects.equals(vehicleImgId, that.vehicleImgId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            type,
            typeFactor,
            plateNumber,
            brand,
            description,
            status,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            seatMapId,
            vehicleImgId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VehicleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalTypeFactor().map(f -> "typeFactor=" + f + ", ").orElse("") +
            optionalPlateNumber().map(f -> "plateNumber=" + f + ", ").orElse("") +
            optionalBrand().map(f -> "brand=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalSeatMapId().map(f -> "seatMapId=" + f + ", ").orElse("") +
            optionalVehicleImgId().map(f -> "vehicleImgId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
