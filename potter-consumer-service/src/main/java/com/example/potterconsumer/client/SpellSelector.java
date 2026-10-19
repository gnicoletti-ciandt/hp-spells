package com.example.potterconsumer.client;

import java.util.List;
import java.util.Optional;

/**
 * Pure selection logic: potterapi's `search` param returns a list of "best
 * matches", but we want a single spell. Prefer an exact (case-insensitive)
 * name match, otherwise fall back to the first result.
 */
public final class SpellSelector {

    private SpellSelector() {
    }

    public static Optional<SpellResponse> pick(List<SpellResponse> candidates, String requestedName) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        String wanted = requestedName == null ? "" : requestedName.trim();
        return candidates.stream()
                .filter(c -> c.spell() != null && c.spell().trim().equalsIgnoreCase(wanted))
                .findFirst()
                .or(() -> Optional.of(candidates.get(0)));
    }
}
