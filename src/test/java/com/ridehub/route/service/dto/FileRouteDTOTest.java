package com.ridehub.route.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileRouteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileRouteDTO.class);
        FileRouteDTO fileRouteDTO1 = new FileRouteDTO();
        fileRouteDTO1.setId(1L);
        FileRouteDTO fileRouteDTO2 = new FileRouteDTO();
        assertThat(fileRouteDTO1).isNotEqualTo(fileRouteDTO2);
        fileRouteDTO2.setId(fileRouteDTO1.getId());
        assertThat(fileRouteDTO1).isEqualTo(fileRouteDTO2);
        fileRouteDTO2.setId(2L);
        assertThat(fileRouteDTO1).isNotEqualTo(fileRouteDTO2);
        fileRouteDTO1.setId(null);
        assertThat(fileRouteDTO1).isNotEqualTo(fileRouteDTO2);
    }
}
