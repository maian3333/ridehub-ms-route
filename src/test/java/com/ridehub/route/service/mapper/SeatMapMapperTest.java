package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.SeatMapAsserts.*;
import static com.ridehub.route.domain.SeatMapTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeatMapMapperTest {

    private SeatMapMapper seatMapMapper;

    @BeforeEach
    void setUp() {
        seatMapMapper = new SeatMapMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSeatMapSample1();
        var actual = seatMapMapper.toEntity(seatMapMapper.toDto(expected));
        assertSeatMapAllPropertiesEquals(expected, actual);
    }
}
