package com.mshoosterman.flightbookingapi.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record CreateBookingRequest(
        @NotBlank(message = "flightNumber is required") String flightNumber,
        @NotBlank(message = "passengerName is required") String passengerName,
        @NotBlank(message = "passengerEmail is required")
        @Email(message = "passengerEmail must be a valid email address") String passengerEmail,
        @NotNull(message = "seatCount is required")
        @Min(value = 1, message = "seatCount must be at least 1")
        @Max(value = CreateBookingRequest.MAX_SEAT_COUNT, message = "seatCount must be at most 1000") Integer seatCount
) {

    static final int MAX_SEAT_COUNT = 1000;
}
