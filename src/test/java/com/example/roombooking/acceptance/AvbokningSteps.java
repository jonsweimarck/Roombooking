package com.example.roombooking.acceptance;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.domain.Booking.BookingId;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import io.cucumber.java.Before;
import io.cucumber.java.sv.Givet;
import io.cucumber.java.sv.När;
import io.cucumber.java.sv.Så;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Följer samma mönster som BookingSteps: stegen kopplar Gherkin direkt mot
 * applikationslagret (BookingService), inte mot webblagret.
 */
public class AvbokningSteps {

    private static final Map<String, DayOfWeek> VECKODAGAR = Map.of(
            "måndag", DayOfWeek.MONDAY,
            "tisdag", DayOfWeek.TUESDAY,
            "onsdag", DayOfWeek.WEDNESDAY,
            "torsdag", DayOfWeek.THURSDAY,
            "fredag", DayOfWeek.FRIDAY,
            "lördag", DayOfWeek.SATURDAY,
            "söndag", DayOfWeek.SUNDAY
    );

    private InMemoryRoomRepository roomRepository;
    private InMemoryBookingRepository bookingRepository;
    private BookingService bookingService;
    private RoomId senasteRum;
    private BookingId senasteBokningId;

    @Before
    public void setUp() {
        roomRepository = new InMemoryRoomRepository();
        bookingRepository = new InMemoryBookingRepository();
        // Måndag 00:00 som fast "nu" - ligger före fredagsbokningen nedan, så
        // "bokning bakåt i tiden"-kontrollen i BookingService inte stör detta test.
        var nu = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        bookingService = new BookingService(roomRepository, bookingRepository, nu);
    }

    @Givet("att {word} har bokat {string} mellan {tid} och {tid} på {word}")
    public void attHarBokat(String namn, String rumId, LocalTime start, LocalTime slut, String veckodag) {
        var timeSlot = new TimeSlot(dagFrån(veckodag), start, slut);
        var resultat = bookingService.book(new RoomId(rumId), timeSlot, namn);

        var bekräftad = (BookingResult.Confirmed) resultat;
        senasteRum = new RoomId(rumId);
        senasteBokningId = bekräftad.booking().id();
    }

    @När("{word} avbokar bokningen")
    public void avbokarBokningen(String namn) {
        bookingService.cancel(senasteBokningId);
    }

    @Så("ska rummet visas som ledigt mellan {tid} och {tid} på {word}")
    public void skaRummetVisasSomLedigt(LocalTime start, LocalTime slut, String veckodag) {
        var avbokad = new TimeSlot(dagFrån(veckodag), start, slut);
        var bokningar = bookingRepository.findByRoom(senasteRum);
        assertThat(bokningar).noneSatisfy(b -> assertThat(b.timeSlot()).isEqualTo(avbokad));
    }

    private DayOfWeek dagFrån(String veckodag) {
        DayOfWeek dag = VECKODAGAR.get(veckodag.toLowerCase(Locale.of("sv", "SE")));
        if (dag == null) {
            throw new IllegalArgumentException("Okänd veckodag i specifikationen: " + veckodag);
        }
        return dag;
    }
}
