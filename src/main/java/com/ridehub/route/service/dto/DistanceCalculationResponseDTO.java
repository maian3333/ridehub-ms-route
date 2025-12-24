package com.ridehub.route.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * DTO for distance calculation response from Google Maps API.
 */
public class DistanceCalculationResponseDTO implements Serializable {

    private String status;

    private String errorMessage;

    private List<DistanceElement> elements;

    private Instant calculatedAt;

    public static class DistanceElement {
        private String status;

        private Distance distance;

        private Duration duration;

        private Duration durationInTraffic;

        @JsonProperty("fare")
        private Fare fare;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public Duration getDurationInTraffic() {
            return durationInTraffic;
        }

        public void setDurationInTraffic(Duration durationInTraffic) {
            this.durationInTraffic = durationInTraffic;
        }

        public Fare getFare() {
            return fare;
        }

        public void setFare(Fare fare) {
            this.fare = fare;
        }
    }

    public static class Distance {
        private String text;

        private BigDecimal value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

    public static class Duration {
        private String text;

        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public static class Fare {
        private String currency;

        private BigDecimal value;

        private String text;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<DistanceElement> getElements() {
        return elements;
    }

    public void setElements(List<DistanceElement> elements) {
        this.elements = elements;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistanceCalculationResponseDTO)) {
            return false;
        }

        DistanceCalculationResponseDTO that = (DistanceCalculationResponseDTO) o;
        return Objects.equals(this.status, that.status) &&
               Objects.equals(this.errorMessage, that.errorMessage) &&
               Objects.equals(this.elements, that.elements) &&
               Objects.equals(this.calculatedAt, that.calculatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, errorMessage, elements, calculatedAt);
    }

    @Override
    public String toString() {
        return "DistanceCalculationResponseDTO{" +
                "status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", elements=" + elements +
                ", calculatedAt=" + calculatedAt +
                '}';
    }
}