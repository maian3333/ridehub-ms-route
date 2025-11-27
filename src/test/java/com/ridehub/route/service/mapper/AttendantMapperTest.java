package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.AttendantAsserts.*;
import static com.ridehub.route.domain.AttendantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttendantMapperTest {

    private AttendantMapper attendantMapper;

    @BeforeEach
    void setUp() {
        attendantMapper = new AttendantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAttendantSample1();
        var actual = attendantMapper.toEntity(attendantMapper.toDto(expected));
        assertAttendantAllPropertiesEquals(expected, actual);
    }
}
