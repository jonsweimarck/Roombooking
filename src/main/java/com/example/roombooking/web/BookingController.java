package com.example.roombooking.web;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Controller
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String bookingForm() {
        return "booking";
    }

    @PostMapping("/bookings")
    public String createBooking(
            @RequestParam String roomId,
            @RequestParam DayOfWeek day,
            @RequestParam LocalTime start,
            @RequestParam LocalTime end,
            @RequestParam String bookedBy,
            Model model
    ) {
        TimeSlot timeSlot = new TimeSlot(day, start, end);
        BookingResult result = bookingService.book(new RoomId(roomId), timeSlot, bookedBy);

        switch (result) {
            case BookingResult.Confirmed confirmed -> {
                model.addAttribute("message", "Bokning bekräftad för rum " + roomId);
                model.addAttribute("success", true);
            }
            case BookingResult.Rejected rejected -> {
                model.addAttribute("message", "Bokning avslogs: " + rejected.reason());
                model.addAttribute("success", false);
            }
        }
        // Returnerar bara fragmentet - htmx byter ut resultat-diven, ingen sidladdning.
        return "booking :: result";
    }
}
