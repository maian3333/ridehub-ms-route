package com.ridehub.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Request DTO for attaching bookingId to a seat hold group.
 */
public class AttachBookingRequestDTO implements Serializable {

    @NotBlank
    private String lockGroupId;  // group prefix

    @NotNull
    private Long bookingId;

    public AttachBookingRequestDTO() {}

    public AttachBookingRequestDTO(String lockGroupId, Long bookingId) {
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
        return "AttachBookingRequestDTO{" +
            "lockGroupId='" + lockGroupId + '\'' +
            ", bookingId=" + bookingId +
            '}';
    }
}
