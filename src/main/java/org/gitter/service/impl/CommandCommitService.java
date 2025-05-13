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

import static org.gitter.utils.CommandServiceUtils.getBaseGitterDir;

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
        validateCommitRequest(args);
        String message = getCommitMessageFromRequest(args);
        if (args[2].equals("-am")) {
            gitterRepository.stageAllModifiedFiles();
        }

        Set<String> stagedFileList = gitterRepository.getStagedFiles();
        Map<String, String> currentCommit = new HashMap<>();

        for (String stagedFile : stagedFileList) {
            File file = new File(getBaseGitterDir(), stagedFile);
            if (!file.exists()) {
                gitterRepository.removeCommittedFile(stagedFile);
                log.info("File deleted and removed from commit: {}", stagedFile);
            } else {
                try {
                    String content = Files.readString(file.toPath());
                    gitterRepository.markFileAsCommitted(stagedFile, content);
                    currentCommit.put(stagedFile, content);
                } catch (IOException e) {
                    log.error("Failed to read content of file {}: {}", stagedFile, e.getMessage());
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

    private void validateCommitRequest(String[] args) {
        if (args.length < 3 || (!args[2].equals("-m") && !args[2].equals("-am"))) {
            log.warn("Usage: gitter commit -m \"<message>\" or gitter commit -am \"<message>\"");
        }
    }

    private String getCommitMessageFromRequest(String[] args) {
        int messageStartIndex = 3;
        return Arrays.stream(args).skip(messageStartIndex).collect(Collectors.joining(" ")).replaceAll("^\"|\"$", "");
    }

}
