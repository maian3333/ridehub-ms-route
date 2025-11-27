package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FloorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FloorDTO.class);
        FloorDTO floorDTO1 = new FloorDTO();
        floorDTO1.setId(1L);
        FloorDTO floorDTO2 = new FloorDTO();
        assertThat(floorDTO1).isNotEqualTo(floorDTO2);
        floorDTO2.setId(floorDTO1.getId());
        assertThat(floorDTO1).isEqualTo(floorDTO2);
        floorDTO2.setId(2L);
        assertThat(floorDTO1).isNotEqualTo(floorDTO2);
        floorDTO1.setId(null);
        assertThat(floorDTO1).isNotEqualTo(floorDTO2);
    }
}
