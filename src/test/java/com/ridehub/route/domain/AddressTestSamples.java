package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address().id(1L).streetAddress("streetAddress1").deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Address getAddressSample2() {
        return new Address().id(2L).streetAddress("streetAddress2").deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address().id(longCount.incrementAndGet()).streetAddress(UUID.randomUUID().toString()).deletedBy(UUID.randomUUID());
    }
}
