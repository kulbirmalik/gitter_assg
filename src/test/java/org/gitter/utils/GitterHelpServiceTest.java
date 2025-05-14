package org.gitter.utils;

import org.gitter.model.GitterHelperMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GitterHelpServiceTest {

    private GitterHelpService helpService;

    @BeforeEach
    void setUp() {
        helpService = new GitterHelpService();
        helpService.initHelpDocs();
    }

    @Test
    void testInitHelpDocsPopulatesHelpMap() {
        assertFalse(helpService.getAllCommandHelps().isEmpty(), "Help map should not be empty after initialization");
        assertTrue(helpService.getAllCommandHelps().containsKey("init"), "Help map should contain 'init' command");
        assertTrue(helpService.getAllCommandHelps().containsKey("commit"), "Help map should contain 'commit' command");
    }

    @Test
    void testGetHelpForCommandReturnsCorrectMessage() {
        GitterHelperMessage message = helpService.getHelpForCommand("commit");

        assertNotNull(message, "Help message for 'commit' should not be null");
        assertEquals("commit - Record changes to the repository", message.getTitle());
        assertEquals("gitter commit -m [-a] <msg>", message.getUsage());
        assertTrue(message.getDescription().contains("Create a new commit"), "Description should explain commit behavior");
        assertNotNull(message.getOptions());
        assertTrue(message.getOptions().contains("-a: Automatically stage modified and deleted files."));
    }

    @Test
    void testGetHelpForInvalidCommandReturnsNull() {
        GitterHelperMessage message = helpService.getHelpForCommand("unknown");
        assertNull(message, "Help message for unknown command should be null");
    }

    @Test
    void testGetAllCommandHelpsReturnsAllExpectedCommands() {
        Map<String, GitterHelperMessage> allHelps = helpService.getAllCommandHelps();
        assertEquals(6, allHelps.size(), "Should contain help for 6 commands");
    }
}
