package com.ridehub.route.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for distance calculation request between two addresses.
 */
public class DistanceCalculationRequestDTO implements Serializable {

    @NotBlank(message = "Origin address cannot be blank")
    @Size(max = 500, message = "Origin address cannot exceed 500 characters")
    private String originAddress;

    @NotBlank(message = "Destination address cannot be blank")
    @Size(max = 500, message = "Destination address cannot exceed 500 characters")
    private String destinationAddress;

    @NotNull(message = "Vehicle profile cannot be null")
    private VehicleProfile vehicleProfile = VehicleProfile.CAR;

    private String language = "en";

    private Boolean enableElevation = false;

    private Boolean pointsEncoded = true;

    public enum VehicleProfile {
        CAR,
        FOOT,
        BIKE,
        HIKING,
        MOUNTAIN_BIKE,
        RACING_BIKE,
        MOTORCYCLE,
        BUS,
        TRUCK,
        SMALL_TRUCK,
        VAN,
        TAXI
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public VehicleProfile getVehicleProfile() {
        return vehicleProfile;
    }

    public void setVehicleProfile(VehicleProfile vehicleProfile) {
        this.vehicleProfile = vehicleProfile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getEnableElevation() {
        return enableElevation;
    }

    public void setEnableElevation(Boolean enableElevation) {
        this.enableElevation = enableElevation;
    }

    public Boolean getPointsEncoded() {
        return pointsEncoded;
    }

    public void setPointsEncoded(Boolean pointsEncoded) {
        this.pointsEncoded = pointsEncoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistanceCalculationRequestDTO)) {
            return false;
        }

        DistanceCalculationRequestDTO that = (DistanceCalculationRequestDTO) o;
        return Objects.equals(this.originAddress, that.originAddress) &&
               Objects.equals(this.destinationAddress, that.destinationAddress) &&
               this.vehicleProfile == that.vehicleProfile &&
               Objects.equals(this.language, that.language) &&
               Objects.equals(this.enableElevation, that.enableElevation) &&
               Objects.equals(this.pointsEncoded, that.pointsEncoded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originAddress, destinationAddress, vehicleProfile, language, enableElevation, pointsEncoded);
    }

    @Override
    public String toString() {
        return "DistanceCalculationRequestDTO{" +
                "originAddress='" + originAddress + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", vehicleProfile=" + vehicleProfile +
                ", language='" + language + '\'' +
                ", enableElevation=" + enableElevation +
                ", pointsEncoded=" + pointsEncoded +
                '}';
    }
}