//package org.gitter.service.impl;
//
//import org.gitter.repository.GitterRepository;
//import org.gitter.model.enums.CommandName;
//import org.junit.jupiter.api.*;
//import org.mockito.*;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.*;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class CommandDiffServiceTest {
//
//    @Mock
//    private GitterRepository gitterRepository;
//
//    @InjectMocks
//    private CommandDiffService commandDiffService;
//
//    @Mock
//    private File mockBaseDir;
//
//    @Mock
//    private File mockFile1;
//
//    @Mock
//    private File mockFile2;
//
//    @Mock
//    private Path mockPath1;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(mockBaseDir.exists()).thenReturn(true);
//        ReflectionTestUtils.setField(commandDiffService, "gitterRepository", gitterRepository);
//    }
//
//    @Test
//    void testGetCommandName() {
//        assertEquals(CommandName.DIFF, commandDiffService.getCommandName());
//    }
//
//    @Test
//    void testHandleDiffRequest_whenBaseGitterDirNotExist() {
//        when(mockBaseDir.exists()).thenReturn(false);
//        commandDiffService.execute(new String[]{"gitter", "diff"});
//        verify(gitterRepository, never()).detectModifiedFilesInDirectory(any());
//        verify(gitterRepository, never()).getCommittedFilesInDirectory(any());
//    }
//
//    @Test
//    void testHandleDiffRequest_withoutPathArgument() {
//        when(gitterRepository.detectModifiedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("file1.txt", "file2.txt"));
//        when(gitterRepository.getCommittedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("file2.txt"));
//        when(mockFile1.exists()).thenReturn(true);
//        when(mockFile2.exists()).thenReturn(true);
//
//        Set<File> targetFiles = new HashSet<>();
//        Set<String> deletedFiles = new HashSet<>();
//
//        commandDiffService.execute(new String[]{"gitter", "diff"});
//
//        verify(gitterRepository).detectModifiedFilesInDirectory(any(File.class));
//        verify(gitterRepository).getCommittedFilesInDirectory(mockBaseDir);
//    }
//
//    @Test
//    void testHandleDiffRequest_withPathArgument_isFile() {
//        // Mock a single file being passed as argument
//        String pathArg = "somefile.txt";
//        File file = new File(".gitter", pathArg);
//        when(file.exists()).thenReturn(true);
//        when(gitterRepository.detectModifiedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("file1.txt"));
//        when(gitterRepository.getCommittedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("file1.txt"));
//
//        Set<File> targetFiles = new HashSet<>();
//        Set<String> deletedFiles = new HashSet<>();
//
//        commandDiffService.execute(new String[]{"gitter", "diff", pathArg});
//
//        verify(gitterRepository).detectModifiedFilesInDirectory(mockBaseDir);
//        verify(gitterRepository).getCommittedFilesInDirectory(mockBaseDir);
//    }
//
//    @Test
//    void testHandleDiffRequest_withDeletedFile() {
//        String pathArg = "deletedfile.txt";
//        File file = new File(".gitter", pathArg);
//        when(file.exists()).thenReturn(false);
//        when(gitterRepository.detectModifiedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("deletedfile.txt"));
//        when(gitterRepository.getCommittedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("deletedfile.txt"));
//
//        Set<File> targetFiles = new HashSet<>();
//        Set<String> deletedFiles = new HashSet<>();
//
//        commandDiffService.execute(new String[]{"gitter", "diff", pathArg});
//
//        assertTrue(deletedFiles.contains(pathArg));
//        verify(gitterRepository).getCommittedFilesInDirectory(mockBaseDir);
//    }
//
//    @Test
//    void testHandleDiffRequest_withModifiedFile() throws Exception {
//        String pathArg = "modifiedfile.txt";
//        File file = new File(".gitter", pathArg);
//        Path mockPath = mock(Path.class);
//        when(file.exists()).thenReturn(true);
//        when(file.toPath()).thenReturn(mockPath);
//        when(Files.readString(mockPath)).thenReturn("modified content");
//        when(gitterRepository.detectModifiedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("modifiedfile.txt"));
//        when(gitterRepository.getCommittedFilesInDirectory(mockBaseDir)).thenReturn(Set.of("modifiedfile.txt"));
//        when(gitterRepository.getCommittedFileContent(pathArg)).thenReturn("old content");
//
//        Set<File> targetFiles = new HashSet<>();
//        Set<String> deletedFiles = new HashSet<>();
//
//        commandDiffService.execute(new String[]{"gitter", "diff", pathArg});
//
//        verify(gitterRepository).getCommittedFilesInDirectory(mockBaseDir);
//        assertTrue(targetFiles.contains(file));
//    }
//
//    @Test
//    void testHandleDiffRequest_withIOException() throws Exception {
//        String pathArg = "file.txt";
//        File file = new File(".gitter", pathArg);
//        Path mockPath = mock(Path.class);
//        when(file.exists()).thenReturn(true);
//        when(file.toPath()).thenReturn(mockPath);
//        when(Files.readString(mockPath)).thenThrow(IOException.class);
//
//        commandDiffService.execute(new String[]{"gitter", "diff", pathArg});
//
//        // Assert error is logged
//        verify(gitterRepository, never()).detectModifiedFilesInDirectory(mockBaseDir);
//        verify(gitterRepository, never()).getCommittedFilesInDirectory(mockBaseDir);
//    }
//}
