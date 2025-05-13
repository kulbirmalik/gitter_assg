package org.gitter.exception;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String command, String message) {
        super("Error executing command '" + command + "': " + message);
    }
}
