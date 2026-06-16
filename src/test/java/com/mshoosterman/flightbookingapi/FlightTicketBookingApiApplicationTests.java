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
                                {"flightNumber":"FL100"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", notNullValue()))
                .andExpect(jsonPath("$.flightNumber").value("FL100"));
    }

    @Test
    void returnsNotFoundForUnknownFlight() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"UNKNOWN"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void preventsOverbooking() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"FL300"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"FL300"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelsBooking() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"FL200"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String bookingId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.bookingId");

        mockMvc.perform(delete("/api/bookings/{bookingId}", bookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsNotFoundWhenCancelingUnknownBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/{bookingId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}
