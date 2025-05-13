package org.gitter.service.impl;

import org.gitter.repository.GitterRepository;
import org.gitter.utils.CommandServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.mockito.Mockito.*;

class CommandStatusServiceTest {

    private GitterRepository gitterRepository;
    private CommandStatusService statusService;

    @BeforeEach
    void setup() {
        gitterRepository = mock(GitterRepository.class);
        statusService = new CommandStatusService(gitterRepository);
    }

    @Test
    void testCreatedStagedFile(@TempDir Path tempDir) throws IOException {
        File gitterDir = tempDir.resolve(".gitter").toFile();
        gitterDir.mkdir();
        File newFile = new File(gitterDir, "new.txt");
        Files.writeString(newFile.toPath(), "hello");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(gitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(gitterDir)).thenReturn(List.of(newFile));

            when(gitterRepository.getStagedFiles()).thenReturn(Set.of("new.txt"));
            when(gitterRepository.getCommittedFiles()).thenReturn(Set.of());

            statusService.execute(new String[]{});
        }
    }

    @Test
    void testModifiedCommittedFile(@TempDir Path tempDir) throws IOException {
        File gitterDir = tempDir.resolve(".gitter").toFile();
        gitterDir.mkdir();
        File modFile = new File(gitterDir, "mod.txt");
        Files.writeString(modFile.toPath(), "new content");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(gitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(gitterDir)).thenReturn(List.of(modFile));

            when(gitterRepository.getStagedFiles()).thenReturn(Set.of());
            when(gitterRepository.getCommittedFiles()).thenReturn(Set.of("mod.txt"));
            when(gitterRepository.getCommittedFileContent("mod.txt")).thenReturn("old content");

            statusService.execute(new String[]{});
        }
    }

    @Test
    void testUntrackedFile(@TempDir Path tempDir) throws IOException {
        File gitterDir = tempDir.resolve(".gitter").toFile();
        gitterDir.mkdir();
        File file = new File(gitterDir, "unknown.txt");
        Files.writeString(file.toPath(), "hello");

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(gitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(gitterDir)).thenReturn(List.of(file));

            when(gitterRepository.getStagedFiles()).thenReturn(Set.of());
            when(gitterRepository.getCommittedFiles()).thenReturn(Set.of());

            statusService.execute(new String[]{});
        }
    }

    @Test
    void testDeletedFile(@TempDir Path tempDir) {
        File gitterDir = tempDir.resolve(".gitter").toFile();
        gitterDir.mkdir();

        try (MockedStatic<CommandServiceUtils> utils = mockStatic(CommandServiceUtils.class)) {
            utils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(gitterDir);
            utils.when(() -> CommandServiceUtils.listAllFilesInDirectory(gitterDir)).thenReturn(List.of());

            when(gitterRepository.getStagedFiles()).thenReturn(Set.of());
            when(gitterRepository.getCommittedFiles()).thenReturn(Set.of("gone.txt"));

            statusService.execute(new String[]{});
        }
    }
}
