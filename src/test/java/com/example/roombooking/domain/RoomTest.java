package com.example.roombooking.domain;

import com.example.roombooking.domain.Room.RoomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Ett rum-id")
class RoomTest {

    @Nested
    @DisplayName("när värdet saknar innehåll")
    class NarVardetSaknarInnehall {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("ska avvisas som ogiltigt")
        void skaAvvisasSomOgiltigt(String vardeUtanInnehall) {
            assertThatThrownBy(() -> new RoomId(vardeUtanInnehall))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("ska accepteras när det har ett värde")
    void skaAccepterasNarDetHarEttVarde() {
        assertThatCode(() -> new RoomId("R204")).doesNotThrowAnyException();
    }
}
