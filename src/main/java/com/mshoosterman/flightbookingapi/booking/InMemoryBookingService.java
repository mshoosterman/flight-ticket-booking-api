package com.mshoosterman.flightbookingapi.booking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
class InMemoryBookingService {

    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<UUID, Booking> bookings = new HashMap<>();

    InMemoryBookingService() {
        seedFlight("FL100", 2);
        seedFlight("FL200", 3);
        seedFlight("FL300", 1);
    }

    synchronized Booking createBooking(String flightNumber) {
        Flight flight = flights.get(flightNumber);
        if (flight == null) {
            throw new FlightNotFoundException();
        }
        if (!flight.hasAvailableSeat()) {
            throw new FlightFullException();
        }

        flight.reserveSeat();
        Booking booking = new Booking(UUID.randomUUID(), flight.flightNumber());
        bookings.put(booking.id(), booking);
        return booking;
    }

    synchronized void cancelBooking(UUID bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException();
        }

        flights.get(booking.flightNumber()).releaseSeat();
    }

    private void seedFlight(String flightNumber, int capacity) {
        flights.put(flightNumber, new Flight(flightNumber, capacity));
    }

    static class FlightNotFoundException extends RuntimeException {
    }

    static class FlightFullException extends RuntimeException {
    }

    static class BookingNotFoundException extends RuntimeException {
    }
}
