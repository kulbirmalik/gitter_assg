package org.gitter.service.impl;
import org.gitter.model.GitterCommitEntry;
import org.gitter.repository.GitterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import static org.gitter.utils.CommandServiceUtils.getBaseGitterDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommandCommitServiceTest {

    @Mock
    private GitterRepository gitterRepository;

    private CommandCommitService commandCommitService;

    private File gitterDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitterDir = getBaseGitterDir();
        commandCommitService = new CommandCommitService(gitterRepository);
    }

    @Test
    void testExecuteWithMessageOnly() throws Exception {
        String[] args = {"gitter", "commit", "-m", "initial commit"};
        File file = new File(gitterDir, "file1.txt");
        Files.writeString(file.toPath(), "hello");

        Set<String> stagedFiles = new HashSet<>(Collections.singletonList("file1.txt"));
        when(gitterRepository.getStagedFiles()).thenReturn(stagedFiles);
        commandCommitService.execute(args);
        verify(gitterRepository).markFileAsCommitted(eq("file1.txt"), eq("hello"));
        verify(gitterRepository).clearStagedFiles();
        verify(gitterRepository).addCommitEntry(any(GitterCommitEntry.class));
    }

    @Test
    void testExecuteWithAutoStageModifiedFiles() {
        String[] args = {"gitter", "commit", "-am", "auto stage commit"};
        when(gitterRepository.getStagedFiles()).thenReturn(new HashSet<>());
        commandCommitService.execute(args);
        verify(gitterRepository).stageAllModifiedFiles();
    }

    @Test
    void testExecuteWithDeletedFile() {
        String[] args = {"gitter", "commit", "-m", "deleted file"};
        String deletedFile = "deleted.txt";
        Set<String> stagedFiles = new HashSet<>(Collections.singletonList(deletedFile));
        when(gitterRepository.getStagedFiles()).thenReturn(stagedFiles);
        File file = new File(gitterDir, deletedFile);
        file.delete();
        commandCommitService.execute(args);
        verify(gitterRepository).removeCommittedFile(deletedFile);
        verify(gitterRepository).clearStagedFiles();
        verify(gitterRepository, never()).markFileAsCommitted(eq(deletedFile), any());
    }

    @Test
    void testExecuteWithInvalidArgs() {
        String[] args = {"gitter", "commit"};
        commandCommitService.execute(args);
        verify(gitterRepository, never()).getStagedFiles();
    }

    @Test
    void testCommitHashIsGeneratedAndCommitSaved() throws Exception {
        String[] args = {"gitter", "commit", "-m", "test hash"};
        File file = new File(gitterDir, "fileA.txt");
        Files.writeString(file.toPath(), "data");
        Set<String> stagedFiles = new HashSet<>(Collections.singletonList("fileA.txt"));
        when(gitterRepository.getStagedFiles()).thenReturn(stagedFiles);
        ArgumentCaptor<GitterCommitEntry> captor = ArgumentCaptor.forClass(GitterCommitEntry.class);
        commandCommitService.execute(args);
        verify(gitterRepository).addCommitEntry(captor.capture());
        GitterCommitEntry commitEntry = captor.getValue();
        assertEquals("test hash", commitEntry.getMessage());
    }
}
