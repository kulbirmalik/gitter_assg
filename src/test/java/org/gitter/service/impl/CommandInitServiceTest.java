package org.gitter.service.impl;

import org.gitter.model.enums.CommandName;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class CommandInitServiceTest {

    private CommandInitService commandInitService;
    private static final String GITTER_DIR = ".gitter";

    @BeforeEach
    void setUp() throws IOException {
        commandInitService = new CommandInitService();
        deleteDirectory(new File(GITTER_DIR));
    }

    @AfterEach
    void cleanUp() throws IOException {
        deleteDirectory(new File(GITTER_DIR));
    }

    @Test
    void testGetCommandName() {
        assertEquals(CommandName.INIT, commandInitService.getCommandName());
    }

    @Test
    void testExecuteWhenGitterAlreadyExists() {
        File existingDir = new File(GITTER_DIR);
        assertTrue(existingDir.mkdir());
        commandInitService.execute(new String[]{"gitter", "init"});
        assertTrue(existingDir.exists());
    }

    @Test
    void testExecuteCreatesGitterAndMainBranch() throws IOException {
        commandInitService.execute(new String[]{"gitter", "init"});
        File gitterDir = new File(GITTER_DIR);
        assertTrue(gitterDir.exists());
        File mainBranch = new File(GITTER_DIR + "/refs/heads/main");
        assertTrue(mainBranch.exists());
        File headFile = new File(GITTER_DIR + "/HEAD");
        assertTrue(headFile.exists());
        String headContent = Files.readString(headFile.toPath());
        assertEquals("ref: refs/heads/main", headContent);
    }

    private static void deleteDirectory(File dir) throws IOException {
        if (dir.exists()) {
            Files.walk(dir.toPath())
                    .map(java.nio.file.Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2)) // delete children before parent
                    .forEach(File::delete);
        }
    }
}
