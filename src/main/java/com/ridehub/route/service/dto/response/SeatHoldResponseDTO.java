package com.ridehub.route.service.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for seat hold operations.
 */
public class SeatHoldResponseDTO implements Serializable {

    private String status;                 // "HELD" | "REJECTED"
    private String message;
    private Instant expiresAt;             // earliest expiry across created/returned holds
    private List<String> heldSeats;        // normalized seats that are actually HELD

    public SeatHoldResponseDTO() {}

    public SeatHoldResponseDTO(String status, String message, Instant expiresAt, List<String> heldSeats) {
        this.status = status;
        this.message = message;
        this.expiresAt = expiresAt;
        this.heldSeats = heldSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<String> getHeldSeats() {
        return heldSeats;
    }

    public void setHeldSeats(List<String> heldSeats) {
        this.heldSeats = heldSeats;
    }

    @Override
    public String toString() {
        return "SeatHoldResponseDTO{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", expiresAt=" + expiresAt +
            ", heldSeats=" + heldSeats +
            '}';
    }
}
