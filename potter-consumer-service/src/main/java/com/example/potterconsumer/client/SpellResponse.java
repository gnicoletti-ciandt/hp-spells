package com.example.potterconsumer.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One item of the array returned by GET /en/spells on potterapi. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpellResponse(String spell, String use, Integer index) {
}
