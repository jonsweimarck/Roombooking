package com.example.roombooking.web;

import com.example.roombooking.application.RoomAdminService;
import com.example.roombooking.domain.Room.RoomId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final RoomAdminService roomAdminService;

    public AdminController(RoomAdminService roomAdminService) {
        this.roomAdminService = roomAdminService;
    }

    @GetMapping("/admin/rum")
    public String rumForm() {
        return "admin-rum";
    }

    @PostMapping("/admin/rum")
    public String läggTillRum(@RequestParam String roomId, Model model) {
        roomAdminService.addRoom(new RoomId(roomId));
        model.addAttribute("message", "Rummet " + roomId + " har lagts till");
        // Returnerar bara fragmentet - htmx byter ut resultat-diven, ingen sidladdning.
        return "admin-rum :: result";
    }
}
