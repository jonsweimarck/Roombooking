package com.example.roombooking.web;

import com.example.roombooking.application.RoomAdminService;
import com.example.roombooking.domain.Room.RoomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testar bara webblagret: RoomAdminService är stubbad, så det som verifieras
 * är den faktiskt renderade HTML:en (formulärfält, htmx-attribut,
 * resultatfragmentet) - inte affärslogiken, som redan täcks av
 * RoomAdminServiceTest och administrera-rum.feature.
 */
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomAdminService roomAdminService;

    @Nested
    @DisplayName("admin-formuläret")
    class AdminFormuläret {

        @Test
        @DisplayName("ska visa fält för rum-id och posta via htmx")
        void skaVisaFältOchPostaViaHtmx() throws Exception {
            mockMvc.perform(get("/admin/rum"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(allOf(
                            containsString("hx-post=\"/admin/rum\""),
                            containsString("name=\"roomId\"")
                    )));
        }
    }

    @Nested
    @DisplayName("när ett rum läggs till")
    class NärEttRumLäggsTill {

        @Test
        @DisplayName("ska rummet skickas till RoomAdminService och resultatfragmentet visa bekräftelsen")
        void skaSkickasTillServiceOchVisaBekräftelsen() throws Exception {
            mockMvc.perform(post("/admin/rum").param("roomId", "R205"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Rummet R205 har lagts till")));

            verify(roomAdminService).addRoom(new RoomId("R205"));
        }
    }
}
