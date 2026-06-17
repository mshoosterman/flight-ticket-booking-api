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

    synchronized Booking createBooking(CreateBookingRequest request) {
        String flightNumber = request.flightNumber();
        Flight flight = flights.get(flightNumber);
        if (flight == null) {
            throw new FlightNotFoundException(flightNumber);
        }
        if (!flight.hasAvailableSeats(request.seatCount())) {
            throw new FlightFullException();
        }

        flight.reserveSeats(request.seatCount());
        Booking booking = new Booking(
                UUID.randomUUID(),
                flight.flightNumber(),
                request.passengerName(),
                request.passengerEmail(),
                request.seatCount()
        );
        bookings.put(booking.id(), booking);
        return booking;
    }

    synchronized void cancelBooking(UUID bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException(bookingId);
        }

        flights.get(booking.flightNumber()).releaseSeats(booking.seatCount());
    }

    private void seedFlight(String flightNumber, int capacity) {
        flights.put(flightNumber, new Flight(flightNumber, capacity));
    }

    static class FlightNotFoundException extends RuntimeException {

        FlightNotFoundException(String flightNumber) {
            super("Flight not found: " + flightNumber);
        }
    }

    static class FlightFullException extends RuntimeException {

        FlightFullException() {
            super("Not enough seats available");
        }
    }

    static class BookingNotFoundException extends RuntimeException {

        BookingNotFoundException(UUID bookingId) {
            super("Booking not found: " + bookingId);
        }
    }
}
