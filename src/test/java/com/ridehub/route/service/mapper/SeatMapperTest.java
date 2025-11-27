package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.SeatAsserts.*;
import static com.ridehub.route.domain.SeatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeatMapperTest {

    private SeatMapper seatMapper;

    @BeforeEach
    void setUp() {
        seatMapper = new SeatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSeatSample1();
        var actual = seatMapper.toEntity(seatMapper.toDto(expected));
        assertSeatAllPropertiesEquals(expected, actual);
    }
}
