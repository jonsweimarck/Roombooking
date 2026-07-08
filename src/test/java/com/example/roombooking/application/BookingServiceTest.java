package com.example.roombooking.application;

import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookingService")
class BookingServiceTest {

    private BookingService bookingService;
    private InMemoryBookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository = new InMemoryBookingRepository();
        bookingService = new BookingService(new InMemoryRoomRepository(), bookingRepository);
    }

    @Nested
    @DisplayName("när rummet är ledigt")
    class NarRummetArLedigt {

        @Test
        @DisplayName("ska bokningen bekräftas")
        void skaBokningenBekraftas() {
            var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

            var result = bookingService.book(new RoomId("R204"), timeSlot, "Alva");

            assertThat(result).isInstanceOf(BookingResult.Confirmed.class);
        }

        @Test
        @DisplayName("ska bokningen sparas i repositoryt")
        void skaBokningenSparasIRepositoryt() {
            var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

            bookingService.book(new RoomId("R204"), timeSlot, "Alva");

            assertThat(bookingRepository.findByRoom(new RoomId("R204")))
                    .anySatisfy(booking -> assertThat(booking.timeSlot()).isEqualTo(timeSlot));
        }
    }

    @Nested
    @DisplayName("när rummet redan är bokat under en överlappande tid")
    class NarRummetRedanArBokat {

        private final TimeSlot befintligBokning = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

        @BeforeEach
        void bokaRummetForst() {
            bookingService.book(new RoomId("R204"), befintligBokning, "Alva");
        }

        @Test
        @DisplayName("ska bokningen avslås med anledningen \"Överlappande bokning\"")
        void skaBokningenAvslasMedAnledningenOverlappandeBokning() {
            var overlappande = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));

            var result = bookingService.book(new RoomId("R204"), overlappande, "Björn");

            assertThat(result).isInstanceOf(BookingResult.Rejected.class);
            assertThat(((BookingResult.Rejected) result).reason()).isEqualTo("Överlappande bokning");
        }

        @Test
        @DisplayName("ska en icke-överlappande tid ändå bekräftas")
        void skaEnIckeOverlappandeTidAndaBekraftas() {
            var senareSammaDag = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 0));

            var result = bookingService.book(new RoomId("R204"), senareSammaDag, "Björn");

            assertThat(result).isInstanceOf(BookingResult.Confirmed.class);
        }
    }

    @Nested
    @DisplayName("när rummet inte finns")
    class NarRummetInteFinns {

        @Test
        @DisplayName("ska bokningen avslås")
        void skaBokningenAvslas() {
            var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

            var result = bookingService.book(new RoomId("OKÄNT"), timeSlot, "Alva");

            assertThat(result).isInstanceOf(BookingResult.Rejected.class);
        }
    }
}
