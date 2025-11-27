package com.ridehub.route.domain;

import static com.ridehub.route.domain.DistrictTestSamples.*;
import static com.ridehub.route.domain.ProvinceTestSamples.*;
import static com.ridehub.route.domain.WardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DistrictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(District.class);
        District district1 = getDistrictSample1();
        District district2 = new District();
        assertThat(district1).isNotEqualTo(district2);

        district2.setId(district1.getId());
        assertThat(district1).isEqualTo(district2);

        district2 = getDistrictSample2();
        assertThat(district1).isNotEqualTo(district2);
    }

    @Test
    void wardsTest() {
        District district = getDistrictRandomSampleGenerator();
        Ward wardBack = getWardRandomSampleGenerator();

        district.addWards(wardBack);
        assertThat(district.getWards()).containsOnly(wardBack);
        assertThat(wardBack.getDistrict()).isEqualTo(district);

        district.removeWards(wardBack);
        assertThat(district.getWards()).doesNotContain(wardBack);
        assertThat(wardBack.getDistrict()).isNull();

        district.wards(new HashSet<>(Set.of(wardBack)));
        assertThat(district.getWards()).containsOnly(wardBack);
        assertThat(wardBack.getDistrict()).isEqualTo(district);

        district.setWards(new HashSet<>());
        assertThat(district.getWards()).doesNotContain(wardBack);
        assertThat(wardBack.getDistrict()).isNull();
    }

    @Test
    void provinceTest() {
        District district = getDistrictRandomSampleGenerator();
        Province provinceBack = getProvinceRandomSampleGenerator();

        district.setProvince(provinceBack);
        assertThat(district.getProvince()).isEqualTo(provinceBack);

        district.province(null);
        assertThat(district.getProvince()).isNull();
    }
}
