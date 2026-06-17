package com.mshoosterman.flightbookingapi.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record CreateBookingRequest(
        @NotBlank String flightNumber,
        @NotBlank String passengerName,
        @NotBlank @Email String passengerEmail,
        @NotNull @Min(1) Integer seatCount
) {
}
