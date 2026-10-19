package com.example.potterconsumer.client;

/** Thrown when potterapi has no spell matching the requested name. */
public class SpellNotFoundException extends RuntimeException {
    public SpellNotFoundException(String name) {
        super("No spell found for name '" + name + "'");
    }
}
