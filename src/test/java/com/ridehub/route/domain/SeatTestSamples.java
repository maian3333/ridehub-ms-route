package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SeatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Seat getSeatSample1() {
        return new Seat().id(1L).seatNo("seatNo1").rowNo(1).colNo(1).deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Seat getSeatSample2() {
        return new Seat().id(2L).seatNo("seatNo2").rowNo(2).colNo(2).deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Seat getSeatRandomSampleGenerator() {
        return new Seat()
            .id(longCount.incrementAndGet())
            .seatNo(UUID.randomUUID().toString())
            .rowNo(intCount.incrementAndGet())
            .colNo(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
