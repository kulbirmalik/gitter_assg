package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.GitterCommitEntry;
import org.gitter.model.enums.CommandName;
import org.gitter.repository.GitterRepository;
import org.gitter.service.CommandService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandLogService implements CommandService {

    private final GitterRepository gitterRepository;

    @Override
    public void execute(String[] args) {
        showCommitLog();
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.LOG;
    }

    private void showCommitLog() {
        List<GitterCommitEntry> commits = gitterRepository.getCommitHistory();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        for (GitterCommitEntry entry : commits) {
            System.out.println("commit " + entry.getHash());
            System.out.println("Author: user");
            System.out.println("Date: " + formatter.format(entry.getTimestamp()));
            System.out.println(entry.getMessage());
        }
    }
}
