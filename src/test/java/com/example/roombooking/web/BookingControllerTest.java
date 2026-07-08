package com.example.roombooking.web;

import com.example.roombooking.application.BookingService;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookingController")
class BookingControllerTest {

    private BookingController controller;
    private Model model;

    @BeforeEach
    void setUp() {
        var bookingService = new BookingService(new InMemoryRoomRepository(), new InMemoryBookingRepository());
        controller = new BookingController(bookingService);
        model = new ExtendedModelMap();
    }

    private void postaBokning(String rumId, LocalTime start, LocalTime slut, String namn) {
        controller.createBooking(rumId, DayOfWeek.FRIDAY, start, slut, namn, model);
    }

    @Nested
    @DisplayName("när rummet är ledigt")
    class NarRummetArLedigt {

        @Test
        @DisplayName("ska modellen visa att bokningen lyckades")
        void skaModellenVisaAttBokningenLyckades() {
            postaBokning("R204", LocalTime.of(10, 0), LocalTime.of(11, 0), "Alva");

            assertThat(model.getAttribute("success")).isEqualTo(true);
            assertThat(model.getAttribute("message")).isEqualTo("Bokning bekräftad för rum R204");
        }
    }

    @Nested
    @DisplayName("när rummet redan är bokat under en överlappande tid")
    class NarRummetRedanArBokat {

        @BeforeEach
        void bokaRummetForst() {
            postaBokning("R204", LocalTime.of(10, 0), LocalTime.of(11, 0), "Alva");
        }

        @Test
        @DisplayName("ska modellen visa anledningen till avslaget")
        void skaModellenVisaAnledningenTillAvslaget() {
            postaBokning("R204", LocalTime.of(10, 30), LocalTime.of(11, 30), "Björn");

            assertThat(model.getAttribute("success")).isEqualTo(false);
            assertThat(model.getAttribute("message")).isEqualTo("Bokning avslogs: Överlappande bokning");
        }
    }
}
