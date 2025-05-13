package org.gitter.model.enums;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandNameTest {

    @Test
    void testAllEnumValuesPresent() {
        CommandName[] values = CommandName.values();
        assertEquals(8, values.length);
        assertArrayEquals(
                new CommandName[]{
                        CommandName.ADD,
                        CommandName.HELP,
                        CommandName.STATUS,
                        CommandName.DIFF,
                        CommandName.COMMIT,
                        CommandName.LOG,
                        CommandName.INIT,
                        CommandName.UNKNOWN
                },
                values
        );
    }

    @Test
    void testValueOfEachEnum() {
        assertEquals(CommandName.ADD, CommandName.valueOf("ADD"));
        assertEquals(CommandName.HELP, CommandName.valueOf("HELP"));
        assertEquals(CommandName.STATUS, CommandName.valueOf("STATUS"));
        assertEquals(CommandName.DIFF, CommandName.valueOf("DIFF"));
        assertEquals(CommandName.COMMIT, CommandName.valueOf("COMMIT"));
        assertEquals(CommandName.LOG, CommandName.valueOf("LOG"));
        assertEquals(CommandName.INIT, CommandName.valueOf("INIT"));
    }

    @Test
    void testInvalidEnumThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> CommandName.valueOf("UNKNOWN_ENUM"));
    }
}
