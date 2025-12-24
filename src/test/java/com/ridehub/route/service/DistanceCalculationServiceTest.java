package com.ridehub.route.service;

import com.ridehub.route.config.ApplicationProperties;
import com.ridehub.route.service.dto.DistanceCalculationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DistanceCalculationServiceTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.GraphHopper graphHopperProperties;

    private DistanceCalculationService distanceCalculationService;

    @BeforeEach
    void setUp() {
        when(applicationProperties.getGraphHopper()).thenReturn(graphHopperProperties);
        when(graphHopperProperties.getApiKey()).thenReturn("test-api-key");
        when(graphHopperProperties.getBaseUrl()).thenReturn("https://graphhopper.com/api/1");
        when(graphHopperProperties.getConnectTimeout()).thenReturn(5000);
        when(graphHopperProperties.getReadTimeout()).thenReturn(10000);

        distanceCalculationService = new DistanceCalculationService(applicationProperties);
    }

    @Test
    void shouldReturnConfiguredWhenApiKeyIsPresent() {
        when(graphHopperProperties.getApiKey()).thenReturn("test-api-key");

        boolean isConfigured = distanceCalculationService.isConfigured();

        assertThat(isConfigured).isTrue();
    }

    @Test
    void shouldReturnNotConfiguredWhenApiKeyIsMissing() {
        when(graphHopperProperties.getApiKey()).thenReturn(null);

        boolean isConfigured = distanceCalculationService.isConfigured();

        assertThat(isConfigured).isFalse();
    }

    @Test
    void shouldReturnNotConfiguredWhenApiKeyIsEmpty() {
        when(graphHopperProperties.getApiKey()).thenReturn("   ");

        boolean isConfigured = distanceCalculationService.isConfigured();

        assertThat(isConfigured).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenInitializingWithoutApiKey() {
        when(graphHopperProperties.getApiKey()).thenReturn(null);

        assertThatThrownBy(() -> new DistanceCalculationService(applicationProperties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("GraphHopper API key is not configured");
    }

    @Test
    void shouldGenerateCacheKeyCorrectly() {
        DistanceCalculationRequestDTO request = new DistanceCalculationRequestDTO();
        request.setOriginAddress("Hanoi, Vietnam");
        request.setDestinationAddress("Ho Chi Minh City, Vietnam");
        request.setVehicleProfile(DistanceCalculationRequestDTO.VehicleProfile.CAR);
        request.setLanguage("en");
        request.setEnableElevation(false);
        request.setPointsEncoded(true);

        String cacheKey = DistanceCalculationService.generateCacheKey(request);

        assertThat(cacheKey).startsWith("distance:");
        assertThat(cacheKey).contains("hanoi, vietnam|ho chi minh city, vietnam|CAR|en|false|true");
    }
}