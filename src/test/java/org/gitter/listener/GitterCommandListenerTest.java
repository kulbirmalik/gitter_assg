package org.gitter.listener;

import org.gitter.model.enums.CommandName;
import org.gitter.registry.CommandRegistry;
import org.gitter.service.CommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class GitterCommandListenerTest {

    private CommandRegistry commandRegistry;
    private GitterCommandListener listener;

    @BeforeEach
    public void setup() {
        commandRegistry = mock(CommandRegistry.class);
        listener = new GitterCommandListener(commandRegistry);
    }

    @Test
    public void testInvalidCommandPrefix() {
        listener.processInput("invalid init");

        // No interaction with registry for non-'gitter' command
        verifyNoInteractions(commandRegistry);
    }

    @Test
    public void testNoCommandAfterGitter() {
        listener.processInput("gitter");
        // No interaction with registry if no actual command
        verifyNoInteractions(commandRegistry);
    }

    @Test
    public void testUnknownCommand() {
        listener.processInput("gitter unknownCommand");
        // Should not call any command
        verifyNoInteractions(commandRegistry);
    }

    @Test
    public void testValidCommandExecution() throws Exception {
        String[] args = {"gitter", "init"};
        CommandService mockService = mock(CommandService.class);

        when(commandRegistry.getService(CommandName.INIT)).thenReturn(mockService);

        listener.processInput("gitter init");

        verify(mockService).execute(args);
    }

    @Test
    public void testCommandExecutionThrowsException() throws Exception {
        CommandService mockService = mock(CommandService.class);
        when(commandRegistry.getService(CommandName.INIT)).thenReturn(mockService);
        doThrow(new RuntimeException("Command failed")).when(mockService).execute(any());

        listener.processInput("gitter init");

        verify(mockService).execute(new String[]{"gitter", "init"});
    }
}
