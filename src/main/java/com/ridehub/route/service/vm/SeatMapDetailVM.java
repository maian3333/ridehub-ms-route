package com.ridehub.route.service.vm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatDTO;

public class SeatMapDetailVM implements Serializable {
    private List<FloorDTO> floors;
    private Map<Long, List<SeatDTO>> seatsByFloorId;

    public SeatMapDetailVM() {
    }

    public SeatMapDetailVM(List<FloorDTO> floors, Map<Long, List<SeatDTO>> seatsByFloorId) {
        this.floors = floors;
        this.seatsByFloorId = seatsByFloorId;
    }

    public List<FloorDTO> getFloors() {
        return floors;
    }

    public void setFloors(List<FloorDTO> floors) {
        this.floors = floors;
    }

    public Map<Long, List<SeatDTO>> getSeatsByFloorId() {
        return seatsByFloorId;
    }

    public void setSeatsByFloorId(Map<Long, List<SeatDTO>> seatsByFloorId) {
        this.seatsByFloorId = seatsByFloorId;
    }
}
