package com.ridehub.route.service.mapper;

import static com.ridehub.route.domain.RouteAsserts.*;
import static com.ridehub.route.domain.RouteTestSamples.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteMapperTest {

    private RouteMapper routeMapper;

    @BeforeEach
    void setUp() throws Exception {
        RouteMapperImpl routeMapperImpl = new RouteMapperImpl();
        StationMapperImpl stationMapperImpl = new StationMapperImpl();

        // Use reflection to set the private stationMapper field
        Field stationMapperField = RouteMapperImpl.class.getDeclaredField("stationMapper");
        stationMapperField.setAccessible(true);
        stationMapperField.set(routeMapperImpl, stationMapperImpl);

        routeMapper = routeMapperImpl;
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRouteSample1();
        var actual = routeMapper.toEntity(routeMapper.toDto(expected));
        assertRouteAllPropertiesEquals(expected, actual);
    }
}
