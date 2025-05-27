package org.gitter.repository;

import lombok.extern.slf4j.Slf4j;
import org.gitter.model.GitterCommitEntry;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.gitter.utils.CommandServiceUtils.getBaseGitterDir;

@Slf4j
@Repository
public class GitterRepository {

    private final Set<String> stagedFiles = new HashSet<>();
    private final Set<String> committedFiles = new HashSet<>();
    private final Map<String, String> committedContent = new HashMap<>();
    private final Map<String, Map<String, String>> commitIdToContentMap = new HashMap<>();
    private final List<GitterCommitEntry> commitHistory = new ArrayList<>();

    public void stageFiles(List<String> filesToStage) {
        stagedFiles.addAll(filesToStage);
    }

    public void clearStagedFiles() {
        stagedFiles.clear();
    }

    public void stageAllModifiedFiles() {
        List<String> modifiedFiles = detectModifiedFiles();
        stagedFiles.addAll(modifiedFiles);
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

    public void addCommitIdAndContent(String commitId, Map<String, String> commitedContent) {
        commitIdToContentMap.put(commitId, commitedContent);
    }

    public Map<String, String> getContentForCommitId(String commitId) {
        if(commitIdToContentMap.containsKey(commitId)){
            return commitIdToContentMap.get(commitId);
        }else{
            // should change this later
            throw new RuntimeException("Invalid CommitId");
        }
    }

    public void removeFromCommitIdContentMap(String commitId) {
        if(commitIdToContentMap.containsKey(commitId)){
            commitIdToContentMap.remove(commitId);
        }else{
            // should change this later
            throw new RuntimeException("Invalid CommitId");
        }
    }

    public List<GitterCommitEntry> getCommitHistory() {
        return new ArrayList<>(commitHistory);
    }

    public void removeCommitFromHistory(String commitId) {
        GitterCommitEntry entryToBeRemoved = null;
        for(GitterCommitEntry gitterCommitEntry : commitHistory){
            if(commitId.equals(gitterCommitEntry.getHash())){
                entryToBeRemoved  = gitterCommitEntry;
                break;
            }
        }
        commitHistory.remove(entryToBeRemoved);
    }

    public Set<String> getStagedFiles() {
        return Collections.unmodifiableSet(stagedFiles);
    }

    public Set<String> getCommittedFiles() {
        return Collections.unmodifiableSet(committedFiles);
    }

    public Set<String> getCommittedFilesInDirectory(File directory) {
        Path dirPath = directory.toPath();
        return committedFiles.stream()
                .filter(path -> {
                    Path fullPath = getBaseGitterDir().toPath().resolve(path).normalize();
                    return fullPath.startsWith(dirPath.normalize());
                })
                .collect(Collectors.toUnmodifiableSet());
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

    public Set<String> detectModifiedFilesInDirectory(File directory) {
        Set<String> modified = new HashSet<>();
        Path dirPath = directory.toPath();

        for (String committedFile : committedFiles) {
            Path committedFilePath = getBaseGitterDir().toPath().resolve(committedFile);
            Path relativeToDir = dirPath.relativize(committedFilePath);

            if (!relativeToDir.startsWith("..") && !relativeToDir.isAbsolute()) {
                try {
                    String currentContent = Files.readString(committedFilePath);
                    String committed = committedContent.getOrDefault(committedFile, "");
                    if (!Objects.equals(currentContent, committed)) {
                        modified.add(committedFile);
                    }
                } catch (IOException exception) {
                    log.error("Failed in reading file : {} ", exception.getMessage());
                }
            }
        }

        return modified;
    }
}
