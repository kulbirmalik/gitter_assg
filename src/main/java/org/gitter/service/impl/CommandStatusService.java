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
import java.util.stream.Collectors;

import static org.gitter.utils.CommandServiceUtils.listAllFilesRecursively;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandStatusService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        printStatus();
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.STATUS;
    }

    private void printStatus() {
        File workingDir = new File(".gitter");
        if (!workingDir.exists()) return;

        Set<String> stagedFiles = gitterRepository.getStagedFiles();
        Set<String> committedFiles = gitterRepository.getCommittedFiles();

        List<File> allFiles = listAllFilesRecursively(workingDir);
        Set<String> currentFileNames = allFiles.stream()
                .map(file -> workingDir.toPath().relativize(file.toPath()).toString().replace("\\", "/"))
                .filter(name -> !(name.equals("HEAD") || name.startsWith("refs")))
                .collect(Collectors.toSet());

        Map<String, String> toBeCommitted = new LinkedHashMap<>();
        Map<String, String> notStaged = new LinkedHashMap<>();
        List<String> untracked = new ArrayList<>();

        for (String filename : currentFileNames) {
            boolean isCommitted = committedFiles.contains(filename);
            boolean isStaged = stagedFiles.contains(filename);
            File file = new File(workingDir, filename);

            if (!isCommitted) {
                if (isStaged) {
                    toBeCommitted.put(filename, "created");
                } else {
                    untracked.add(filename);
                }
            } else {
                try {
                    String currentContent = Files.readString(file.toPath());
                    String committedContent = gitterRepository.getCommittedFileContent(filename);

                    boolean isModified = !Objects.equals(currentContent, committedContent);

                    if (isModified) {
                        if (isStaged) {
                            toBeCommitted.put(filename, "modified");
                        } else {
                            notStaged.put(filename, "modified");
                        }
                    }
                } catch (IOException ignored) {}
            }
        }

        for (String committedFile : committedFiles) {
            if (!currentFileNames.contains(committedFile)) {
                if (stagedFiles.contains(committedFile)) {
                    toBeCommitted.put(committedFile, "deleted");
                } else {
                    notStaged.put(committedFile, "deleted");
                }
            }
        }

        if (!toBeCommitted.isEmpty()) {
            System.out.println("Changes to be committed:");
            toBeCommitted.forEach((file, status) -> System.out.println("  " + status + ": " + file));
            System.out.println();
        }

        if (!notStaged.isEmpty()) {
            System.out.println("Changes not staged for commit:");
            notStaged.forEach((file, status) -> System.out.println("  " + status + ": " + file));
            System.out.println();
        }

        if (!untracked.isEmpty()) {
            System.out.println("Untracked files:");
            untracked.forEach(f -> System.out.println("  " + f));
        }
    }
}
