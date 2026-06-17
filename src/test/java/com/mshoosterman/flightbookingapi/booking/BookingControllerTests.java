package com.mshoosterman.flightbookingapi.booking;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postBookingsCreatesBooking() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson("FL100", 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", notNullValue()))
                .andExpect(jsonPath("$.flightNumber").value("FL100"))
                .andExpect(jsonPath("$.passengerName").value("Alex Morgan"))
                .andExpect(jsonPath("$.passengerEmail").value("alex@example.com"))
                .andExpect(jsonPath("$.seatCount").value(1));
    }

    @Test
    void postBookingsReturnsBadRequestForInvalidRequestData() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "flightNumber":"",
                                  "passengerName":"",
                                  "passengerEmail":"not-an-email",
                                  "seatCount":0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request body"));
    }

    @Test
    void postBookingsReturnsNotFoundForUnknownFlightNumber() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson("UNKNOWN", 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Flight not found: UNKNOWN"));
    }

    @Test
    void postBookingsReturnsConflictWhenBookingWouldOverbookFlight() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson("FL300", 2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Not enough seats available"));
    }

    @Test
    void deleteBookingsCancelsBooking() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingJson("FL200", 2)))
                .andExpect(status().isCreated())
                .andReturn();

        String bookingId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.bookingId");

        mockMvc.perform(delete("/api/bookings/{bookingId}", bookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBookingsReturnsNotFoundForUnknownBookingId() throws Exception {
        UUID bookingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/bookings/{bookingId}", bookingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found: " + bookingId));
    }

    private String validBookingJson(String flightNumber, int seatCount) {
        return """
                {
                  "flightNumber":"%s",
                  "passengerName":"Alex Morgan",
                  "passengerEmail":"alex@example.com",
                  "seatCount":%d
                }
                """.formatted(flightNumber, seatCount);
    }
}
