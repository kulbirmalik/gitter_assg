package org.gitter.service.impl;

import org.gitter.repository.GitterRepository;
import org.gitter.utils.CommandServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.*;

import static org.gitter.utils.Constants.GITTER_DIR;
import static org.mockito.Mockito.*;

public class CommandAddServiceTest {

    private GitterRepository gitterRepo;
    private CommandAddService commandAddService;
    private File gitterDir;

    @BeforeEach
    public void setUp() {
        gitterRepo = mock(GitterRepository.class);
        commandAddService = new CommandAddService(gitterRepo);

        gitterDir = new File(GITTER_DIR);
        gitterDir.mkdirs(); // Optional: create if needed, or mock `exists()` via wrapper
    }

    @Test
    public void testExecute_AddAllFiles() {
        String[] args = {"gitter", "add", "."};

        when(gitterRepo.getCommittedFiles()).thenReturn(new HashSet<>());

        File file1 = new File(GITTER_DIR + "/file1.txt");
        File file2 = new File(GITTER_DIR + "/file2.java");

        try (MockedStatic<CommandServiceUtils> mockedStatic = Mockito.mockStatic(CommandServiceUtils.class)) {
            mockedStatic.when(() -> CommandServiceUtils.listAllFilesInDirectory(any()))
                    .thenReturn(List.of(file1, file2));

            commandAddService.execute(args);

            verify(gitterRepo, times(1)).stageFiles(List.of("file1.txt", "file2.java"));
        }
    }

    @Test
    public void testExecute_AddFilesMatchingPattern() {
        String[] args = {"gitter", "add", "*.txt"};

        when(gitterRepo.getCommittedFiles()).thenReturn(Set.of("file1.txt", "file2.txt"));

        File file1 = new File(GITTER_DIR + "/file1.txt");
        File file2 = new File(GITTER_DIR + "/note.log");

        try (MockedStatic<CommandServiceUtils> mockedStatic = Mockito.mockStatic(CommandServiceUtils.class)) {
            mockedStatic.when(() -> CommandServiceUtils.listAllFilesInDirectory(any()))
                    .thenReturn(List.of(file1, file2));

            commandAddService.execute(args);

            verify(gitterRepo).stageFiles(List.of("file1.txt", "file2.txt")); // includes deleted file2.txt
        }
    }

    @Test
    public void testExecute_DeletedFilesDetected() {
        String[] args = {"gitter", "add", "*"};

        File file1 = new File(GITTER_DIR + "/exists.txt");

        Set<String> committedFiles = Set.of("exists.txt", "missing.txt");

        when(gitterRepo.getCommittedFiles()).thenReturn(committedFiles);

        try (MockedStatic<CommandServiceUtils> mockedStatic = Mockito.mockStatic(CommandServiceUtils.class)) {
            mockedStatic.when(() -> CommandServiceUtils.listAllFilesInDirectory(any()))
                    .thenReturn(List.of(file1)); // missing.txt is not returned, hence deleted

            commandAddService.execute(args);

            verify(gitterRepo).stageFiles(List.of("exists.txt", "missing.txt"));
        }
    }

    @Test
    public void testExecute_InvalidRequest_NotEnoughArgs() {
        String[] args = {"gitter", "add"};

        commandAddService.execute(args);

        verify(gitterRepo, never()).stageFiles(anyList());
    }

    @Test
    public void testExecute_InvalidRequest_MissingRepoDir() {
        String[] args = {"gitter", "add", "."};

        // Simulate missing directory
        File fakeDir = new File(GITTER_DIR);
        fakeDir.delete();

        commandAddService.execute(args);

        verify(gitterRepo, never()).stageFiles(anyList());
    }
}
