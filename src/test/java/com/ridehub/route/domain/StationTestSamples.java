package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Station getStationSample1() {
        return new Station()
            .id(1L)
            .name("name1")
            .phoneNumber("phoneNumber1")
            .description("description1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Station getStationSample2() {
        return new Station()
            .id(2L)
            .name("name2")
            .phoneNumber("phoneNumber2")
            .description("description2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Station getStationRandomSampleGenerator() {
        return new Station()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
