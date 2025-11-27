package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RouteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Route getRouteSample1() {
        return new Route().id(1L).routeCode("routeCode1").deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Route getRouteSample2() {
        return new Route().id(2L).routeCode("routeCode2").deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Route getRouteRandomSampleGenerator() {
        return new Route().id(longCount.incrementAndGet()).routeCode(UUID.randomUUID().toString()).deletedBy(UUID.randomUUID());
    }
}
