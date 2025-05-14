package org.gitter.registry;

import org.gitter.model.enums.CommandName;
import org.gitter.service.CommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommandRegistryTest {

    private CommandRegistry commandRegistry;
    private CommandService mockService1;
    private CommandService mockService2;

    @BeforeEach
    public void setup() {
        mockService1 = mock(CommandService.class);
        mockService2 = mock(CommandService.class);

        when(mockService1.getCommandName()).thenReturn(CommandName.INIT);
        when(mockService2.getCommandName()).thenReturn(CommandName.ADD);

        commandRegistry = new CommandRegistry(Arrays.asList(mockService1, mockService2));
    }

    @Test
    public void testRegistryInitialization() {
        assertNotNull(commandRegistry.getService(CommandName.INIT), "INIT service should be registered.");
        assertNotNull(commandRegistry.getService(CommandName.ADD), "ADD service should be registered.");
    }

    @Test
    public void testGetServiceReturnsCorrectService() {
        CommandService service = commandRegistry.getService(CommandName.INIT);
        assertEquals(mockService1, service, "The returned service should match the INIT service.");

        service = commandRegistry.getService(CommandName.ADD);
        assertEquals(mockService2, service, "The returned service should match the ADD service.");
    }

    @Test
    public void testGetServiceReturnsNullForUnknownCommand() {
        CommandService service = commandRegistry.getService(CommandName.UNKNOWN);
        assertNull(service, "The service for an unknown command should be null.");
    }

    @Test
    public void testEmptyRegistry() {
        CommandRegistry emptyRegistry = new CommandRegistry(Arrays.asList());
        CommandService service = emptyRegistry.getService(CommandName.INIT);
        assertNull(service, "An empty registry should return null for any command.");
    }
}
