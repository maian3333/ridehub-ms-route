package com.ridehub.route.domain;

import static com.ridehub.route.domain.FileRouteTestSamples.*;
import static com.ridehub.route.domain.SeatMapTestSamples.*;
import static com.ridehub.route.domain.VehicleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehicle.class);
        Vehicle vehicle1 = getVehicleSample1();
        Vehicle vehicle2 = new Vehicle();
        assertThat(vehicle1).isNotEqualTo(vehicle2);

        vehicle2.setId(vehicle1.getId());
        assertThat(vehicle1).isEqualTo(vehicle2);

        vehicle2 = getVehicleSample2();
        assertThat(vehicle1).isNotEqualTo(vehicle2);
    }

    @Test
    void seatMapTest() {
        Vehicle vehicle = getVehicleRandomSampleGenerator();
        SeatMap seatMapBack = getSeatMapRandomSampleGenerator();

        vehicle.setSeatMap(seatMapBack);
        assertThat(vehicle.getSeatMap()).isEqualTo(seatMapBack);

        vehicle.seatMap(null);
        assertThat(vehicle.getSeatMap()).isNull();
    }

    @Test
    void vehicleImgTest() {
        Vehicle vehicle = getVehicleRandomSampleGenerator();
        FileRoute fileRouteBack = getFileRouteRandomSampleGenerator();

        vehicle.setVehicleImg(fileRouteBack);
        assertThat(vehicle.getVehicleImg()).isEqualTo(fileRouteBack);

        vehicle.vehicleImg(null);
        assertThat(vehicle.getVehicleImg()).isNull();
    }
}
