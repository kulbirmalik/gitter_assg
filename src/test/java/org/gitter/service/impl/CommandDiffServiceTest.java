package org.gitter.service.impl;

import org.gitter.repository.GitterRepository;
import org.gitter.utils.CommandServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.io.File;
import java.util.*;

import static org.mockito.Mockito.*;

class CommandDiffServiceTest {

    private GitterRepository gitterRepository;
    private CommandDiffService commandDiffService;

    @BeforeEach
    void setUp() {
        gitterRepository = mock(GitterRepository.class);
        commandDiffService = new CommandDiffService(gitterRepository);
    }

    @Test
    void testExecuteWithDeletedFile() {
        File mockGitterDir = new File(".gitter");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(mockGitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(mockGitterDir))
                    .thenReturn(Collections.emptyList());

            when(gitterRepository.getCommittedFilesInDirectory(mockGitterDir))
                    .thenReturn(Set.of("file1.txt"));
            when(gitterRepository.getCommittedFileContent("file1.txt"))
                    .thenReturn("line1\nline2");

            commandDiffService.execute(new String[]{"gitter", "diff"});
        }
    }

    @Test
    void testExecuteWithModifiedFile() {
        File mockGitterDir = new File(".gitter");
        File mockFile = new File(".gitter/file1.txt");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(mockGitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(mockGitterDir))
                    .thenReturn(List.of(mockFile));

            when(gitterRepository.detectModifiedFilesInDirectory(mockGitterDir))
                    .thenReturn(Set.of("file1.txt"));
            when(gitterRepository.getCommittedFilesInDirectory(mockGitterDir))
                    .thenReturn(Set.of("file1.txt"));
            when(gitterRepository.getCommittedFileContent("file1.txt"))
                    .thenReturn("line1\nline2");

            // Mock file content via spy or avoid actual I/O depending on setup
            commandDiffService.execute(new String[]{"gitter", "diff"});
        }
    }

    @Test
    void testExecuteWithNonexistentInputFile() {
        File mockGitterDir = new File(".gitter");
        File nonExistentFile = new File(".gitter/nonexistent.txt");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(mockGitterDir);

            commandDiffService.execute(new String[]{"gitter", "diff", "nonexistent.txt"});
        }
    }

    @Test
    void testExecuteWithNewUntrackedFile() {
        File mockGitterDir = new File(".gitter");
        File newFile = new File(".gitter/newfile.txt");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(mockGitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(mockGitterDir))
                    .thenReturn(List.of(newFile));

            when(gitterRepository.detectModifiedFilesInDirectory(mockGitterDir))
                    .thenReturn(Collections.emptySet());
            when(gitterRepository.getCommittedFilesInDirectory(mockGitterDir))
                    .thenReturn(Collections.emptySet());

            commandDiffService.execute(new String[]{"gitter", "diff"});
        }
    }
}
