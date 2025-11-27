package com.ridehub.route.service.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for seat validation and lock operations.
 * Includes validation results, lock status, and pricing information.
 */
public class SeatValidateLockResponseDTO implements Serializable {

    private String status; // HELD, REJECTED
    private String message;
    private String lockGroupId;
    private Long tripId;
    private Instant expiresAt;
    
    // Pricing information
    private PricingInfo pricing;

    public static class PricingInfo implements Serializable {
        private BigDecimal baseFare;
        private BigDecimal finalPrice;
        private List<String> appliedPromotions;
        private String currency;

        public PricingInfo() {}

        public PricingInfo(BigDecimal baseFare, BigDecimal finalPrice, 
                          List<String> appliedPromotions, String currency) {
            this.baseFare = baseFare;
            this.finalPrice = finalPrice;
            this.appliedPromotions = appliedPromotions;
            this.currency = currency;
        }

        public BigDecimal getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(BigDecimal baseFare) {
            this.baseFare = baseFare;
        }

        public BigDecimal getFinalPrice() {
            return finalPrice;
        }

        public void setFinalPrice(BigDecimal finalPrice) {
            this.finalPrice = finalPrice;
        }

        public List<String> getAppliedPromotions() {
            return appliedPromotions;
        }

        public void setAppliedPromotions(List<String> appliedPromotions) {
            this.appliedPromotions = appliedPromotions;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @Override
        public String toString() {
            return "PricingInfo{" +
                "baseFare=" + baseFare +
                ", finalPrice=" + finalPrice +
                ", appliedPromotions=" + appliedPromotions +
                ", currency='" + currency + '\'' +
                '}';
        }
    }

    public SeatValidateLockResponseDTO() {}

    public SeatValidateLockResponseDTO(String status, String message, Long tripId) {
        this.status = status;
        this.message = message;
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

    public String getLockGroupId() {
        return lockGroupId;
    }

    public void setLockGroupId(String lockGroupId) {
        this.lockGroupId = lockGroupId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public PricingInfo getPricing() {
        return pricing;
    }

    public void setPricing(PricingInfo pricing) {
        this.pricing = pricing;
    }

    @Override
    public String toString() {
        return "SeatValidateLockResponse{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", lockGroupId='" + lockGroupId + '\'' +
            ", tripId=" + tripId +
            ", expiresAt=" + expiresAt +
            ", pricing=" + pricing +
            '}';
    }
}
