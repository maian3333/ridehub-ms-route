package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeatDTO.class);
        SeatDTO seatDTO1 = new SeatDTO();
        seatDTO1.setId(1L);
        SeatDTO seatDTO2 = new SeatDTO();
        assertThat(seatDTO1).isNotEqualTo(seatDTO2);
        seatDTO2.setId(seatDTO1.getId());
        assertThat(seatDTO1).isEqualTo(seatDTO2);
        seatDTO2.setId(2L);
        assertThat(seatDTO1).isNotEqualTo(seatDTO2);
        seatDTO1.setId(null);
        assertThat(seatDTO1).isNotEqualTo(seatDTO2);
    }
}
