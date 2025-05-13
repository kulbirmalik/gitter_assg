package org.gitter.model;
import lombok.Data;
import java.util.Date;

@Data
public class GitterCommitEntry {
    private final String hash;
    private final Date timestamp;
    private final String message;

    public GitterCommitEntry(String hash, Date timestamp, String message) {
        this.hash = hash;
        this.timestamp = timestamp;
        this.message = message;
    }
}
