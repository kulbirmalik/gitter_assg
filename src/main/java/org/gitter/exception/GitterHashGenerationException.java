package org.gitter.exception;

public class GitterHashGenerationException extends RuntimeException {
    public GitterHashGenerationException(String message) {
        super(message);
    }

    public GitterHashGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}