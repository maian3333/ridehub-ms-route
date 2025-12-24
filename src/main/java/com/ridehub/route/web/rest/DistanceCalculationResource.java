package com.ridehub.route.web.rest;

import com.ridehub.route.service.DistanceCalculationService;
import com.ridehub.route.service.dto.DistanceCalculationRequestDTO;
import com.ridehub.route.service.dto.DistanceCalculationResponseDTO;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for calculating distances between addresses using Google Maps API.
 */
@RestController
@RequestMapping("/api/distances")
public class DistanceCalculationResource {

    private static final Logger LOG = LoggerFactory.getLogger(DistanceCalculationResource.class);

    private static final String ENTITY_NAME = "distanceCalculation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DistanceCalculationService distanceCalculationService;

    public DistanceCalculationResource(DistanceCalculationService distanceCalculationService) {
        this.distanceCalculationService = distanceCalculationService;
    }

    /**
     * {@code POST /api/distances/calculate} : Calculate distance between two addresses.
     *
     * @param requestDTO the distance calculation request containing origin, destination, and travel preferences
     * @return the ResponseEntity with status 200 (OK) and with distance calculation result in body
     * @throws BadRequestAlertException if the request is invalid or the service is not configured
     */
    @PostMapping("/calculate")
    public ResponseEntity<DistanceCalculationResponseDTO> calculateDistance(@Valid @RequestBody DistanceCalculationRequestDTO requestDTO) {
        LOG.debug("REST request to calculate distance from '{}' to '{}' using {} profile",
                requestDTO.getOriginAddress(),
                requestDTO.getDestinationAddress(),
                requestDTO.getVehicleProfile());

        // Validate service configuration
        if (!distanceCalculationService.isConfigured()) {
            throw new BadRequestAlertException("Distance calculation service is not properly configured. GraphHopper API key is missing.", ENTITY_NAME, "service.not.configured");
        }

        DistanceCalculationResponseDTO response = distanceCalculationService.calculateDistance(requestDTO);

        // Log the result
        if (response.getErrorMessage() != null) {
            LOG.warn("Distance calculation failed with status: {} - {}", response.getStatus(), response.getErrorMessage());
        } else {
            LOG.info("Distance calculation completed successfully with status: {}", response.getStatus());
        }

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, "calculated"))
                .body(response);
    }

    /**
     * {@code GET /api/distances/health} : Check if distance calculation service is properly configured.
     *
     * @return the ResponseEntity with status 200 (OK) and service status in body
     */
    @GetMapping("/health")
    public ResponseEntity<ServiceStatusDTO> checkServiceHealth() {
        boolean isConfigured = distanceCalculationService.isConfigured();
        ServiceStatusDTO status = new ServiceStatusDTO();
        status.setService("distance-calculation");
        status.setConfigured(isConfigured);
        status.setStatus(isConfigured ? "UP" : "DOWN");
        status.setMessage(isConfigured ? "Service is properly configured" : "GraphHopper API key is not configured");

        return ResponseEntity.ok(status);
    }

    /**
     * DTO for service health status.
     */
    public static class ServiceStatusDTO {
        private String service;
        private String status;
        private boolean configured;
        private String message;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isConfigured() {
            return configured;
        }

        public void setConfigured(boolean configured) {
            this.configured = configured;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}