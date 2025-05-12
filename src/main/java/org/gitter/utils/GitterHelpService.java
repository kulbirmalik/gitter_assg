package org.gitter.utils;

import jakarta.annotation.PostConstruct;
import org.gitter.model.GitterHelperMessage;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GitterHelpService {

    private final Map<String, GitterHelperMessage> gitterHelpMap = new HashMap<>();

    @PostConstruct
    public void initHelpDocs() {
        gitterHelpMap.put("init", new GitterHelperMessage(
                "init - Create an empty Gitter repository",
                "gitter init",
                "Create a new empty repository in the current directory by initializing a .gitter folder. The default branch is named 'main'.",
                null
        ));

        gitterHelpMap.put("add", new GitterHelperMessage(
                "add - Add file contents to the index",
                "gitter add .",
                "Add all files in the current working directory to the index.",
                null
        ));

        gitterHelpMap.put("commit", new GitterHelperMessage(
                "commit - Record changes to the repository",
                "gitter commit -m [-a] <msg>",
                "Create a new commit containing the current contents of the index and the given log message describing the changes. "
                        + "The new commit is a direct child of HEAD, usually the tip of the current branch, and the branch is updated to point to it.",
                List.of(
                        "-a: Automatically stage modified and deleted files.",
                        "-m: Use the given <msg> as the commit message. Multiple -m options are concatenated as paragraphs."
                )
        ));

        gitterHelpMap.put("status", new GitterHelperMessage(
                "status - Show the working tree status",
                "gitter status",
                "Displays paths that have differences between the working directory and the index, paths that are staged for commit, "
                        + "and untracked files. Helps understand what changes are staged, unstaged, or untracked.",
                null
        ));

        gitterHelpMap.put("diff", new GitterHelperMessage(
                "diff - Show changes between commits, commit and working tree, etc.",
                "gitter diff",
                "Displays line-by-line changes between committed and current working directory files. Shows deleted, modified, or added lines "
                        + "with context (like unified diff).",
                null
        ));

        gitterHelpMap.put("log", new GitterHelperMessage(
                "log - Show commit logs",
                "gitter log",
                "Displays the history of commits, showing commit hash, message, and timestamp in reverse chronological order.",
                null
        ));
    }

    public GitterHelperMessage getHelpForCommand(String command) {
        return gitterHelpMap.get(command);
    }

    public Map<String, GitterHelperMessage> getAllCommandHelps() {
        return gitterHelpMap;
    }

}
