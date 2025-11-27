package com.ridehub.route.service.dto.request;

import java.io.Serializable;

/**
 * Request DTO for canceling seat holds by group or booking.
 */
public class CancelGroupRequestDTO implements Serializable {

    private String lockGroupId; // optional
    private Long bookingId;     // optional (require at least one)

    public CancelGroupRequestDTO() {}

    public CancelGroupRequestDTO(String lockGroupId, Long bookingId) {
        this.lockGroupId = lockGroupId;
        this.bookingId = bookingId;
    }

    public String getLockGroupId() {
        return lockGroupId;
    }

    public void setLockGroupId(String lockGroupId) {
        this.lockGroupId = lockGroupId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String toString() {
        return "CancelGroupRequestDTO{" +
            "lockGroupId='" + lockGroupId + '\'' +
            ", bookingId=" + bookingId +
            '}';
    }
}
