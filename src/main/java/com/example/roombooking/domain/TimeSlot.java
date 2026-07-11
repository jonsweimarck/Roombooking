package com.example.roombooking.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Ett värdeobjekt som representerar en avgränsad tidsperiod på en given veckodag.
 * Håller sin egen invariant: start måste ligga före slut.
 */
public record TimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {

    public TimeSlot {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Starttid måste ligga före sluttid: %s - %s".formatted(start, end));
        }
    }

    public boolean overlaps(TimeSlot other) {
        if (this.day != other.day) {
            return false;
        }
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    /**
     * Slotar är återkommande veckovis och har inget kalenderdatum, så
     * "bakåt i tiden" betyder bara: samma veckodag som nu, men starttiden har
     * redan passerat. En tidigare veckodag avser då nästa förekomst, nästa
     * vecka, och räknas inte som passerad.
     */
    public boolean hasPassed(DayOfWeek nu, LocalTime nuTid) {
        return day == nu && start.isBefore(nuTid);
    }
}
