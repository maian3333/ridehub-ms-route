package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.TripAsserts.*;
import static com.ridehub.route.domain.TripTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripMapperTest {

    private TripMapper tripMapper;

    @BeforeEach
    void setUp() {
        tripMapper = new TripMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTripSample1();
        var actual = tripMapper.toEntity(tripMapper.toDto(expected));
        assertTripAllPropertiesEquals(expected, actual);
    }
}
