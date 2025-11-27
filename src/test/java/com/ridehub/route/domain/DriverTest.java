package com.ridehub.route.domain;

import static com.ridehub.route.domain.DriverTestSamples.*;
import static com.ridehub.route.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DriverTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Driver.class);
        Driver driver1 = getDriverSample1();
        Driver driver2 = new Driver();
        assertThat(driver1).isNotEqualTo(driver2);

        driver2.setId(driver1.getId());
        assertThat(driver1).isEqualTo(driver2);

        driver2 = getDriverSample2();
        assertThat(driver1).isNotEqualTo(driver2);
    }

    @Test
    void staffTest() {
        Driver driver = getDriverRandomSampleGenerator();
        Staff staffBack = getStaffRandomSampleGenerator();

        driver.setStaff(staffBack);
        assertThat(driver.getStaff()).isEqualTo(staffBack);

        driver.staff(null);
        assertThat(driver.getStaff()).isNull();
    }
}
