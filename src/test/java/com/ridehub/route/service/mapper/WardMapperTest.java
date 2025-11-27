package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.WardAsserts.*;
import static com.ridehub.route.domain.WardTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WardMapperTest {

    private WardMapper wardMapper;

    @BeforeEach
    void setUp() {
        wardMapper = new WardMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWardSample1();
        var actual = wardMapper.toEntity(wardMapper.toDto(expected));
        assertWardAllPropertiesEquals(expected, actual);
    }
}
