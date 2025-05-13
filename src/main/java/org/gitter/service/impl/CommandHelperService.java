package org.gitter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitter.model.GitterHelperMessage;
import org.gitter.model.enums.CommandName;
import org.gitter.service.CommandService;
import org.gitter.utils.GitterHelpService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandHelperService implements CommandService {

    private final GitterHelpService gitterHelpService;

    @Override
    public void execute(String[] args) {
        handleHelp(args);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.HELP;
    }

    private void handleHelp(String[] args) {
        if (args.length == 2) {
            log.info("--------- These are common Gitter commands: --------------- ");
            gitterHelpService.getAllCommandHelps().values().forEach(val -> {
                log.info("{}", val.getTitle());
            });
        } else {
            String subcommand = args[2];
            GitterHelperMessage helperModel = gitterHelpService.getHelpForCommand(subcommand);
            if (helperModel == null) {
                log.warn("No help available for command: {}", subcommand);
            } else {
                log.info("NAME:\n{}", helperModel.getTitle());
                log.info("SYNOPSIS:\n{}", helperModel.getUsage());
                log.info("DESCRIPTION:\n{}", helperModel.getDescription());
                if (helperModel.getOptions() != null) {
                    log.info("OPTIONS:");
                    helperModel.getOptions().forEach(opt -> log.info("{}", opt));
                }
            }
        }
    }
}
