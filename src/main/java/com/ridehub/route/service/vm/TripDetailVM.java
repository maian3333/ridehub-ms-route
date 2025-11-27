package com.ridehub.route.service.vm;

import java.util.List;

import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.dto.SeatLockDTO;

public class TripDetailVM {

    private TripDTO tripDTO;
    private VehicleDetailVM detailVM;
    private List<SeatLockDTO> SeatLockDTOs;

    public TripDetailVM(TripDTO trip, VehicleDetailVM detailVM,
            List<SeatLockDTO> SeatLockDTOs) {
        this.tripDTO = trip;
        this.detailVM = detailVM;
        this.SeatLockDTOs = SeatLockDTOs;
    }

    public TripDTO getTripDTO() {
        return tripDTO;
    }

    public void setTripDTO(TripDTO tripDTO) {
        this.tripDTO = tripDTO;
    }

    public VehicleDetailVM getDetailVM() {
        return detailVM;
    }

    public void setDetailVM(VehicleDetailVM detailVM) {
        this.detailVM = detailVM;
    }

    public List<SeatLockDTO> getSeatLockDTOs() {
        return SeatLockDTOs;
    }

    public void setSeatLockDTOs(List<SeatLockDTO> SeatLockDTOs) {
        this.SeatLockDTOs = SeatLockDTOs;
    }

}
