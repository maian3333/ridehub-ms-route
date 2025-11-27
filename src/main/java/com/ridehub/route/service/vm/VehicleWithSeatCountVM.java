package com.ridehub.route.service.vm;

import java.io.Serializable;

import com.ridehub.route.service.dto.VehicleDTO;

public class VehicleWithSeatCountVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seatsCount;
    private VehicleDTO vehicleDTO;

    public VehicleWithSeatCountVM() {
    }

    public VehicleWithSeatCountVM(Long seatsCount, VehicleDTO vehicleDTO) {
        this.seatsCount = seatsCount;
        this.vehicleDTO = vehicleDTO;
    }

    public Long getSeatsCount() {
        return seatsCount;
    }

    public void setSeatsCount(Long seatsCount) {
        this.seatsCount = seatsCount;
    }

    public VehicleDTO getVehicleDTO() {
        return vehicleDTO;
    }

    public void setVehicleDTO(VehicleDTO vehicleDTO) {
        this.vehicleDTO = vehicleDTO;
    }
}
