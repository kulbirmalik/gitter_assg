package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.GitterCommitEntry;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandCommitService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        handleCommit(args);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.COMMIT;
    }

    private void handleCommit(String[] args) {
        boolean autoStage = false;
        String message;

        if (args.length < 3 || (!args[2].equals("-m") && !args[2].equals("-am"))) {
            log.warn("Usage: gitter commit -m \"<message>\" or gitter commit -am \"<message>\"");
            return;
        }

        if (args[2].equals("-am")) {
            autoStage = true;
        }

        int messageStartIndex = 3;
        message = Arrays.stream(args).skip(messageStartIndex).collect(Collectors.joining(" ")).replaceAll("^\"|\"$", "");

        if (autoStage) {
            gitterRepository.stageAllModifiedFiles();
        }

        Set<String> stagedFiles = gitterRepository.getStagedFiles();
        Map<String, String> currentCommit = new HashMap<>();

        File baseRepo = new File(".gitter");
        for (String file : stagedFiles) {
            File targetFile = new File(baseRepo, file);
            if (!targetFile.exists()) {
                gitterRepository.removeCommittedFile(file);
                log.info("File deleted and removed from commit: {}", file);
            } else {
                try {
                    String content = Files.readString(targetFile.toPath());
                    gitterRepository.markFileAsCommitted(file, content);
                    currentCommit.put(file, content);
                } catch (IOException e) {
                    log.error("Failed to read content of file {}: {}", file, e.getMessage());
                }
            }
        }

        gitterRepository.clearStagedFiles();

        if (!currentCommit.isEmpty()) {
            String hash = generateHash(message + System.currentTimeMillis());
            gitterRepository.addCommitEntry(new GitterCommitEntry(hash, new Date(), message));
        }
    }

    private String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating commit hash", e);
        }
    }

}
