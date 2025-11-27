package com.ridehub.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for seat lock confirmation and cancellation.
 */
public class SeatLockActionRequestDTO implements Serializable {

    @NotNull
    private Long bookingId;

    @NotNull
    private Long tripId;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;

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

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    @Override
    public String toString() {
        return "SeatLockActionRequestDTO{" +
            "bookingId=" + bookingId +
            ", tripId=" + tripId +
            ", seatNumbers=" + seatNumbers +
            '}';
    }
}

