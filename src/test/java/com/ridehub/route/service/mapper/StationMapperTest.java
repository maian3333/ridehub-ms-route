package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.StationAsserts.*;
import static com.ridehub.route.domain.StationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StationMapperTest {

    private StationMapper stationMapper;

    @BeforeEach
    void setUp() {
        stationMapper = new StationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStationSample1();
        var actual = stationMapper.toEntity(stationMapper.toDto(expected));
        assertStationAllPropertiesEquals(expected, actual);
    }
}
