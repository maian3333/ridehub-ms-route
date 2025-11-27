package com.ridehub.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for holding seats without bookingId yet.
 */
public class SeatHoldRequestDTO implements Serializable {

    @NotNull
    private Long tripId;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;

    @NotBlank
    private String lockGroupId;  // your idemKey prefix

    @NotBlank
    private String idemKey;      // for API-call idempotency (can reuse lockGroupId)

    private Integer holdTtlSec;  // default 180..300

    private Long userId;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public String getLockGroupId() {
        return lockGroupId;
    }

    public void setLockGroupId(String lockGroupId) {
        this.lockGroupId = lockGroupId;
    }

    public String getIdemKey() {
        return idemKey;
    }

    public void setIdemKey(String idemKey) {
        this.idemKey = idemKey;
    }

    public Integer getHoldTtlSec() {
        return holdTtlSec;
    }

    public void setHoldTtlSec(Integer holdTtlSec) {
        this.holdTtlSec = holdTtlSec;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SeatHoldRequestDTO{" +
            "tripId=" + tripId +
            ", seatNumbers=" + seatNumbers +
            ", lockGroupId='" + lockGroupId + '\'' +
            ", idemKey='" + idemKey + '\'' +
            ", holdTtlSec=" + holdTtlSec +
            ", userId=" + userId +
            '}';
    }
}
