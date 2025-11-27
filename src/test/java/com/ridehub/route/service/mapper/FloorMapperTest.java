package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.FloorAsserts.*;
import static com.ridehub.route.domain.FloorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FloorMapperTest {

    private FloorMapper floorMapper;

    @BeforeEach
    void setUp() {
        floorMapper = new FloorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFloorSample1();
        var actual = floorMapper.toEntity(floorMapper.toDto(expected));
        assertFloorAllPropertiesEquals(expected, actual);
    }
}
