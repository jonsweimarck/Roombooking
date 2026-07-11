package com.example.roombooking.acceptance;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import io.cucumber.java.Before;
import io.cucumber.java.sv.Givet;
import io.cucumber.java.sv.Och;
import io.cucumber.java.sv.När;
import io.cucumber.java.sv.Så;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steg-definitionerna hålls medvetet tunna: de översätter Gherkin till anrop
 * mot applikationslagret (BookingService) - samma lager som webbkontrollern
 * använder. Det säkerställer att acceptanstesterna verkligen testar
 * användarens väg genom systemet, inte bara isolerad domänlogik.
 */
public class BookingSteps {

    private static final Map<String, DayOfWeek> VECKODAGAR = Map.of(
            "måndag", DayOfWeek.MONDAY,
            "tisdag", DayOfWeek.TUESDAY,
            "onsdag", DayOfWeek.WEDNESDAY,
            "torsdag", DayOfWeek.THURSDAY,
            "fredag", DayOfWeek.FRIDAY,
            "lördag", DayOfWeek.SATURDAY,
            "söndag", DayOfWeek.SUNDAY
    );

    /** En godtycklig måndag, bara använd som referenspunkt för att bygga en fast klocka. */
    private static final LocalDate REFERENSMÅNDAG = LocalDate.of(2024, 1, 1);

    private InMemoryRoomRepository roomRepository;
    private InMemoryBookingRepository bookingRepository;
    private BookingService bookingService;
    private BookingResult senasteResultat;

    @Before
    public void setUp() {
        roomRepository = new InMemoryRoomRepository();
        bookingRepository = new InMemoryBookingRepository();
        // Måndag 00:00 som standard "nu" - ligger före alla scenarier som bokar senare i veckan,
        // så "bokning bakåt i tiden"-kontrollen inte råkar avslå dem.
        bookingService = new BookingService(roomRepository, bookingRepository, klockaFrån(DayOfWeek.MONDAY, LocalTime.MIDNIGHT));
    }

    @Givet("att rum {string} är ledigt")
    public void attRumÄrLedigt(String rumId) {
        // Rummet finns redan i InMemoryRoomRepository och har inga bokningar - inget att göra.
    }

    @Givet("att klockan är {tid} på {word}")
    public void attKlockanÄr(LocalTime tid, String veckodag) {
        bookingService = new BookingService(roomRepository, bookingRepository, klockaFrån(dagFrån(veckodag), tid));
    }

    @Givet("att rum {string} redan är bokat mellan {tid} och {tid} på {word}")
    public void attRumRedanÄrBokat(String rumId, LocalTime start, LocalTime slut, String veckodag) {
        var timeSlot = new TimeSlot(dagFrån(veckodag), start, slut);
        bookingService.book(new RoomId(rumId), timeSlot, "Tidigare bokare");
    }

    @När("{word} bokar {string} mellan {tid} och {tid} på {word}")
    public void bokarRum(String namn, String rumId, LocalTime start, LocalTime slut, String veckodag) {
        försökBoka(namn, rumId, start, slut, veckodag);
    }

    @När("{word} försöker boka {string} mellan {tid} och {tid} på {word}")
    public void försökerBokaRum(String namn, String rumId, LocalTime start, LocalTime slut, String veckodag) {
        försökBoka(namn, rumId, start, slut, veckodag);
    }

    private void försökBoka(String namn, String rumId, LocalTime start, LocalTime slut, String veckodag) {
        var timeSlot = new TimeSlot(dagFrån(veckodag), start, slut);
        senasteResultat = bookingService.book(new RoomId(rumId), timeSlot, namn);
    }

    @Så("ska bokningen bekräftas")
    public void skaBokningenBekräftas() {
        assertThat(senasteResultat).isInstanceOf(BookingResult.Confirmed.class);
    }

    @Så("ska bokningen avslås med anledningen {string}")
    public void skaBokningenAvslåsMedAnledningen(String förväntadAnledning) {
        assertThat(senasteResultat).isInstanceOf(BookingResult.Rejected.class);
        var avslag = (BookingResult.Rejected) senasteResultat;
        assertThat(avslag.reason()).isEqualTo(förväntadAnledning);
    }

    @Och("rummet ska visas som upptaget mellan {tid} och {tid} på {word}")
    public void rummetSkaVisasSomUpptaget(LocalTime start, LocalTime slut, String veckodag) {
        var förväntad = new TimeSlot(dagFrån(veckodag), start, slut);
        var bokningar = bookingRepository.findByRoom(new RoomId(hämtaBokatRum()));
        assertThat(bokningar).anySatisfy(b -> assertThat(b.timeSlot()).isEqualTo(förväntad));
    }

    private String hämtaBokatRum() {
        var bekräftad = (BookingResult.Confirmed) senasteResultat;
        return bekräftad.booking().roomId().value();
    }

    private DayOfWeek dagFrån(String veckodag) {
        DayOfWeek dag = VECKODAGAR.get(veckodag.toLowerCase(Locale.of("sv", "SE")));
        if (dag == null) {
            throw new IllegalArgumentException("Okänd veckodag i specifikationen: " + veckodag);
        }
        return dag;
    }

    private static Clock klockaFrån(DayOfWeek dag, LocalTime tid) {
        LocalDate datum = REFERENSMÅNDAG.plusDays(dag.getValue() - 1L);
        Instant instant = LocalDateTime.of(datum, tid).toInstant(ZoneOffset.UTC);
        return Clock.fixed(instant, ZoneOffset.UTC);
    }
}
