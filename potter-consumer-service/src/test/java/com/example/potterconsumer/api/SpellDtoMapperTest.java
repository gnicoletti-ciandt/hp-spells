package com.example.potterconsumer.api;

import com.example.potterconsumer.client.SpellResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** UNIT test for the pure upstream -> our-own-contract mapping. */
class SpellDtoMapperTest {

    @Test
    void mapsSpellAndUseToNameAndDescription() {
        SpellDto dto = SpellDtoMapper.toDto(new SpellResponse("Expelliarmus", "Disarming Charm", 15));

        assertEquals("Expelliarmus", dto.name());
        assertEquals("Disarming Charm", dto.description());
    }

    @Test
    void trimsWhitespaceFromBothFields() {
        SpellDto dto = SpellDtoMapper.toDto(new SpellResponse("  Lumos\n", "\nMakes wand emit light ", 7));

        assertEquals("Lumos", dto.name());
        assertEquals("Makes wand emit light", dto.description());
    }

    @Test
    void sortedDtosAreSortedAlphabeticallyIgnoringCase() {
        List<SpellDto> dtos = SpellDtoMapper.toSortedDtos(List.of(
                new SpellResponse("lumos", "light", 1),
                new SpellResponse("Accio", "summons", 2),
                new SpellResponse("Expelliarmus", "disarms", 3)));

        assertEquals(List.of("Accio", "Expelliarmus", "lumos"), dtos.stream().map(SpellDto::name).toList());
    }

    @Test
    void sortedDtosDropEntriesWithoutAName() {
        List<SpellDto> dtos = SpellDtoMapper.toSortedDtos(List.of(
                new SpellResponse(null, "x", 1),
                new SpellResponse("  ", "y", 2),
                new SpellResponse("Lumos", "light", 3)));

        assertEquals(1, dtos.size());
        assertEquals("Lumos", dtos.get(0).name());
    }

    @Test
    void sortedDtosRemoveCaseInsensitiveDuplicatesKeepingTheFirst() {
        List<SpellDto> dtos = SpellDtoMapper.toSortedDtos(List.of(
                new SpellResponse("Lumos", "first", 1),
                new SpellResponse("LUMOS", "second", 2)));

        assertEquals(1, dtos.size());
        assertEquals("first", dtos.get(0).description());
    }

    @Test
    void sortedDtosOfNullOrEmptyInputIsEmpty() {
        assertTrue(SpellDtoMapper.toSortedDtos(null).isEmpty());
        assertTrue(SpellDtoMapper.toSortedDtos(List.of()).isEmpty());
    }

    @Test
    void mapsNullFieldsToEmptyStrings() {
        SpellDto dto = SpellDtoMapper.toDto(new SpellResponse(null, null, null));

        assertEquals("", dto.name());
        assertEquals("", dto.description());
    }
}
