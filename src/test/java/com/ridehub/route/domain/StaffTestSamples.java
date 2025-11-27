package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StaffTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Staff getStaffSample1() {
        return new Staff()
            .id(1L)
            .name("name1")
            .age(1)
            .phoneNumber("phoneNumber1")
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Staff getStaffSample2() {
        return new Staff()
            .id(2L)
            .name("name2")
            .age(2)
            .phoneNumber("phoneNumber2")
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Staff getStaffRandomSampleGenerator() {
        return new Staff()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .age(intCount.incrementAndGet())
            .phoneNumber(UUID.randomUUID().toString())
            .deletedBy(UUID.randomUUID());
    }
}
