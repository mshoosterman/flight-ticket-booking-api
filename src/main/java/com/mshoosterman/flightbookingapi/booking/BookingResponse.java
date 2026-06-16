package com.mshoosterman.flightbookingapi.booking;

import java.util.UUID;

record BookingResponse(UUID bookingId, String flightNumber) {
}
