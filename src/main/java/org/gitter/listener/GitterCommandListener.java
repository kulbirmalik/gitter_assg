package org.gitter.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.enums.CommandName;
import org.gitter.registry.CommandRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@Slf4j
@RequiredArgsConstructor
public class GitterCommandListener implements CommandLineRunner {

    private final CommandRegistry commandRegistry;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        log.info("Gitter service started. Type commands like `gitter help`, `gitter init`, `gitter add .`");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            if (!tokens[0].equals("gitter")) {
                log.warn("Invalid command. Commands must start with 'gitter'.");
                continue;
            }
            handleCommand(tokens);
        }
    }

    // Add a new method for processing a single line (makes testing easier)
    public void processInput(String input) {
        if (input == null || input.trim().isEmpty()) return;

        String[] tokens = input.trim().split("\\s+");
        if (!tokens[0].equals("gitter")) {
            log.warn("Invalid command. Commands must start with 'gitter'.");
            return;
        }

        handleCommand(tokens);
    }

    private void handleCommand(String[] args) {
        if (args.length < 2) {
            log.warn("No command provided after 'gitter'.");
            return;
        }

        try {
            CommandName commandName = CommandName.valueOf(args[1].toUpperCase());
            commandRegistry.getService(commandName).execute(args);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown command: {}", args[1]);
        } catch (Exception e) {
            log.error("Error executing command '{}': {}", args[1], e.getMessage(), e);
        }
    }
}
