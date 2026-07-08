package com.example.roombooking.domain;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeSlotTest {

    @Test
    void avvisarSlutSomInteLiggerEfterStart() {
        assertThatThrownBy(() ->
                new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(10, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void overlapparNarTidsintervallenKorsarVarandra() {
        var forsta = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        var andra = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));

        assertThat(forsta.overlaps(andra)).isTrue();
        assertThat(andra.overlaps(forsta)).isTrue();
    }

    @Test
    void overlapparInteNarTidsintervallenLiggerEfterVarandra() {
        var forsta = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        var andra = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(12, 0));

        assertThat(forsta.overlaps(andra)).isFalse();
    }

    @Test
    void overlapparInteOmDeLiggerPaOlikaVeckodagar() {
        var fredag = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        var mandag = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertThat(fredag.overlaps(mandag)).isFalse();
    }
}
