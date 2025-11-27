package com.ridehub.route.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProvinceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Province getProvinceSample1() {
        return new Province()
            .id(1L)
            .provinceCode("provinceCode1")
            .name("name1")
            .nameEn("nameEn1")
            .fullName("fullName1")
            .fullNameEn("fullNameEn1")
            .codeName("codeName1")
            .administrativeUnitId(1)
            .administrativeRegionId(1)
            .deletedBy(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Province getProvinceSample2() {
        return new Province()
            .id(2L)
            .provinceCode("provinceCode2")
            .name("name2")
            .nameEn("nameEn2")
            .fullName("fullName2")
            .fullNameEn("fullNameEn2")
            .codeName("codeName2")
            .administrativeUnitId(2)
            .administrativeRegionId(2)
            .deletedBy(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Province getProvinceRandomSampleGenerator() {
        return new Province()
            .id(longCount.incrementAndGet())
            .provinceCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .nameEn(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .fullNameEn(UUID.randomUUID().toString())
            .codeName(UUID.randomUUID().toString())
            .administrativeUnitId(intCount.incrementAndGet())
            .administrativeRegionId(intCount.incrementAndGet())
            .deletedBy(UUID.randomUUID());
    }
}
