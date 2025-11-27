package com.ridehub.route.domain;

import static com.ridehub.route.domain.FloorTestSamples.*;
import static com.ridehub.route.domain.SeatMapTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FloorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Floor.class);
        Floor floor1 = getFloorSample1();
        Floor floor2 = new Floor();
        assertThat(floor1).isNotEqualTo(floor2);

        floor2.setId(floor1.getId());
        assertThat(floor1).isEqualTo(floor2);

        floor2 = getFloorSample2();
        assertThat(floor1).isNotEqualTo(floor2);
    }

    @Test
    void seatMapTest() {
        Floor floor = getFloorRandomSampleGenerator();
        SeatMap seatMapBack = getSeatMapRandomSampleGenerator();

        floor.setSeatMap(seatMapBack);
        assertThat(floor.getSeatMap()).isEqualTo(seatMapBack);

        floor.seatMap(null);
        assertThat(floor.getSeatMap()).isNull();
    }
}
