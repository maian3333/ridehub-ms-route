package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttendantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Attendant getAttendantSample1() {
        return new Attendant().id(1L).deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Attendant getAttendantSample2() {
        return new Attendant().id(2L).deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Attendant getAttendantRandomSampleGenerator() {
        return new Attendant().id(longCount.incrementAndGet()).deletedBy(UUID.randomUUID());
    }
}
