package com.example.roombooking.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class BookingEntity {

    @Id
    private UUID id;

    private String roomId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private LocalTime startTime;

    private LocalTime endTime;

    private String bookedBy;

    protected BookingEntity() {
    }

    BookingEntity(UUID id, String roomId, DayOfWeek day, LocalTime startTime, LocalTime endTime, String bookedBy) {
        this.id = id;
        this.roomId = roomId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookedBy = bookedBy;
    }

    UUID getId() {
        return id;
    }

    String getRoomId() {
        return roomId;
    }

    DayOfWeek getDay() {
        return day;
    }

    LocalTime getStartTime() {
        return startTime;
    }

    LocalTime getEndTime() {
        return endTime;
    }

    String getBookedBy() {
        return bookedBy;
    }
}