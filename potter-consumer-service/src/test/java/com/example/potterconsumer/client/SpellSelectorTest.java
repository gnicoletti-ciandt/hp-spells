package com.example.potterconsumer.client;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UNIT test for the pure "pick one spell out of the search results" logic. */
class SpellSelectorTest {

    private static final SpellResponse LUMOS = new SpellResponse("Lumos", "Makes wand emit light", 7);
    private static final SpellResponse LUMOS_MAXIMA = new SpellResponse("Lumos Maxima", "Bigger light", 8);

    @Test
    void returnsEmptyForAnEmptyList() {
        assertTrue(SpellSelector.pick(List.of(), "Lumos").isEmpty());
    }

    @Test
    void returnsEmptyForANullList() {
        assertTrue(SpellSelector.pick(null, "Lumos").isEmpty());
    }

    @Test
    void prefersAnExactMatchOverAnEarlierPartialMatch() {
        Optional<SpellResponse> picked = SpellSelector.pick(List.of(LUMOS_MAXIMA, LUMOS), "Lumos");

        assertEquals(Optional.of(LUMOS), picked);
    }

    @Test
    void exactMatchIsCaseInsensitiveAndIgnoresSurroundingWhitespace() {
        Optional<SpellResponse> picked = SpellSelector.pick(List.of(LUMOS_MAXIMA, LUMOS), "  lUmOs ");

        assertEquals(Optional.of(LUMOS), picked);
    }

    @Test
    void fallsBackToTheFirstResultWhenThereIsNoExactMatch() {
        Optional<SpellResponse> picked = SpellSelector.pick(List.of(LUMOS_MAXIMA, LUMOS), "Lum");

        assertEquals(Optional.of(LUMOS_MAXIMA), picked);
    }

    @Test
    void toleratesCandidatesWithANullSpellName() {
        SpellResponse broken = new SpellResponse(null, "?", 1);

        assertEquals(Optional.of(LUMOS), SpellSelector.pick(List.of(broken, LUMOS), "Lumos"));
    }
}
