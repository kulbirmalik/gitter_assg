package org.gitter.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

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
