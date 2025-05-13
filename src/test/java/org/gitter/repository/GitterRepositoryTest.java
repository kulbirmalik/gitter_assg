package org.gitter.repository;

import org.gitter.model.GitterCommitEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GitterRepositoryTest {

    private GitterRepository gitterRepository;

    @BeforeEach
    void setUp() {
        gitterRepository = new GitterRepository();
    }

    @Test
    void testStageFiles() {
        gitterRepository.stageFiles(List.of("file1.txt", "file2.txt"));
        Set<String> staged = gitterRepository.getStagedFiles();
        assertEquals(Set.of("file1.txt", "file2.txt"), staged);
    }

    @Test
    void testClearStagedFiles() {
        gitterRepository.stageFiles(List.of("file1.txt"));
        gitterRepository.clearStagedFiles();
        assertTrue(gitterRepository.getStagedFiles().isEmpty());
    }

    @Test
    void testMarkAndRemoveCommittedFile() {
        gitterRepository.markFileAsCommitted("file1.txt", "content");
        assertTrue(gitterRepository.getCommittedFiles().contains("file1.txt"));
        assertEquals("content", gitterRepository.getCommittedFileContent("file1.txt"));

        gitterRepository.removeCommittedFile("file1.txt");
        assertFalse(gitterRepository.getCommittedFiles().contains("file1.txt"));
    }

    @Test
    void testAddCommitEntry() {
        GitterCommitEntry entry = new GitterCommitEntry("Initial commit", new Date(), "abc123");
        gitterRepository.addCommitEntry(entry);

        List<GitterCommitEntry> history = gitterRepository.getCommitHistory();
        assertEquals(1, history.size());
    }

    @Test
    void testDetectModifiedFiles(@TempDir File tempDir) throws IOException {
        File gitterDir = new File(tempDir, ".gitter");
        assertTrue(gitterDir.mkdir());

        File file = new File(gitterDir, "file1.txt");
        Files.writeString(file.toPath(), "initial content");

        gitterRepository.markFileAsCommitted("file1.txt", "initial content");
        Files.writeString(file.toPath(), "updated content");

        List<String> modified = gitterRepository.detectModifiedFiles();
        assertTrue(modified.contains("file1.txt"));
    }
}
