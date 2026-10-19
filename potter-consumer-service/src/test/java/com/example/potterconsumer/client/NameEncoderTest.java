package com.example.potterconsumer.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** UNIT test: pure function, no HTTP, no I/O. */
class NameEncoderTest {

    @Test
    void encodesSpacesAsPercentTwenty() {
        assertEquals("Wingardium%20Leviosa", NameEncoder.encode("Wingardium Leviosa"));
    }

    @Test
    void leavesAlreadySafeCharactersUntouched() {
        assertEquals("Lumos", NameEncoder.encode("Lumos"));
    }

    @Test
    void encodesReservedCharacters() {
        assertEquals("a%26b%3Dc", NameEncoder.encode("a&b=c"));
    }

    @Test
    void encodesNonAsciiCharactersAsUtf8() {
        assertEquals("Accio%20%C3%A9", NameEncoder.encode("Accio é"));
    }
}
