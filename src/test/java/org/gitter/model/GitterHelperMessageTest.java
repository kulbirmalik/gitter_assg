package org.gitter.model;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GitterHelperMessageTest {

    @Test
    void testConstructorAndGetters() {
        String title = "commit - Record changes to the repository";
        String usage = "gitter commit -m <msg>";
        String description = "Record a new commit with a message.";
        List<String> options = List.of("-m: Commit message");

        GitterHelperMessage message = new GitterHelperMessage(title, usage, description, options);

        assertEquals(title, message.getTitle());
        assertEquals(usage, message.getUsage());
        assertEquals(description, message.getDescription());
        assertEquals(options, message.getOptions());
    }

    @Test
    void testToStringWithOptions() {
        GitterHelperMessage message = new GitterHelperMessage(
                "add - Add file contents to the index",
                "gitter add .",
                "Adds files in the working directory to the index.",
                List.of("-a: Add all files", "-v: Verbose output")
        );

        String output = message.toString();
        assertTrue(output.contains("NAME:\n  add - Add file contents to the index"));
        assertTrue(output.contains("SYNOPSIS:\n  gitter add ."));
        assertTrue(output.contains("DESCRIPTION:\n  Adds files in the working directory to the index."));
        assertTrue(output.contains("OPTIONS:\n  -a: Add all files\n  -v: Verbose output"));
    }

    @Test
    void testToStringWithoutOptions() {
        GitterHelperMessage message = new GitterHelperMessage(
                "init - Create an empty Gitter repository",
                "gitter init",
                "Creates a new .gitter directory.",
                null
        );

        String output = message.toString();
        assertTrue(output.contains("NAME:\n  init - Create an empty Gitter repository"));
        assertTrue(output.contains("SYNOPSIS:\n  gitter init"));
        assertTrue(output.contains("DESCRIPTION:\n  Creates a new .gitter directory."));
        assertFalse(output.contains("OPTIONS:"));
    }

    @Test
    void testToStringWithEmptyOptionsList() {
        GitterHelperMessage message = new GitterHelperMessage(
                "log - Show commit logs",
                "gitter log",
                "Displays the history of commits.",
                List.of()
        );

        String output = message.toString();
        assertTrue(output.contains("DESCRIPTION:\n  Displays the history of commits."));
        assertFalse(output.contains("OPTIONS:"));
    }
}
