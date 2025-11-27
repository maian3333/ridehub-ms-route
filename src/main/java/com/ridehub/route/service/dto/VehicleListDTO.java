package com.ridehub.route.service.dto;

import com.ridehub.route.domain.enumeration.VehicleType;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for displaying vehicle list information combining Vehicle, Route, Driver,
 * and SeatMap data for the vehicle management interface.
 */
public class VehicleListDTO implements Serializable {

    private Long vehicleId;
    private String plateNumber;
    private VehicleType vehicleType;
    private String brand;
    private String description;
    private Integer seatCount;
    private String currentRoute; // Current route code (e.g., "SG-CT")
    private String currentRouteDescription; // Route description (e.g., "TP.HCM - Cần Thơ")
    private String driverName; // Main driver name
    private Long driverId;
    private String driverLicenseClass;
    private Integer driverYearsExperience;
    private String status; // Vehicle status (Active, Inactive, Maintenance, etc.)
    private Instant createdAt;
    private Instant updatedAt;

    // Default constructor
    public VehicleListDTO() {}

    // Constructor for repository queries
    public VehicleListDTO(Long vehicleId, String plateNumber, VehicleType vehicleType, 
                         String brand, String description, Integer seatCount,
                         String currentRoute, String currentRouteDescription,
                         String driverName, Long driverId, String driverLicenseClass,
                         Integer driverYearsExperience, Instant createdAt, Instant updatedAt) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.description = description;
        this.seatCount = seatCount;
        this.currentRoute = currentRoute;
        this.currentRouteDescription = currentRouteDescription;
        this.driverName = driverName;
        this.driverId = driverId;
        this.driverLicenseClass = driverLicenseClass;
        this.driverYearsExperience = driverYearsExperience;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = determineStatus();
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
    }

    public String getCurrentRouteDescription() {
        return currentRouteDescription;
    }

    public void setCurrentRouteDescription(String currentRouteDescription) {
        this.currentRouteDescription = currentRouteDescription;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverLicenseClass() {
        return driverLicenseClass;
    }

    public void setDriverLicenseClass(String driverLicenseClass) {
        this.driverLicenseClass = driverLicenseClass;
    }

    public Integer getDriverYearsExperience() {
        return driverYearsExperience;
    }

    public void setDriverYearsExperience(Integer driverYearsExperience) {
        this.driverYearsExperience = driverYearsExperience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    /**
     * Determine vehicle status based on current assignments and state
     */
    private String determineStatus() {
        if (driverId != null && currentRoute != null) {
            return "Hoạt động"; // Active - has driver and route assigned
        } else if (driverId == null && currentRoute != null) {
            return "Chờ tài xế"; // Waiting for driver
        } else if (driverId != null && currentRoute == null) {
            return "Chờ tuyến"; // Waiting for route assignment
        } else {
            return "Không hoạt động"; // Inactive
        }
    }

    @Override
    public String toString() {
        return "VehicleListDTO{" +
                "vehicleId=" + vehicleId +
                ", plateNumber='" + plateNumber + '\'' +
                ", vehicleType=" + vehicleType +
                ", brand='" + brand + '\'' +
                ", seatCount=" + seatCount +
                ", currentRoute='" + currentRoute + '\'' +
                ", driverName='" + driverName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
