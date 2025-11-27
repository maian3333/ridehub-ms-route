package com.ridehub.route.web.rest;

import com.ridehub.route.broker.KafkaProducer;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.kafka.service.KafkaUtilityService;
import com.ridehub.msroute.client.api.AddressResourceMsrouteApi;
import com.ridehub.msroute.client.api.RouteResourceMsrouteApi;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ms-route-kafka")
public class MsRouteKafkaResource {

    private static final Logger LOG = LoggerFactory.getLogger(MsRouteKafkaResource.class);

    private final KafkaProducer kafkaProducer; // now only has get() + send()
    private final KafkaUtilityService kafkaUtilityService;

    public MsRouteKafkaResource(
            KafkaProducer kafkaProducer,
            KafkaUtilityService kafkaUtilityService) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaUtilityService = kafkaUtilityService;
    }

    /**
     * Test endpoint to send a simple message
     */
    @PostMapping("/publish/simple")
    public ResponseEntity<Map<String, String>> publishSimple(@RequestParam("message") String message) {
        LOG.debug("REST request to send simple message: {} to Kafka via Supplier", message);

        try {
            String messageKey = kafkaProducer.send("route.test", message);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Message queued successfully");
            response.put("messageKey", messageKey);
            response.put("payload", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing simple message: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue message: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Send a route creation event
     */
    @PostMapping("/publish/route-created")
    public ResponseEntity<Map<String, String>> publishRouteCreated(@RequestBody RouteDTO routeDTO) {
        LOG.debug("REST request to queue route created event for: {}", routeDTO);

        try {
            String messageKey = kafkaProducer.send("route.created", routeDTO);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Route created event queued successfully");
            response.put("messageKey", messageKey);
            response.put("routeId", String.valueOf(routeDTO.getId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing route created event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue route created event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Send a route updated event
     */
    @PostMapping("/publish/route-updated")
    public ResponseEntity<Map<String, String>> publishRouteUpdated(@RequestBody RouteDTO routeDTO) {
        LOG.debug("REST request to queue route updated event for: {}", routeDTO);
        try {
            String messageKey = kafkaProducer.send("route.updated", routeDTO);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Route updated event queued successfully");
            response.put("messageKey", messageKey);
            response.put("routeId", String.valueOf(routeDTO.getId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing route updated event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue route updated event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }

    }

    /**
     * Send a route deleted event
     */
    @DeleteMapping("/publish/route-deleted/{id}")
    public ResponseEntity<Map<String, String>> publishRouteDeleted(@PathVariable Long id) {
        LOG.debug("REST request to queue route deleted event for ID: {}", id);

        try {
            String messageKey = kafkaProducer.send("route.deleted", id);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Route deleted event queued successfully");
            response.put("messageKey", messageKey);
            response.put("routeId", String.valueOf(id));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing route deleted event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue route deleted event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Get Kafka utility service status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "ms_route");
        status.put("kafkaUtilityEnabled", true);
        status.put("sseClientsCount", kafkaUtilityService.getEmitters().size());
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}
