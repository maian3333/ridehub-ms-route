package com.ridehub.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for seat validation and lock operations.
 * This combines seat existence validation with atomic seat locking.
 */
public class SeatValidateLockRequestDTO implements Serializable {

    @NotNull
    private Long tripId;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;

    @NotBlank
    private String idemKey;

    // Optional promo code for pricing calculation
    private String promoCode;

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

    public String getIdemKey() {
        return idemKey;
    }

    public void setIdemKey(String idemKey) {
        this.idemKey = idemKey;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    @Override
    public String toString() {
        return "SeatValidateLockRequest{" +
            "tripId=" + tripId +
            ", seatNumbers=" + seatNumbers +
            ", idemKey='" + idemKey + '\'' +
            ", promoCode='" + promoCode + '\'' +
            '}';
    }
}
