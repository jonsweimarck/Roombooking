package com.example.roombooking.application;

import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookingService")
class BookingServiceTest {

    /** Onsdag 12:00 - ett fast "nu" som ligger mellan de tider testerna bokar. */
    private static final Clock ONSDAG_KLOCKAN_TOLV =
            Clock.fixed(Instant.parse("2024-01-03T12:00:00Z"), ZoneOffset.UTC);

    private BookingService bookingService;
    private InMemoryBookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository = new InMemoryBookingRepository();
        bookingService = new BookingService(new InMemoryRoomRepository(), bookingRepository, ONSDAG_KLOCKAN_TOLV);
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

    @Nested
    @DisplayName("när tiden redan har passerat idag")
    class NarTidenRedanHarPasseratIdag {

        @Test
        @DisplayName("ska bokningen avslås med anledningen \"Kan inte boka bakåt i tiden\"")
        void skaBokningenAvslasMedAnledningenKanInteBokaBakatITiden() {
            var tidigareIdag = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));

            var result = bookingService.book(new RoomId("R204"), tidigareIdag, "Alva");

            assertThat(result).isInstanceOf(BookingResult.Rejected.class);
            assertThat(((BookingResult.Rejected) result).reason()).isEqualTo("Kan inte boka bakåt i tiden");
        }

        @Test
        @DisplayName("ska en tidigare veckodag ändå bekräftas - avser nästa vecka")
        void skaEnTidigareVeckodagAndaBekraftas() {
            var mandag = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));

            var result = bookingService.book(new RoomId("R204"), mandag, "Alva");

            assertThat(result).isInstanceOf(BookingResult.Confirmed.class);
        }
    }
}
