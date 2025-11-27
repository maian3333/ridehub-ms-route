package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VehicleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Vehicle getVehicleSample1() {
        return new Vehicle()
            .id(1L)
            .plateNumber("plateNumber1")
            .brand("brand1")
            .description("description1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Vehicle getVehicleSample2() {
        return new Vehicle()
            .id(2L)
            .plateNumber("plateNumber2")
            .brand("brand2")
            .description("description2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Vehicle getVehicleRandomSampleGenerator() {
        return new Vehicle()
            .id(longCount.incrementAndGet())
            .plateNumber(UUID.randomUUID().toString())
            .brand(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
