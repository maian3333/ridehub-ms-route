package com.ridehub.route.domain;

import static com.ridehub.route.domain.DistrictTestSamples.*;
import static com.ridehub.route.domain.ProvinceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.route.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProvinceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Province.class);
        Province province1 = getProvinceSample1();
        Province province2 = new Province();
        assertThat(province1).isNotEqualTo(province2);

        province2.setId(province1.getId());
        assertThat(province1).isEqualTo(province2);

        province2 = getProvinceSample2();
        assertThat(province1).isNotEqualTo(province2);
    }

    @Test
    void districtsTest() {
        Province province = getProvinceRandomSampleGenerator();
        District districtBack = getDistrictRandomSampleGenerator();

        province.addDistricts(districtBack);
        assertThat(province.getDistricts()).containsOnly(districtBack);
        assertThat(districtBack.getProvince()).isEqualTo(province);

        province.removeDistricts(districtBack);
        assertThat(province.getDistricts()).doesNotContain(districtBack);
        assertThat(districtBack.getProvince()).isNull();

        province.districts(new HashSet<>(Set.of(districtBack)));
        assertThat(province.getDistricts()).containsOnly(districtBack);
        assertThat(districtBack.getProvince()).isEqualTo(province);

        province.setDistricts(new HashSet<>());
        assertThat(province.getDistricts()).doesNotContain(districtBack);
        assertThat(districtBack.getProvince()).isNull();
    }
}
