package com.example.potterconsumer.client;

/** Thrown when potterapi responds with something we don't know how to handle. */
public class PotterApiException extends RuntimeException {
    public PotterApiException(String message) {
        super(message);
    }

    public PotterApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
