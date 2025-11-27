package com.ridehub.route.service.dto.request;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for creating and updating routes based on station IDs with flag-based logic.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RouteStationRequestDTO implements Serializable {

    private Long id; // Only used for update operations

    @NotNull
    @Size(max = 40)
    private String routeCode;

    private BigDecimal distanceKm;

    @NotNull
    private Long stationId;

    @NotNull
    private Boolean isOrigin; // true = set as origin, false = set as destination

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Boolean getIsOrigin() {
        return isOrigin;
    }

    public void setIsOrigin(Boolean isOrigin) {
        this.isOrigin = isOrigin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouteStationRequestDTO)) {
            return false;
        }

        RouteStationRequestDTO that = (RouteStationRequestDTO) o;
        return Objects.equals(this.routeCode, that.routeCode) &&
               Objects.equals(this.stationId, that.stationId) &&
               Objects.equals(this.isOrigin, that.isOrigin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeCode, stationId, isOrigin);
    }

    @Override
    public String toString() {
        return "RouteStationRequestDTO{" +
            "id=" + getId() +
            ", routeCode='" + getRouteCode() + "'" +
            ", distanceKm=" + getDistanceKm() +
            ", stationId=" + getStationId() +
            ", isOrigin=" + getIsOrigin() +
            "}";
    }
}
