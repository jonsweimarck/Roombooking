package com.example.roombooking.web;

import com.example.roombooking.application.RoomAdminService;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AdminController")
class AdminControllerTest {

    private AdminController controller;
    private InMemoryRoomRepository roomRepository;
    private Model model;

    @BeforeEach
    void setUp() {
        roomRepository = new InMemoryRoomRepository();
        controller = new AdminController(new RoomAdminService(roomRepository));
        model = new ExtendedModelMap();
    }

    @Test
    @DisplayName("ska lägga till rummet i repositoryt och visa en bekräftelse")
    void skaLäggaTillRummetIRepositorytOchVisaEnBekräftelse() {
        controller.läggTillRum("R205", model);

        assertThat(roomRepository.findById(new RoomId("R205"))).isPresent();
        assertThat(model.getAttribute("message")).isEqualTo("Rummet R205 har lagts till");
    }
}
