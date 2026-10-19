package com.example.potterconsumer.api;

/** The shape potter-consumer-service exposes to its own consumers (e.g. potter-frontend). */
public record SpellDto(String name, String description) {
}
