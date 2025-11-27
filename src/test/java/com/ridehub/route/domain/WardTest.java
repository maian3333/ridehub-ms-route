package com.ridehub.route.domain;

import static com.ridehub.route.domain.AddressTestSamples.*;
import static com.ridehub.route.domain.DistrictTestSamples.*;
import static com.ridehub.route.domain.WardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ward.class);
        Ward ward1 = getWardSample1();
        Ward ward2 = new Ward();
        assertThat(ward1).isNotEqualTo(ward2);

        ward2.setId(ward1.getId());
        assertThat(ward1).isEqualTo(ward2);

        ward2 = getWardSample2();
        assertThat(ward1).isNotEqualTo(ward2);
    }

    @Test
    void addressesTest() {
        Ward ward = getWardRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        ward.addAddresses(addressBack);
        assertThat(ward.getAddresses()).containsOnly(addressBack);
        assertThat(addressBack.getWard()).isEqualTo(ward);

        ward.removeAddresses(addressBack);
        assertThat(ward.getAddresses()).doesNotContain(addressBack);
        assertThat(addressBack.getWard()).isNull();

        ward.addresses(new HashSet<>(Set.of(addressBack)));
        assertThat(ward.getAddresses()).containsOnly(addressBack);
        assertThat(addressBack.getWard()).isEqualTo(ward);

        ward.setAddresses(new HashSet<>());
        assertThat(ward.getAddresses()).doesNotContain(addressBack);
        assertThat(addressBack.getWard()).isNull();
    }

    @Test
    void districtTest() {
        Ward ward = getWardRandomSampleGenerator();
        District districtBack = getDistrictRandomSampleGenerator();

        ward.setDistrict(districtBack);
        assertThat(ward.getDistrict()).isEqualTo(districtBack);

        ward.district(null);
        assertThat(ward.getDistrict()).isNull();
    }
}
