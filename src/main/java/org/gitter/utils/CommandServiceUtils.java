package org.gitter.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandServiceUtils {

    public static List<File> listAllFilesRecursively(File dir) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(listAllFilesRecursively(file));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }

}
