package com.ridehub.route.service.vm;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.dto.VehicleDTO;

public class VehicleDetailVM implements Serializable {
    private VehicleDTO vehicle; // your existing DTO
    private List<FloorDTO> floors; // separate list (not inside SeatMapDTO)
    private Map<Long, List<SeatDTO>> seatsByFloorId; // seats grouped by floor

    public VehicleDetailVM(VehicleDTO vehicle, List<FloorDTO> floors, Map<Long, List<SeatDTO>> seatsByFloorId) {
        this.vehicle = vehicle;
        this.floors = floors;
        this.seatsByFloorId = seatsByFloorId;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public List<FloorDTO> getFloors() {
        return floors;
    }

    public Map<Long, List<SeatDTO>> getSeatsByFloorId() {
        return seatsByFloorId;
    }
}
