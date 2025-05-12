package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.gitter.utils.CommandServiceUtils.listAllFilesRecursively;
import static org.gitter.utils.Constants.GITTER_DIR;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandAddService implements CommandService {

    private final GitterRepository gitterRepo;

    @Override
    public void execute(String[] args) {
        handleAdd(args);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.ADD;
    }

    private void handleAdd(String[] args) {
        if (args.length < 3) {
            log.warn("Usage: gitter add . OR gitter add <pattern>");
            return;
        }

        File gitterDir = new File(GITTER_DIR);
        if (!gitterDir.exists()) {
            log.error("Error: not a gitter repository (or any of the parent directories): .gitter");
            return;
        }

        String pattern = args[2];
        Pattern regexPattern;

        if (pattern.equals(".")) {
            regexPattern = Pattern.compile(".*");
        } else {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            regexPattern = Pattern.compile(regex);
        }

        List<File> allFiles = listAllFilesRecursively(gitterDir);
        Set<String> currentFileNames = allFiles.stream()
                .map(file -> gitterDir.toPath().relativize(file.toPath()).toString().replace("\\", "/"))
                .filter(name -> !(name.equals("HEAD") || name.startsWith("refs")))
                .collect(Collectors.toSet());

        List<String> filesToStage = currentFileNames.stream()
                .filter(name -> regexPattern.matcher(name).matches())
                .collect(Collectors.toList());

        Set<String> committedFiles = gitterRepo.getCommittedFiles();
        List<String> deletedFilesToStage = committedFiles.stream()
                .filter(name -> !currentFileNames.contains(name)) // file missing
                .filter(name -> regexPattern.matcher(name).matches())
                .collect(Collectors.toList());

        List<String> allFilesToStage = new ArrayList<>();
        allFilesToStage.addAll(filesToStage);
        allFilesToStage.addAll(deletedFilesToStage);

        gitterRepo.stageFiles(allFilesToStage);
    }

}
