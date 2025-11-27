package com.ridehub.route.service.vm;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.ridehub.route.service.dto.AddressDTO;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.RouteDTO;

/**
 * A DTO for displaying station information with associated routes.
 */
public class StationWithRoutesVM implements Serializable {

    private Long id;
    private String name;
    private String phoneNumber;
    private String description;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isDeleted;
    private Instant deletedAt;
    private UUID deletedBy;
    private AddressDTO address;
    private FileRouteDTO stationImg;
    private List<RouteDTO> routes;
    private Long routresCount;
    // Constructors
    public StationWithRoutesVM() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public FileRouteDTO getStationImg() {
        return stationImg;
    }

    public void setStationImg(FileRouteDTO stationImg) {
        this.stationImg = stationImg;
    }

    public List<RouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDTO> routes) {
        this.routes = routes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StationWithRoutesVM)) {
            return false;
        }

        StationWithRoutesVM stationWithRoutesDTO = (StationWithRoutesVM) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stationWithRoutesDTO.id);
    }

    public Boolean isActive() {
        return this.active;
    }

    public Boolean isIsDeleted() {
        return this.isDeleted;
    }

    public Long getRoutresCount() {
        return this.routresCount;
    }

    public void setRoutresCount(Long routresCount) {
        this.routresCount = routresCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + isActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + isIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", address='" + getAddress() + "'" +
            ", stationImg='" + getStationImg() + "'" +
            ", routes='" + getRoutes() + "'" +
            ", routresCount='" + getRoutresCount() + "'" +
            "}";
    }
   
}
