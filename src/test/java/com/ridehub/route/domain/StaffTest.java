package com.ridehub.route.domain;

import static com.ridehub.route.domain.AttendantTestSamples.*;
import static com.ridehub.route.domain.DriverTestSamples.*;
import static com.ridehub.route.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StaffTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Staff.class);
        Staff staff1 = getStaffSample1();
        Staff staff2 = new Staff();
        assertThat(staff1).isNotEqualTo(staff2);

        staff2.setId(staff1.getId());
        assertThat(staff1).isEqualTo(staff2);

        staff2 = getStaffSample2();
        assertThat(staff1).isNotEqualTo(staff2);
    }

    @Test
    void driverTest() {
        Staff staff = getStaffRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        staff.setDriver(driverBack);
        assertThat(staff.getDriver()).isEqualTo(driverBack);
        assertThat(driverBack.getStaff()).isEqualTo(staff);

        staff.driver(null);
        assertThat(staff.getDriver()).isNull();
        assertThat(driverBack.getStaff()).isNull();
    }

    @Test
    void attendantTest() {
        Staff staff = getStaffRandomSampleGenerator();
        Attendant attendantBack = getAttendantRandomSampleGenerator();

        staff.setAttendant(attendantBack);
        assertThat(staff.getAttendant()).isEqualTo(attendantBack);
        assertThat(attendantBack.getStaff()).isEqualTo(staff);

        staff.attendant(null);
        assertThat(staff.getAttendant()).isNull();
        assertThat(attendantBack.getStaff()).isNull();
    }
}
