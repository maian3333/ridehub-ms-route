package com.ridehub.route.domain;

import static com.ridehub.route.domain.AddressTestSamples.*;
import static com.ridehub.route.domain.FileRouteTestSamples.*;
import static com.ridehub.route.domain.StationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Station.class);
        Station station1 = getStationSample1();
        Station station2 = new Station();
        assertThat(station1).isNotEqualTo(station2);

        station2.setId(station1.getId());
        assertThat(station1).isEqualTo(station2);

        station2 = getStationSample2();
        assertThat(station1).isNotEqualTo(station2);
    }

    @Test
    void addressTest() {
        Station station = getStationRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        station.setAddress(addressBack);
        assertThat(station.getAddress()).isEqualTo(addressBack);

        station.address(null);
        assertThat(station.getAddress()).isNull();
    }

    @Test
    void stationImgTest() {
        Station station = getStationRandomSampleGenerator();
        FileRoute fileRouteBack = getFileRouteRandomSampleGenerator();

        station.setStationImg(fileRouteBack);
        assertThat(station.getStationImg()).isEqualTo(fileRouteBack);

        station.stationImg(null);
        assertThat(station.getStationImg()).isNull();
    }
}
