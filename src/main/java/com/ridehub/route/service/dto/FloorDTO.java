package com.ridehub.route.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.route.domain.Floor} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FloorDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer floorNo;

    private BigDecimal priceFactorFloor;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private SeatMapDTO seatMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(Integer floorNo) {
        this.floorNo = floorNo;
    }

    public BigDecimal getPriceFactorFloor() {
        return priceFactorFloor;
    }

    public void setPriceFactorFloor(BigDecimal priceFactorFloor) {
        this.priceFactorFloor = priceFactorFloor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public SeatMapDTO getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(SeatMapDTO seatMap) {
        this.seatMap = seatMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloorDTO)) {
            return false;
        }

        FloorDTO floorDTO = (FloorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, floorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FloorDTO{" +
            "id=" + getId() +
            ", floorNo=" + getFloorNo() +
            ", priceFactorFloor=" + getPriceFactorFloor() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", seatMap=" + getSeatMap() +
            "}";
    }
}
