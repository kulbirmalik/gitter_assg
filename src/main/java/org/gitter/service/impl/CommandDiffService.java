package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.gitter.utils.CommandServiceUtils.listAllFilesRecursively;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDiffService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        showDiff(args);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.DIFF;
    }

    private void showDiff(String[] args) {
        File baseRepoDir = new File(".gitter");
        if (!baseRepoDir.exists() || !baseRepoDir.isDirectory()) return;

        Path basePath = baseRepoDir.toPath();
        Set<String> modifiedFileSet = new HashSet<>(gitterRepository.detectModifiedFiles());
        Set<String> committedFiles = new HashSet<>(gitterRepository.getCommittedFiles());
        List<File> allFiles = listAllFilesRecursively(baseRepoDir);

        List<File> targetFiles = new ArrayList<>();
        Set<String> deletedFiles = new HashSet<>();

        // Determine the scope
        if (args.length > 2) {
            String inputPath = args[2];
            File inputFile = new File(".gitter", inputPath);
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    Path relPath = basePath.relativize(inputFile.toPath());
                    String relativePathString = relPath.toString().replace("\\", "/");

                    if (modifiedFileSet.contains(relativePathString) || !committedFiles.contains(relativePathString)) {
                        targetFiles.add(inputFile);
                    }

                    if (committedFiles.contains(relativePathString) && !inputFile.exists()) {
                        deletedFiles.add(relativePathString);
                    }

                } else if (inputFile.isDirectory()) {
                    List<File> allFilesInDirectory = listAllFilesRecursively(inputFile);

                    for (File file : allFilesInDirectory) {
                        Path relPath = basePath.relativize(file.toPath());
                        String relativePathString = relPath.toString().replace("\\", "/");

                        if ((modifiedFileSet.contains(relativePathString) || !committedFiles.contains(relativePathString))
                                && !file.getName().equals("HEAD")
                                && !relativePathString.startsWith("refs")) {
                            targetFiles.add(file);
                        }
                    }

                    for (String committedFile : committedFiles) {
                        if (committedFile.startsWith(inputPath + "/")) {
                            File file = new File(".gitter", committedFile);
                            if (!file.exists()) {
                                deletedFiles.add(committedFile);
                            }
                        }
                    }
                }
            } else {
                System.out.println("File or directory not found: " + inputPath);
                return;
            }
        }
        else {
            for (String modifiedFile : modifiedFileSet) {
                targetFiles.add(new File(".gitter", modifiedFile));
            }

            for (File file : allFiles) {
                Path relPath = basePath.relativize(file.toPath());
                String relative = relPath.toString().replace("\\", "/");
                if (!committedFiles.contains(relative)
                        && !file.getName().equals("HEAD")
                        && !relative.startsWith("refs")) {
                    targetFiles.add(file);
                }
            }

            deletedFiles = new HashSet<>(committedFiles);
            for (File file : allFiles) {
                Path relPath = basePath.relativize(file.toPath());
                String relative = relPath.toString().replace("\\", "/");
                deletedFiles.remove(relative);
            }
        }

        for (String deletedFile : deletedFiles) {
            System.out.println("--- a/" + deletedFile);
            System.out.println("+++ b/" + null);
            String committedContent = gitterRepository.getCommittedFileContent(deletedFile);
            for (String line : committedContent.split("\\R")) {
                System.out.println("-" + line);
            }
            System.out.println();
        }

        DiffMatchPatch dmp = new DiffMatchPatch();
        for (File file : targetFiles) {
            Path relPath = basePath.relativize(file.toPath());
            String relative = relPath.toString().replace("\\", "/");

            try {
                String currentContent = Files.readString(file.toPath());
                String committedContent = gitterRepository.getCommittedFileContent(relative);

                if (!committedFiles.contains(relative)) {
                    System.out.println("+++ b/" + relative);
                    for (String line : currentContent.split("\\R")) {
                        System.out.println("+" + line);
                    }
                } else {
                    System.out.println("--- a/" + relative);
                    System.out.println("+++ b/" + relative);

                    List<String> committedLines = Arrays.asList(committedContent.split("\\R"));
                    List<String> currentLines = Arrays.asList(currentContent.split("\\R"));
                    int maxLines = Math.max(committedLines.size(), currentLines.size());

                    List<Integer> changedLineIndices = new ArrayList<>();
                    for (int i = 0; i < maxLines; i++) {
                        String oldLine = i < committedLines.size() ? committedLines.get(i) : "";
                        String newLine = i < currentLines.size() ? currentLines.get(i) : "";
                        if (!oldLine.equals(newLine)) {
                            LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(oldLine, newLine);
                            dmp.diffCleanupSemantic(diffs);
                            if (diffs.stream().anyMatch(d -> d.operation != DiffMatchPatch.Operation.EQUAL)) {
                                changedLineIndices.add(i);
                            }
                        }
                    }

                    int CONTEXT = 2;
                    Set<Integer> linesToPrint = new TreeSet<>();
                    for (int i : changedLineIndices) {
                        for (int j = Math.max(0, i - CONTEXT); j <= Math.min(maxLines - 1, i + CONTEXT); j++) {
                            linesToPrint.add(j);
                        }
                    }

                    for (int i : linesToPrint) {
                        String oldLine = i < committedLines.size() ? committedLines.get(i) : "";
                        String newLine = i < currentLines.size() ? currentLines.get(i) : "";

                        if (!oldLine.equals(newLine)) {
                            if (!oldLine.isEmpty()) System.out.println("-" + oldLine);
                            if (!newLine.isEmpty()) System.out.println("+" + newLine);
                        } else {
                            System.out.println(" " + oldLine);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error reading file {}: {}", file.getName(), e.getMessage());
            }
        }
    }

}

