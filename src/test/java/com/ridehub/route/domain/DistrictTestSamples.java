package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DistrictTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static District getDistrictSample1() {
        return new District()
            .id(1L)
            .districtCode("districtCode1")
            .name("name1")
            .nameEn("nameEn1")
            .fullName("fullName1")
            .fullNameEn("fullNameEn1")
            .codeName("codeName1")
            .administrativeUnitId(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static District getDistrictSample2() {
        return new District()
            .id(2L)
            .districtCode("districtCode2")
            .name("name2")
            .nameEn("nameEn2")
            .fullName("fullName2")
            .fullNameEn("fullNameEn2")
            .codeName("codeName2")
            .administrativeUnitId(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static District getDistrictRandomSampleGenerator() {
        return new District()
            .id(longCount.incrementAndGet())
            .districtCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .nameEn(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .fullNameEn(UUID.randomUUID().toString())
            .codeName(UUID.randomUUID().toString())
            .administrativeUnitId(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
