package com.mshoosterman.flightbookingapi.booking;

final class Flight {

    private final String flightNumber;
    private final int capacity;
    private int bookedSeats;

    Flight(String flightNumber, int capacity) {
        this.flightNumber = flightNumber;
        this.capacity = capacity;
    }

    String flightNumber() {
        return flightNumber;
    }

    boolean hasAvailableSeats(int seatCount) {
        return seatCount <= capacity - bookedSeats;
    }

    void reserveSeats(int seatCount) {
        if (seatCount < 1) {
            throw new IllegalArgumentException("seatCount must be at least 1");
        }
        if (!hasAvailableSeats(seatCount)) {
            throw new IllegalStateException("Cannot reserve more seats than available");
        }

        bookedSeats += seatCount;
    }

    void releaseSeats(int seatCount) {
        if (seatCount < 1) {
            throw new IllegalArgumentException("seatCount must be at least 1");
        }
        if (seatCount > bookedSeats) {
            throw new IllegalStateException("Cannot release more seats than booked");
        }

        bookedSeats -= seatCount;
    }
}
