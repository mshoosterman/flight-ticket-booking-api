package com.mshoosterman.flightbookingapi.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class FlightTests {

    @Test
    void reservesAvailableSeats() {
        Flight flight = new Flight("FL100", 2);

        flight.reserveSeats(2);

        assertThat(flight.hasAvailableSeats(1)).isFalse();
    }

    @Test
    void rejectsInvalidSeatCountWhenReserving() {
        Flight flight = new Flight("FL100", 2);

        assertThatThrownBy(() -> flight.reserveSeats(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount must be at least 1");
    }

    @Test
    void rejectsReservingMoreSeatsThanAvailable() {
        Flight flight = new Flight("FL100", 2);

        assertThatThrownBy(() -> flight.reserveSeats(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot reserve more seats than available");
    }

    @Test
    void releasesBookedSeats() {
        Flight flight = new Flight("FL100", 2);
        flight.reserveSeats(2);

        flight.releaseSeats(1);

        assertThat(flight.hasAvailableSeats(1)).isTrue();
        assertThat(flight.hasAvailableSeats(2)).isFalse();
    }

    @Test
    void rejectsInvalidSeatCountWhenReleasing() {
        Flight flight = new Flight("FL100", 2);

        assertThatThrownBy(() -> flight.releaseSeats(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount must be at least 1");
    }

    @Test
    void rejectsReleasingMoreSeatsThanBooked() {
        Flight flight = new Flight("FL100", 2);
        flight.reserveSeats(1);

        assertThatThrownBy(() -> flight.releaseSeats(2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot release more seats than booked");
    }
}
