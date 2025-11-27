package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeatLockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeatLockDTO.class);
        SeatLockDTO seatLockDTO1 = new SeatLockDTO();
        seatLockDTO1.setId(1L);
        SeatLockDTO seatLockDTO2 = new SeatLockDTO();
        assertThat(seatLockDTO1).isNotEqualTo(seatLockDTO2);
        seatLockDTO2.setId(seatLockDTO1.getId());
        assertThat(seatLockDTO1).isEqualTo(seatLockDTO2);
        seatLockDTO2.setId(2L);
        assertThat(seatLockDTO1).isNotEqualTo(seatLockDTO2);
        seatLockDTO1.setId(null);
        assertThat(seatLockDTO1).isNotEqualTo(seatLockDTO2);
    }
}
