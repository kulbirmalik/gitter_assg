package org.gitter.model;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class GitterCommitEntryTest {

    @Test
    void testConstructorAndGetters() {
        String expectedHash = "abc123";
        Date expectedTimestamp = new Date();
        String expectedMessage = "Initial commit";

        GitterCommitEntry entry = new GitterCommitEntry(expectedHash, expectedTimestamp, expectedMessage);

        assertEquals(expectedHash, entry.getHash());
        assertEquals(expectedTimestamp, entry.getTimestamp());
        assertEquals(expectedMessage, entry.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        Date timestamp = new Date();
        GitterCommitEntry entry1 = new GitterCommitEntry("hash1", timestamp, "commit1");
        GitterCommitEntry entry2 = new GitterCommitEntry("hash1", timestamp, "commit1");
        GitterCommitEntry entry3 = new GitterCommitEntry("hash2", timestamp, "commit2");

        assertEquals(entry1, entry2, "Entries with same values should be equal");
        assertEquals(entry1.hashCode(), entry2.hashCode(), "Equal entries should have same hash code");

        assertNotEquals(entry1, entry3, "Different entries should not be equal");
    }

    @Test
    void testToString() {
        GitterCommitEntry entry = new GitterCommitEntry("abc123", new Date(0), "First commit");
        String toStringOutput = entry.toString();
        assertTrue(toStringOutput.contains("abc123"));
        assertTrue(toStringOutput.contains("First commit"));
    }
}
