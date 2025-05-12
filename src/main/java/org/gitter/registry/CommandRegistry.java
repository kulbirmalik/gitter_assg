package org.gitter.registry;

import lombok.RequiredArgsConstructor;
import org.gitter.model.enums.CommandName;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandRegistry {

    private final Map<CommandName, CommandService> registry = new HashMap<>();

    public CommandRegistry(List<CommandService> services) {
        for (CommandService service : services) {
            registry.put(service.getCommandName(), service);
        }
    }

    public CommandService getService(CommandName commandName) {
        return registry.get(commandName);
    }
}

