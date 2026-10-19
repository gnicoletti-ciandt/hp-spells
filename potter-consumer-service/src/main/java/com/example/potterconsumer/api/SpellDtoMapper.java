package com.example.potterconsumer.api;

import com.example.potterconsumer.client.SpellResponse;

import java.util.Comparator;
import java.util.List;

/** Pure mapping from the raw potterapi shape to our own public contract. */
public final class SpellDtoMapper {

    private SpellDtoMapper() {
    }

    public static SpellDto toDto(SpellResponse upstream) {
        String name = upstream.spell() == null ? "" : upstream.spell().trim();
        String description = upstream.use() == null ? "" : upstream.use().trim();
        return new SpellDto(name, description);
    }

    /** Maps, drops entries without a name, removes duplicates (case-insensitive) and sorts A-Z. */
    public static List<SpellDto> toSortedDtos(List<SpellResponse> upstream) {
        if (upstream == null) {
            return List.of();
        }
        return upstream.stream()
                .map(SpellDtoMapper::toDto)
                .filter(dto -> !dto.name().isEmpty())
                .collect(java.util.stream.Collectors.toMap(
                        dto -> dto.name().toLowerCase(), dto -> dto, (first, second) -> first,
                        java.util.LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(dto -> dto.name().toLowerCase()))
                .toList();
    }
}
