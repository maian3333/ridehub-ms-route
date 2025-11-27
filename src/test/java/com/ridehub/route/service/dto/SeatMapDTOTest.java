package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeatMapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeatMapDTO.class);
        SeatMapDTO seatMapDTO1 = new SeatMapDTO();
        seatMapDTO1.setId(1L);
        SeatMapDTO seatMapDTO2 = new SeatMapDTO();
        assertThat(seatMapDTO1).isNotEqualTo(seatMapDTO2);
        seatMapDTO2.setId(seatMapDTO1.getId());
        assertThat(seatMapDTO1).isEqualTo(seatMapDTO2);
        seatMapDTO2.setId(2L);
        assertThat(seatMapDTO1).isNotEqualTo(seatMapDTO2);
        seatMapDTO1.setId(null);
        assertThat(seatMapDTO1).isNotEqualTo(seatMapDTO2);
    }
}
