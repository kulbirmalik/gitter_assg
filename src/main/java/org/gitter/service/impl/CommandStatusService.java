package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import static org.gitter.utils.CommandServiceUtils.*;
import static org.gitter.utils.CommandServiceUtils.getBaseGitterDir;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandStatusService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        handleStatusRequest();
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.STATUS;
    }

    private void handleStatusRequest() {
        if (!getBaseGitterDir().exists()) return;

        Set<String> stagedFiles = gitterRepository.getStagedFiles();
        Set<String> committedFiles = gitterRepository.getCommittedFiles();
        List<File> allFilesInDir = listAllFilesInDirectory(getBaseGitterDir());

        Map<String, String> toBeCommitted = new LinkedHashMap<>();
        Map<String, String> notStaged = new LinkedHashMap<>();
        List<String> untracked = new ArrayList<>();

        for (File file : allFilesInDir) {
            String fileName = getBaseGitterDir().toPath().relativize(file.toPath()).toString();
            boolean isCommitted = committedFiles.contains(fileName);
            boolean isStaged = stagedFiles.contains(fileName);

            if (!isCommitted) {
                if (isStaged) {
                    toBeCommitted.put(fileName, "created");
                } else {
                    untracked.add(fileName);
                }
            } else {
                try {
                    String currentContent = Files.readString(file.toPath());
                    String committedContent = gitterRepository.getCommittedFileContent(fileName);
                    boolean isModified = !Objects.equals(currentContent, committedContent);

                    if (isModified) {
                        if (isStaged) {
                            toBeCommitted.put(fileName, "modified");
                        } else {
                            notStaged.put(fileName, "modified");
                        }
                    }
                } catch (IOException ignored) {}
            }
        }

        for (String committedFile : committedFiles) {
            if (!containsFile(allFilesInDir, getBaseGitterDir().toPath(), committedFile)) {
                if (stagedFiles.contains(committedFile)) {
                    toBeCommitted.put(committedFile, "deleted");
                } else {
                    notStaged.put(committedFile, "deleted");
                }
            }
        }

        printStatus(toBeCommitted, notStaged, untracked);
    }

    private void printStatus(Map<String, String> toBeCommitted, Map<String, String> notStaged, List<String> untracked) {
        if (!toBeCommitted.isEmpty()) {
            log.info("Changes to be committed:");
            toBeCommitted.forEach((file, status) -> log.info("  {}: {}", status, file));
            log.info("");
        }

        if (!notStaged.isEmpty()) {
            log.info("Changes not staged for commit:");
            notStaged.forEach((file, status) -> log.info("  {}: {}", status, file));
            log.info("");
        }

        if (!untracked.isEmpty()) {
            log.info("Untracked files:");
            untracked.forEach(f -> log.info("  {}", f));
        }
    }
}
