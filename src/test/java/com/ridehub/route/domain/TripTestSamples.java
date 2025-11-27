package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TripTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Trip getTripSample1() {
        return new Trip().id(1L).tripCode("tripCode1").deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Trip getTripSample2() {
        return new Trip().id(2L).tripCode("tripCode2").deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Trip getTripRandomSampleGenerator() {
        return new Trip().id(longCount.incrementAndGet()).tripCode(UUID.randomUUID().toString()).deletedBy(UUID.randomUUID());
    }
}
