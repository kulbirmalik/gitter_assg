package org.gitter.model;

import lombok.Data;

import java.util.List;

@Data
public class GitterHelperMessage {

    private String title;
    private String usage;
    private String description;
    private List<String> options;

    public GitterHelperMessage(String title, String usage, String description, List<String> options) {
        this.title = title;
        this.usage = usage;
        this.description = description;
        this.options = options;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NAME:\n  ").append(title).append("\n\n");
        sb.append("SYNOPSIS:\n  ").append(usage).append("\n\n");
        sb.append("DESCRIPTION:\n  ").append(description).append("\n");
        if (options != null && !options.isEmpty()) {
            sb.append("\nOPTIONS:\n");
            for (String opt : options) {
                sb.append("  ").append(opt).append("\n");
            }
        }
        return sb.toString();
    }

}
