package org.gitter.service.impl;

import org.gitter.model.GitterCommitEntry;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CommandLogServiceTest {

    private GitterRepository gitterRepository;
    private CommandLogService commandLogService;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        gitterRepository = mock(GitterRepository.class);
        commandLogService = new CommandLogService(gitterRepository);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testGetCommandName() {
        assertTrue(commandLogService.getCommandName() == CommandName.LOG);
    }

    @Test
    void testExecutePrintsCommitLogs() {
        GitterCommitEntry entry1 = new GitterCommitEntry("abc123", new Date(1715600000000L), "Initial commit");
        GitterCommitEntry entry2 = new GitterCommitEntry("def456", new Date(1715700000000L), "Added feature X");
        List<GitterCommitEntry> commits = Arrays.asList(entry1, entry2);
        when(gitterRepository.getCommitHistory()).thenReturn(commits);
        commandLogService.execute(new String[]{"gitter", "log"});
        String output = outContent.toString();
        assertTrue(output.contains("commit abc123"));
        assertTrue(output.contains("Initial commit"));
        assertTrue(output.contains("commit def456"));
        assertTrue(output.contains("Added feature X"));
    }

    @Test
    void testExecuteWithNoCommits() {
        when(gitterRepository.getCommitHistory()).thenReturn(List.of());
        commandLogService.execute(new String[]{"gitter", "log"});
        String output = outContent.toString();
        assertTrue(output.isEmpty());
    }
}
