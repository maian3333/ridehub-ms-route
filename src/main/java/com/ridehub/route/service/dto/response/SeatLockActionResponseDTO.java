package com.ridehub.route.service.dto.response;

import java.io.Serializable;

/**
 * Response DTO for seat lock confirmation and cancellation.
 */
public class SeatLockActionResponseDTO implements Serializable {

    private String status;
    private String message;

    public SeatLockActionResponseDTO() {
    }

    public SeatLockActionResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
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

    @Override
    public String toString() {
        return "SeatLockActionResponseDTO{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}

