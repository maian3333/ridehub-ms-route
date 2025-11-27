package com.ridehub.route.domain;

import static com.ridehub.route.domain.AttendantTestSamples.*;
import static com.ridehub.route.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttendantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attendant.class);
        Attendant attendant1 = getAttendantSample1();
        Attendant attendant2 = new Attendant();
        assertThat(attendant1).isNotEqualTo(attendant2);

        attendant2.setId(attendant1.getId());
        assertThat(attendant1).isEqualTo(attendant2);

        attendant2 = getAttendantSample2();
        assertThat(attendant1).isNotEqualTo(attendant2);
    }

    @Test
    void staffTest() {
        Attendant attendant = getAttendantRandomSampleGenerator();
        Staff staffBack = getStaffRandomSampleGenerator();

        attendant.setStaff(staffBack);
        assertThat(attendant.getStaff()).isEqualTo(staffBack);

        attendant.staff(null);
        assertThat(attendant.getStaff()).isNull();
    }
}
