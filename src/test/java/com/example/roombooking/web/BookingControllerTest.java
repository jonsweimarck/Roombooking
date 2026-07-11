package com.example.roombooking.web;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testar bara webblagret: BookingService är stubbad, så det som verifieras
 * är den faktiskt renderade HTML:en (formulärfält, htmx-attribut,
 * resultatfragmentet) - inte affärslogiken, som redan täcks av
 * BookingServiceTest och bokning.feature.
 */
@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Nested
    @DisplayName("bokningsformuläret")
    class Bokningsformuläret {

        @Test
        @DisplayName("ska visa fält för rum, veckodag, tid och namn samt posta via htmx")
        void skaVisaFältOchPostaViaHtmx() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(allOf(
                            containsString("hx-post=\"/bookings\""),
                            containsString("name=\"roomId\""),
                            containsString("name=\"day\""),
                            containsString("name=\"start\""),
                            containsString("name=\"end\""),
                            containsString("name=\"bookedBy\"")
                    )));
        }
    }

    @Nested
    @DisplayName("när bokningen bekräftas")
    class NärBokningenBekräftas {

        @Test
        @DisplayName("ska resultatfragmentet visa bekräftelsen")
        void skaResultatfragmentetVisaBekräftelsen() throws Exception {
            var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
            var booking = Booking.create(new RoomId("R204"), timeSlot, "Alva");
            when(bookingService.book(eq(new RoomId("R204")), any(), eq("Alva")))
                    .thenReturn(new BookingResult.Confirmed(booking));

            mockMvc.perform(post("/bookings")
                            .param("roomId", "R204")
                            .param("day", "FRIDAY")
                            .param("start", "10:00")
                            .param("end", "11:00")
                            .param("bookedBy", "Alva"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(allOf(
                            containsString("Bokning bekräftad för rum R204"),
                            containsString("#e6f4ea")
                    )));
        }
    }

    @Nested
    @DisplayName("när bokningen avslås")
    class NärBokningenAvslås {

        @Test
        @DisplayName("ska resultatfragmentet visa anledningen")
        void skaResultatfragmentetVisaAnledningen() throws Exception {
            when(bookingService.book(eq(new RoomId("R204")), any(), eq("Björn")))
                    .thenReturn(new BookingResult.Rejected("Överlappande bokning"));

            mockMvc.perform(post("/bookings")
                            .param("roomId", "R204")
                            .param("day", "FRIDAY")
                            .param("start", "10:30")
                            .param("end", "11:30")
                            .param("bookedBy", "Björn"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(allOf(
                            containsString("Bokning avslogs: Överlappande bokning"),
                            containsString("#fce8e6")
                    )));

            verify(bookingService).book(eq(new RoomId("R204")), any(), eq("Björn"));
        }
    }
}
