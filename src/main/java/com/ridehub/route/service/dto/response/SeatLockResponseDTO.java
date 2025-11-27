package com.ridehub.route.service.dto.response;

import java.io.Serializable;

/**
 * Response DTO for seat lock operations.
 */
public class SeatLockResponseDTO implements Serializable {

    private String status; // "HELD" or "REJECTED"
    private String message;
    private Long bookingId;
    private Long tripId;
    private String lockId;
    private Long expiresAt;

    public SeatLockResponseDTO() {}

    public SeatLockResponseDTO(String status, String message, Long bookingId, Long tripId) {
        this.status = status;
        this.message = message;
        this.bookingId = bookingId;
        this.tripId = tripId;
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

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "SeatLockResponseDTO{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", bookingId=" + bookingId +
            ", tripId=" + tripId +
            ", lockId='" + lockId + '\'' +
            ", expiresAt=" + expiresAt +
            '}';
    }
}
