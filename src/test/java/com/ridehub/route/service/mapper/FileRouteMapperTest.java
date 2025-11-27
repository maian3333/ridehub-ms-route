package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.FileRouteAsserts.*;
import static com.ridehub.route.domain.FileRouteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileRouteMapperTest {

    private FileRouteMapper fileRouteMapper;

    @BeforeEach
    void setUp() {
        fileRouteMapper = new FileRouteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileRouteSample1();
        var actual = fileRouteMapper.toEntity(fileRouteMapper.toDto(expected));
        assertFileRouteAllPropertiesEquals(expected, actual);
    }
}
