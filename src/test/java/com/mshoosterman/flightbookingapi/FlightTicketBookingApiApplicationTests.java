package com.mshoosterman.flightbookingapi;

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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class FlightTicketBookingApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsBooking() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "flightNumber":"FL100",
                                  "passengerName":"Alex Morgan",
                                  "passengerEmail":"alex@example.com",
                                  "seatCount":1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", notNullValue()))
                .andExpect(jsonPath("$.flightNumber").value("FL100"))
                .andExpect(jsonPath("$.passengerName").value("Alex Morgan"))
                .andExpect(jsonPath("$.passengerEmail").value("alex@example.com"))
                .andExpect(jsonPath("$.seatCount").value(1));
    }

    @Test
    void returnsBadRequestForInvalidRequestBody() throws Exception {
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
    void returnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"FL100"
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request body"));
    }

    @Test
    void returnsNotFoundForUnknownFlight() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "flightNumber":"UNKNOWN",
                                  "passengerName":"Alex Morgan",
                                  "passengerEmail":"alex@example.com",
                                  "seatCount":1
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Flight not found: UNKNOWN"));
    }

    @Test
    void returnsConflictWhenBookingWouldExceedAvailableSeats() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "flightNumber":"FL300",
                                  "passengerName":"Alex Morgan",
                                  "passengerEmail":"alex@example.com",
                                  "seatCount":2
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Not enough seats available"));
    }

    @Test
    void cancelsBooking() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "flightNumber":"FL200",
                                  "passengerName":"Alex Morgan",
                                  "passengerEmail":"alex@example.com",
                                  "seatCount":2
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String bookingId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.bookingId");

        mockMvc.perform(delete("/api/bookings/{bookingId}", bookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsNotFoundWhenCancelingUnknownBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/bookings/{bookingId}", bookingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found: " + bookingId));
    }

}
