package org.gitter.exception;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(String command) {
        super("Unknown command: " + command);
    }
}
