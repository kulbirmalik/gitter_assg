package org.gitter.repository;

import lombok.extern.slf4j.Slf4j;
import org.gitter.model.GitterCommitEntry;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Repository
public class GitterRepository {

    private final Set<String> stagedFiles = new HashSet<>();
    private final Set<String> committedFiles = new HashSet<>();
    private final Map<String, String> committedContent = new HashMap<>();
    private final List<GitterCommitEntry> commitHistory = new ArrayList<>();

    public void stageFiles(List<String> filesToStage) {
        stagedFiles.addAll(filesToStage);
    }

    public void stageAllModifiedFiles() {
        List<String> modifiedFiles = detectModifiedFiles();
        stagedFiles.addAll(modifiedFiles);
    }

    public void clearStagedFiles() {
        stagedFiles.clear();
    }

    public void markFileAsCommitted(String filename, String content) {
        committedFiles.add(filename);
        committedContent.put(filename, content);
    }

    public void removeCommittedFile(String filename) {
        committedFiles.remove(filename);
        committedContent.remove(filename);
    }

    public void addCommitEntry(GitterCommitEntry entry) {
        commitHistory.add(entry);
    }


    public List<GitterCommitEntry> getCommitHistory() {
        return new ArrayList<>(commitHistory);
    }

    public Set<String> getStagedFiles() {
        return Collections.unmodifiableSet(stagedFiles);
    }

    public Set<String> getCommittedFiles() {
        return Collections.unmodifiableSet(committedFiles);
    }

    public String getCommittedFileContent(String filename) {
        return committedContent.getOrDefault(filename, "");
    }

    public List<String> detectModifiedFiles() {
        List<String> modified = new ArrayList<>();
        for (String committedFile : committedFiles) {
            try {
                String currentContent = Files.readString(Paths.get(".gitter", committedFile));
                String committed = committedContent.getOrDefault(committedFile, "");
                if (!Objects.equals(currentContent, committed)) {
                    modified.add(committedFile);
                }
            } catch (IOException ignored) {

            }
        }
        return modified;
    }
}
