package org.gitter.service.impl;

import org.gitter.repository.GitterRepository;
import org.gitter.model.enums.CommandName;
import org.gitter.utils.CommandServiceUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.gitter.utils.CommandServiceUtils.containsFile;
import static org.gitter.utils.CommandServiceUtils.listAllFilesInDirectory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandStatusServiceTest {

    private GitterRepository gitterRepository;
    private CommandStatusService commandStatusService;

    private File mockBaseDir;

    @BeforeEach
    void setUp() throws Exception {
        gitterRepository = mock(GitterRepository.class);
        commandStatusService = new CommandStatusService(gitterRepository);
        mockBaseDir = mock(File.class);
        when(mockBaseDir.exists()).thenReturn(true);
    }

    @Test
    void testGetCommandName() {
        assertEquals(CommandName.STATUS, commandStatusService.getCommandName());
    }

    @Test
    void testHandleStatusRequest_whenRepoNotInitialized() {
        try (
                MockedStatic<CommandServiceUtils> mockUtils = mockStatic(CommandServiceUtils.class)
        ) {
            File fake = mock(File.class);
            when(fake.exists()).thenReturn(false);
            mockUtils.when(CommandServiceUtils::getBaseGitterDir).thenReturn(fake);
            commandStatusService.execute(new String[]{"gitter", "status"});
            verify(gitterRepository, times(0)).getStagedFiles();
            verify(gitterRepository, times(0)).getCommittedFiles();
        }
    }
}
