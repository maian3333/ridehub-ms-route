package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttendantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttendantDTO.class);
        AttendantDTO attendantDTO1 = new AttendantDTO();
        attendantDTO1.setId(1L);
        AttendantDTO attendantDTO2 = new AttendantDTO();
        assertThat(attendantDTO1).isNotEqualTo(attendantDTO2);
        attendantDTO2.setId(attendantDTO1.getId());
        assertThat(attendantDTO1).isEqualTo(attendantDTO2);
        attendantDTO2.setId(2L);
        assertThat(attendantDTO1).isNotEqualTo(attendantDTO2);
        attendantDTO1.setId(null);
        assertThat(attendantDTO1).isNotEqualTo(attendantDTO2);
    }
}
