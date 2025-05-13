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
import static org.gitter.utils.CommandServiceUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDiffService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        handleDiffRequest(args);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.DIFF;
    }

    private void handleDiffRequest(String[] args) {
        if (!getBaseGitterDir().exists()) return;

        Set<File> targetFiles = new HashSet<>();
        Set<String> deletedFiles = new HashSet<>();

        if (args.length > 2) {
            processPathArgument(args[2], targetFiles, deletedFiles);
        } else {
            collectDiffsFromDirectory(getBaseGitterDir(), targetFiles, deletedFiles);
        }
        printDeletedFileDiffs(deletedFiles);
        printModifiedFileDiffs(targetFiles);
    }

    private void processPathArgument(String pathArg, Set<File> targetFiles, Set<String> deletedFiles) {
        File inputFile = new File(".gitter", pathArg);
        if (!inputFile.exists()) {
            log.warn("Specified file or directory not found: {}", pathArg);
            return;
        }

        if (inputFile.isFile()) {
            handleSingleFile(inputFile, targetFiles, deletedFiles);
        } else {
            collectDiffsFromDirectory(inputFile, targetFiles, deletedFiles);
        }
    }

    private void handleSingleFile(File file, Set<File> targetFiles, Set<String> deletedFiles) {
        Path basePath = getBaseGitterDir().toPath();
        String relativePath = basePath.relativize(file.toPath()).toString().replace("\\", "/");

        Set<String> modifiedFiles = gitterRepository.detectModifiedFilesInDirectory(getBaseGitterDir());
        Set<String> committedFiles = gitterRepository.getCommittedFilesInDirectory(getBaseGitterDir());

        if (modifiedFiles.contains(relativePath) || !committedFiles.contains(relativePath)) {
            targetFiles.add(file);
        }
        if (committedFiles.contains(relativePath) && !file.exists()) {
            deletedFiles.add(relativePath);
        }
    }

    private void collectDiffsFromDirectory(File dir, Set<File> targetFiles, Set<String> deletedFiles) {
        Path basePath = getBaseGitterDir().toPath();
        Set<String> modifiedFiles = gitterRepository.detectModifiedFilesInDirectory(dir);
        Set<String> committedFiles = gitterRepository.getCommittedFilesInDirectory(dir);
        List<File> allFiles = listAllFilesInDirectory(dir);

        for (String modified : modifiedFiles) {
            targetFiles.add(new File(".gitter", modified));
        }

        for (File file : allFiles) {
            String relative = basePath.relativize(file.toPath()).toString().replace("\\", "/");
            if (!committedFiles.contains(relative)
                    && !file.getName().equals("HEAD")
                    && !relative.startsWith("refs")) {
                targetFiles.add(file);
            }
        }

        deletedFiles.addAll(committedFiles);
        for (File file : allFiles) {
            String relative = basePath.relativize(file.toPath()).toString().replace("\\", "/");
            deletedFiles.remove(relative);
        }
    }

    private void printDeletedFileDiffs(Set<String> deletedFiles) {
        for (String file : deletedFiles) {
            log.info("--- a/{}", file);
            log.info("+++ b/null");
            String content = gitterRepository.getCommittedFileContent(file);
            for (String line : content.split("\\R")) {
                log.info("-{}", line);
            }
            log.info("");
        }
    }

    private void printModifiedFileDiffs(Set<File> files) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        Path basePath = getBaseGitterDir().toPath();
        Set<String> committedFiles = gitterRepository.getCommittedFilesInDirectory(getBaseGitterDir());

        for (File file : files) {
            String relative = basePath.relativize(file.toPath()).toString().replace("\\", "/");
            try {
                String currentContent = Files.readString(file.toPath());
                String committedContent = gitterRepository.getCommittedFileContent(relative);

                if (!committedFiles.contains(relative)) {
                    log.info("+++ b/{}", relative);
                    for (String line : currentContent.split("\\R")) {
                        log.info("+{}", line);
                    }
                } else {
                    log.info("--- a/{}", relative);
                    log.info("+++ b/{}", relative);
                    printLineDiffs(dmp, committedContent, currentContent);
                }
            } catch (IOException e) {
                log.error("Error reading file {}: {}", file.getName(), e.getMessage());
            }
        }
    }

    private void printLineDiffs(DiffMatchPatch dmp, String committed, String current) {
        List<String> oldLines = Arrays.asList(committed.split("\\R"));
        List<String> newLines = Arrays.asList(current.split("\\R"));
        int max = Math.max(oldLines.size(), newLines.size());

        List<Integer> changedLines = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            String oldLine = i < oldLines.size() ? oldLines.get(i) : "";
            String newLine = i < newLines.size() ? newLines.get(i) : "";
            if (!oldLine.equals(newLine)) {
                LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(oldLine, newLine);
                dmp.diffCleanupSemantic(diffs);
                if (diffs.stream().anyMatch(d -> d.operation != DiffMatchPatch.Operation.EQUAL)) {
                    changedLines.add(i);
                }
            }
        }

        int CONTEXT = 2;
        Set<Integer> linesToPrint = new TreeSet<>();
        for (int index : changedLines) {
            for (int j = Math.max(0, index - CONTEXT); j <= Math.min(max - 1, index + CONTEXT); j++) {
                linesToPrint.add(j);
            }
        }

        for (int i : linesToPrint) {
            String oldLine = i < oldLines.size() ? oldLines.get(i) : "";
            String newLine = i < newLines.size() ? newLines.get(i) : "";
            if (!oldLine.equals(newLine)) {
                if (!oldLine.isEmpty()) log.info("-{}", oldLine);
                if (!newLine.isEmpty()) log.info("+{}", newLine);
            } else {
                log.info(" {}", oldLine);
            }
        }
    }
}
