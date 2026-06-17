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
        bookedSeats += seatCount;
    }

    void releaseSeats(int seatCount) {
        bookedSeats -= seatCount;
    }
}
