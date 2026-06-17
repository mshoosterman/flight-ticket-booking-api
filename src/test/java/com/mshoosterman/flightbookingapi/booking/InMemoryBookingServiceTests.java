package com.mshoosterman.flightbookingapi.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryBookingServiceTests {

    private final InMemoryBookingService bookingService = new InMemoryBookingService();

    @Test
    void createsBooking() {
        Booking booking = bookingService.createBooking(request("FL100", 1));

        assertThat(booking.id()).isNotNull();
        assertThat(booking.flightNumber()).isEqualTo("FL100");
        assertThat(booking.passengerName()).isEqualTo("Alex Morgan");
        assertThat(booking.passengerEmail()).isEqualTo("alex@example.com");
        assertThat(booking.seatCount()).isEqualTo(1);
    }

    @Test
    void preventsOverbooking() {
        bookingService.createBooking(request("FL300", 1));

        assertThatThrownBy(() -> bookingService.createBooking(request("FL300", 1)))
                .isInstanceOf(InMemoryBookingService.FlightFullException.class)
                .hasMessage("Not enough seats available");
    }

    @Test
    void rejectsBookingWhenRequestedSeatsExceedCapacity() {
        assertThatThrownBy(() -> bookingService.createBooking(request("FL300", 2)))
                .isInstanceOf(InMemoryBookingService.FlightFullException.class)
                .hasMessage("Not enough seats available");
    }

    @Test
    void rejectsVeryLargeSeatCountAfterSeatsAreBooked() {
        bookingService.createBooking(request("FL100", 1));

        assertThatThrownBy(() -> bookingService.createBooking(request("FL100", Integer.MAX_VALUE)))
                .isInstanceOf(InMemoryBookingService.FlightFullException.class)
                .hasMessage("Not enough seats available");
    }

    @Test
    void rejectsUnknownFlightNumber() {
        assertThatThrownBy(() -> bookingService.createBooking(request("UNKNOWN", 1)))
                .isInstanceOf(InMemoryBookingService.FlightNotFoundException.class)
                .hasMessage("Flight not found: UNKNOWN");
    }

    @Test
    void cancelsBooking() {
        Booking booking = bookingService.createBooking(request("FL200", 2));

        bookingService.cancelBooking(booking.id());

        assertThatThrownBy(() -> bookingService.cancelBooking(booking.id()))
                .isInstanceOf(InMemoryBookingService.BookingNotFoundException.class)
                .hasMessage("Booking not found: " + booking.id());
    }

    @Test
    void cancelingBookingReleasesSeatsForLaterBooking() {
        Booking booking = bookingService.createBooking(request("FL300", 1));
        bookingService.cancelBooking(booking.id());

        Booking laterBooking = bookingService.createBooking(request("FL300", 1));

        assertThat(laterBooking.id()).isNotNull();
        assertThat(laterBooking.id()).isNotEqualTo(booking.id());
        assertThat(laterBooking.flightNumber()).isEqualTo("FL300");
    }

    @Test
    void rejectsUnknownBookingIdWhenCanceling() {
        UUID bookingId = UUID.randomUUID();

        assertThatThrownBy(() -> bookingService.cancelBooking(bookingId))
                .isInstanceOf(InMemoryBookingService.BookingNotFoundException.class)
                .hasMessage("Booking not found: " + bookingId);
    }

    private CreateBookingRequest request(String flightNumber, int seatCount) {
        return new CreateBookingRequest(
                flightNumber,
                "Alex Morgan",
                "alex@example.com",
                seatCount
        );
    }
}
