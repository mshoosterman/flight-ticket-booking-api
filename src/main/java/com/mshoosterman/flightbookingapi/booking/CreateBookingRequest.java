package com.mshoosterman.flightbookingapi.booking;

import jakarta.validation.constraints.NotBlank;

record CreateBookingRequest(@NotBlank String flightNumber) {
}
