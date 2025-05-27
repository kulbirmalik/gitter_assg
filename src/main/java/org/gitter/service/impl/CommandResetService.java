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
import java.util.List;
import java.util.Map;

import static org.gitter.utils.CommandServiceUtils.getBaseGitterDir;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommandResetService implements CommandService {

    private final GitterRepository gitterRepo;

    @Override
    public void execute(String[] args) {
        handleReset();
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.RESET;
    }

    private void handleReset() {
        List<GitterCommitEntry> gitterCommitEntries = gitterRepo.getCommitHistory();
        if(gitterCommitEntries.isEmpty()){
            log.warn("No commits present to be reset.");
            return;
        }
        String prevCommitId = gitterCommitEntries.get(gitterCommitEntries.size() - 1).getHash();
        String lastPrevCommitId = gitterCommitEntries.get(gitterCommitEntries.size() - 2).getHash();
        Map<String, String> prevCommitContent = gitterRepo.getContentForCommitId(lastPrevCommitId);

        for (String commitedFile : prevCommitContent.keySet()) {
            File file = new File(getBaseGitterDir(), commitedFile);
            if (!file.exists()) {
                log.info("File not present : {}", file);
            } else {
                try {
                    String oldContent = prevCommitContent.get(commitedFile);
                    Files.writeString(file.toPath(), oldContent);
                } catch (IOException e) {
                    log.error("Failed to write content of file {}: {}", commitedFile, e.getMessage());
                }
            }
        }

        // remove that commitId from our storage
        gitterRepo.removeFromCommitIdContentMap(prevCommitId);
        gitterRepo.removeCommitFromHistory(prevCommitId);
    }
}
