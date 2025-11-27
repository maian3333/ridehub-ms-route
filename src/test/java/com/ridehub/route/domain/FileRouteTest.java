package com.ridehub.route.domain;

import static com.ridehub.route.domain.FileRouteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileRouteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileRoute.class);
        FileRoute fileRoute1 = getFileRouteSample1();
        FileRoute fileRoute2 = new FileRoute();
        assertThat(fileRoute1).isNotEqualTo(fileRoute2);

        fileRoute2.setId(fileRoute1.getId());
        assertThat(fileRoute1).isEqualTo(fileRoute2);

        fileRoute2 = getFileRouteSample2();
        assertThat(fileRoute1).isNotEqualTo(fileRoute2);
    }
}
