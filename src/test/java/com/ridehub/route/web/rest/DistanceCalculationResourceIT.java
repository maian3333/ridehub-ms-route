package com.ridehub.route.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.service.DistanceCalculationService;
import com.ridehub.route.service.dto.DistanceCalculationRequestDTO;
import com.ridehub.route.service.dto.DistanceCalculationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureWebMvc
class DistanceCalculationResourceIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private DistanceCalculationService distanceCalculationService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private DistanceCalculationRequestDTO distanceRequest;
    private DistanceCalculationResponseDTO distanceResponse;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Setup test data
        distanceRequest = new DistanceCalculationRequestDTO();
        distanceRequest.setOriginAddress("Hanoi, Vietnam");
        distanceRequest.setDestinationAddress("Ho Chi Minh City, Vietnam");
        distanceRequest.setVehicleProfile(DistanceCalculationRequestDTO.VehicleProfile.CAR);
        distanceRequest.setLanguage("en");
        distanceRequest.setEnableElevation(false);
        distanceRequest.setPointsEncoded(true);

        distanceResponse = new DistanceCalculationResponseDTO();
        distanceResponse.setStatus("OK");
        distanceResponse.setCalculatedAt(java.time.Instant.now());

        ArrayList<DistanceCalculationResponseDTO.DistanceElement> elements = new ArrayList<>();
        DistanceCalculationResponseDTO.DistanceElement element = new DistanceCalculationResponseDTO.DistanceElement();
        element.setStatus("OK");

        DistanceCalculationResponseDTO.Distance distance = new DistanceCalculationResponseDTO.Distance();
        distance.setText("1,156 km");
        distance.setValue(java.math.BigDecimal.valueOf(1156000));
        element.setDistance(distance);

        DistanceCalculationResponseDTO.Duration duration = new DistanceCalculationResponseDTO.Duration();
        duration.setText("14 hours 28 mins");
        duration.setValue(52080);
        element.setDuration(duration);

        elements.add(element);
        distanceResponse.setElements(elements);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldCalculateDistanceSuccessfully() throws Exception {
        when(distanceCalculationService.isConfigured()).thenReturn(true);
        when(distanceCalculationService.calculateDistance(any(DistanceCalculationRequestDTO.class)))
                .thenReturn(distanceResponse);

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.elements").isArray())
                .andExpect(jsonPath("$.elements[0].status").value("OK"))
                .andExpect(jsonPath("$.elements[0].distance.text").value("1,156 km"))
                .andExpect(jsonPath("$.elements[0].distance.value").value(1156000))
                .andExpect(jsonPath("$.elements[0].duration.text").value("14 hours 28 mins"))
                .andExpect(jsonPath("$.elements[0].duration.value").value(52080));

        verify(distanceCalculationService, times(1)).calculateDistance(any(DistanceCalculationRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnBadRequestWhenServiceNotConfigured() throws Exception {
        when(distanceCalculationService.isConfigured()).thenReturn(false);

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Distance calculation service is not properly configured. Google Maps API key is missing."));

        verify(distanceCalculationService, never()).calculateDistance(any(DistanceCalculationRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnBadRequestWhenOriginAddressIsBlank() throws Exception {
        distanceRequest.setOriginAddress("");

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='originAddress')].message").value("Origin address cannot be blank"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnBadRequestWhenDestinationAddressIsBlank() throws Exception {
        distanceRequest.setDestinationAddress("");

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='destinationAddress')].message").value("Destination address cannot be blank"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnBadRequestWhenVehicleProfileIsNull() throws Exception {
        distanceRequest.setVehicleProfile(null);

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='vehicleProfile')].message").value("Vehicle profile cannot be null"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnBadRequestWhenOriginAddressTooLong() throws Exception {
        String longAddress = "a".repeat(501);
        distanceRequest.setOriginAddress(longAddress);

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='originAddress')].message").value("Origin address cannot exceed 500 characters"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldHandleServiceErrorGracefully() throws Exception {
        when(distanceCalculationService.isConfigured()).thenReturn(true);

        DistanceCalculationResponseDTO errorResponse = new DistanceCalculationResponseDTO();
        errorResponse.setStatus("ZERO_RESULTS");
        errorResponse.setErrorMessage("No route could be found between the specified origin and destination.");
        errorResponse.setCalculatedAt(java.time.Instant.now());
        errorResponse.setElements(new ArrayList<>());

        when(distanceCalculationService.calculateDistance(any(DistanceCalculationRequestDTO.class)))
                .thenReturn(errorResponse);

        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value("ZERO_RESULTS"))
                .andExpect(jsonPath("$.errorMessage").value("No route could be found between the specified origin and destination."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnServiceHealthStatusWhenConfigured() throws Exception {
        when(distanceCalculationService.isConfigured()).thenReturn(true);

        mockMvc.perform(get("/api/distances/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.service").value("distance-calculation"))
                .andExpect(jsonPath("$.configured").value(true))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Service is properly configured"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnServiceHealthStatusWhenNotConfigured() throws Exception {
        when(distanceCalculationService.isConfigured()).thenReturn(false);

        mockMvc.perform(get("/api/distances/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.service").value("distance-calculation"))
                .andExpect(jsonPath("$.configured").value(false))
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.message").value("Google Maps API key is not configured"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
        mockMvc.perform(post("/api/distances/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(distanceRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/distances/health"))
                .andExpect(status().isUnauthorized());
    }
}