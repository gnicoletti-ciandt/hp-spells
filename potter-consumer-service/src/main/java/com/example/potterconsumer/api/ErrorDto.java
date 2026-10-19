package com.example.potterconsumer.api;

/** Returned as the JSON body whenever a request cannot be fulfilled. */
public record ErrorDto(String error) {
}
