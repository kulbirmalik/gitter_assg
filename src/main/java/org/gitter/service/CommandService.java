package org.gitter.service;

import org.gitter.model.enums.CommandName;

public interface CommandService {

    void execute(String[] args);

    CommandName getCommandName();
}
