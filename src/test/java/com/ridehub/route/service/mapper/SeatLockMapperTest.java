package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.SeatLockAsserts.*;
import static com.ridehub.route.domain.SeatLockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeatLockMapperTest {

    private SeatLockMapper seatLockMapper;

    @BeforeEach
    void setUp() {
        seatLockMapper = new SeatLockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSeatLockSample1();
        var actual = seatLockMapper.toEntity(seatLockMapper.toDto(expected));
        assertSeatLockAllPropertiesEquals(expected, actual);
    }
}
