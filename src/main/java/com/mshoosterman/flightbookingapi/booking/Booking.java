package com.mshoosterman.flightbookingapi.booking;

import java.util.UUID;

record Booking(UUID id, String flightNumber, String passengerName, String passengerEmail, int seatCount) {
}
