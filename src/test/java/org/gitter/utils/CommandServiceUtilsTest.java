package org.gitter.utils;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.gitter.utils.Constants.GITTER_DIR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandServiceUtilsTest {

    // Test for listAllFilesInDirectory method
    @Test
    void testListAllFilesInDirectory_withFiles() {
        // Arrange
        File mockDir = mock(File.class);
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        File dir1 = mock(File.class);

        when(mockDir.listFiles()).thenReturn(new File[]{file1, file2});
        when(file1.isDirectory()).thenReturn(false);
        when(file1.getName()).thenReturn("file1.txt");
        when(file2.isDirectory()).thenReturn(true);
        when(file2.getName()).thenReturn("dir1");
        when(dir1.listFiles()).thenReturn(new File[0]);

        // Act
        List<File> files = CommandServiceUtils.listAllFilesInDirectory(mockDir);

        // Assert
        assertEquals(1, files.size());
        assertTrue(files.contains(file1));
        assertFalse(files.contains(file2)); // Since file2 is a directory
    }

    @Test
    void testListAllFilesInDirectory_emptyDirectory() {
        // Arrange
        File mockDir = mock(File.class);
        when(mockDir.listFiles()).thenReturn(new File[0]);

        // Act
        List<File> files = CommandServiceUtils.listAllFilesInDirectory(mockDir);

        // Assert
        assertTrue(files.isEmpty());
    }

    @Test
    void testListAllFilesInDirectory_withExcludedFiles() {
        // Arrange
        File mockDir = mock(File.class);
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        when(mockDir.listFiles()).thenReturn(new File[]{file1, file2});
        when(file1.isDirectory()).thenReturn(false);
        when(file1.getName()).thenReturn("HEAD");
        when(file2.isDirectory()).thenReturn(false);
        when(file2.getName()).thenReturn("file2.txt");

        // Act
        List<File> files = CommandServiceUtils.listAllFilesInDirectory(mockDir);

        // Assert
        assertEquals(1, files.size());
        assertTrue(files.contains(file2));
    }

    // Test for getBaseGitterDir method
    @Test
    void testGetBaseGitterDir() {
        // Act
        File baseDir = CommandServiceUtils.getBaseGitterDir();

        // Assert
        assertNotNull(baseDir);
        assertEquals(GITTER_DIR, baseDir.getPath());
    }

    // Test for containsFile method
    @Test
    void testContainsFile_withMatchingFilePath() {
        // Arrange
        List<File> files = List.of(new File("src/file1.txt"));
        Path baseDir = Paths.get("src");
        String relativeFilePath = "file1.txt";

        // Act
        boolean containsFile = CommandServiceUtils.containsFile(files, baseDir, relativeFilePath);

        // Assert
        assertTrue(containsFile);
    }

    @Test
    void testContainsFile_withNonMatchingFilePath() {
        // Arrange
        List<File> files = List.of(new File("src/file1.txt"));
        Path baseDir = Paths.get("src");
        String relativeFilePath = "file2.txt";

        // Act
        boolean containsFile = CommandServiceUtils.containsFile(files, baseDir, relativeFilePath);

        // Assert
        assertFalse(containsFile);
    }

    @Test
    void testContainsFile_withEmptyFileList() {
        // Arrange
        List<File> files = List.of();
        Path baseDir = Paths.get("src");
        String relativeFilePath = "file1.txt";

        // Act
        boolean containsFile = CommandServiceUtils.containsFile(files, baseDir, relativeFilePath);

        // Assert
        assertFalse(containsFile);
    }

    @Test
    void testContainsFile_withDifferentFilePathSeparator() {
        // Arrange
        List<File> files = List.of(new File("src" + File.separator + "file1.txt"));
        Path baseDir = Paths.get("src");
        String relativeFilePath = "file1.txt";

        // Act
        boolean containsFile = CommandServiceUtils.containsFile(files, baseDir, relativeFilePath);

        // Assert
        assertTrue(containsFile);
    }
}
