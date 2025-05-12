package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.enums.CommandName;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;
import static org.gitter.utils.Constants.GITTER_DIR;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandInitService implements CommandService {

    @Override
    public void execute(String[] args) {
        handleInit();
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.INIT;
    }

    private void handleInit() {
        File gitterDir = new File(GITTER_DIR);
        if (gitterDir.exists()) {
            log.warn("Gitter repository already exists.");
        } else {
            gitterDir.mkdir();
            log.info("Successfully created init directory.");
            createDefaultBranch();
        }
    }

    private void createDefaultBranch() {
        File mainBranchFile = new File(GITTER_DIR + "/refs/heads/main");
        mainBranchFile.getParentFile().mkdirs();
        try {
            mainBranchFile.createNewFile();
            File headFile = new File(GITTER_DIR + "/HEAD");
            Files.writeString(headFile.toPath(), "ref: refs/heads/main");
            log.info("Default branch 'main' created.");
        } catch (IOException e) {
            log.error("Failed to create default branch: {}", e.getMessage());
        }
    }
}
