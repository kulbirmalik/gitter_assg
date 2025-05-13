package org.gitter.service.impl;
import org.gitter.model.GitterHelperMessage;
import org.gitter.utils.GitterHelpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.mockito.Mockito.*;

class CommandHelperServiceTest {

    @Mock
    private GitterHelpService gitterHelpService;

    private CommandHelperService commandHelperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandHelperService = new CommandHelperService(gitterHelpService);
    }

    @Test
    void testExecuteWithAllCommandHelp() {
        String[] args = {"gitter", "help"};
        Map<String, GitterHelperMessage> mockHelpMap = new HashMap<>();
        mockHelpMap.put("init", new GitterHelperMessage("init - Initializes repo", "", "", null));
        mockHelpMap.put("add", new GitterHelperMessage("add - Stages files", "", "", null));
        when(gitterHelpService.getAllCommandHelps()).thenReturn(mockHelpMap);
        commandHelperService.execute(args);
        verify(gitterHelpService, times(1)).getAllCommandHelps();
    }

    @Test
    void testExecuteWithValidSubcommandHelp() {
        String[] args = {"gitter", "help", "commit"};
        GitterHelperMessage helpMessage = new GitterHelperMessage(
                "commit - Record changes",
                "gitter commit -m \"message\"",
                "Stores staged changes with a message",
                Arrays.asList("-m <message>: message for commit")
        );
        when(gitterHelpService.getHelpForCommand("commit")).thenReturn(helpMessage);
        commandHelperService.execute(args);
        verify(gitterHelpService).getHelpForCommand("commit");
    }

    @Test
    void testExecuteWithInvalidSubcommand() {
        String[] args = {"gitter", "help", "nonexistent"};
        when(gitterHelpService.getHelpForCommand("nonexistent")).thenReturn(null);
        commandHelperService.execute(args);
        verify(gitterHelpService).getHelpForCommand("nonexistent");
    }
}
