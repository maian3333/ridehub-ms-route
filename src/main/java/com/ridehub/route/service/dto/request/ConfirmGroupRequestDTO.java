package com.ridehub.route.service.dto.request;

import java.io.Serializable;

/**
 * Request DTO for confirming seat holds by booking (preferred) or group.
 */
public class ConfirmGroupRequestDTO implements Serializable {

    private Long bookingId;     // preferred
    private String lockGroupId; // fallback

    public ConfirmGroupRequestDTO() {}

    public ConfirmGroupRequestDTO(Long bookingId, String lockGroupId) {
        this.bookingId = bookingId;
        this.lockGroupId = lockGroupId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getLockGroupId() {
        return lockGroupId;
    }

    public void setLockGroupId(String lockGroupId) {
        this.lockGroupId = lockGroupId;
    }

    @Override
    public String toString() {
        return "ConfirmGroupRequestDTO{" +
            "bookingId=" + bookingId +
            ", lockGroupId='" + lockGroupId + '\'' +
            '}';
    }
}
