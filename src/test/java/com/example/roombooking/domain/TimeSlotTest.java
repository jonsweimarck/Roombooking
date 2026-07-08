package com.example.roombooking.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Ett tidsintervall")
class TimeSlotTest {

    @Test
    @DisplayName("ska avvisas om sluttiden inte ligger efter starttiden")
    void skaAvvisasOmSluttidenInteLiggerEfterStarttiden() {
        assertThatThrownBy(() ->
                new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(10, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    @DisplayName("när det jämförs med ett annat tidsintervall")
    class NarDetJamforsMedEttAnnatTidsintervall {

        @Test
        @DisplayName("ska de överlappa om de korsar varandra samma dag")
        void skaDeOverlappaOmDeKorsarVarandraSammaDag() {
            var forsta = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
            var andra = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));

            assertThat(forsta.overlaps(andra)).isTrue();
            assertThat(andra.overlaps(forsta)).isTrue();
        }

        @Test
        @DisplayName("ska de inte överlappa om de bara ligger efter varandra")
        void skaDeInteOverlappaOmDeBaraLiggerEfterVarandra() {
            var forsta = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
            var andra = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 0));

            assertThat(forsta.overlaps(andra)).isFalse();
        }

        @Test
        @DisplayName("ska de inte överlappa om de ligger på olika veckodagar")
        void skaDeInteOverlappaOmDeLiggerPaOlikaVeckodagar() {
            var fredag = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
            var mandag = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

            assertThat(fredag.overlaps(mandag)).isFalse();
        }
    }
}
