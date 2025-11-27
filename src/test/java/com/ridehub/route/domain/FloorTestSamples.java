package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FloorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Floor getFloorSample1() {
        return new Floor().id(1L).floorNo(1).deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Floor getFloorSample2() {
        return new Floor().id(2L).floorNo(2).deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Floor getFloorRandomSampleGenerator() {
        return new Floor().id(longCount.incrementAndGet()).floorNo(intCount.incrementAndGet()).deletedBy(UUID.randomUUID());
    }
}
