package com.mshoosterman.flightbookingapi.booking;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
class BookingController {

    private final InMemoryBookingService bookingService;

    BookingController(InMemoryBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request.flightNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BookingResponse(booking.id(), booking.flightNumber()));
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    @ExceptionHandler({
            InMemoryBookingService.FlightNotFoundException.class,
            InMemoryBookingService.BookingNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void handleNotFound() {
    }

    @ExceptionHandler(InMemoryBookingService.FlightFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    void handleConflict() {
    }
}
