package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SeatLockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SeatLock getSeatLockSample1() {
        return new SeatLock()
            .id(1L)
            .seatNo("seatNo1")
            .userId(1L)
            .idempotencyKey("idempotencyKey1")
            .bookingId(1L)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static SeatLock getSeatLockSample2() {
        return new SeatLock()
            .id(2L)
            .seatNo("seatNo2")
            .userId(2L)
            .idempotencyKey("idempotencyKey2")
            .bookingId(2L)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static SeatLock getSeatLockRandomSampleGenerator() {
        return new SeatLock()
            .id(longCount.incrementAndGet())
            .seatNo(UUID.randomUUID().toString())
            .userId(longCount.incrementAndGet())
            .idempotencyKey(UUID.randomUUID().toString())
            .bookingId(longCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
