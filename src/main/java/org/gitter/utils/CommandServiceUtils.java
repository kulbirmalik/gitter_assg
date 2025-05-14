package org.gitter.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.gitter.utils.Constants.GITTER_DIR;

public class CommandServiceUtils {

    public static List<File> listAllFilesInDirectory(File dir) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    result.addAll(listAllFilesInDirectory(file));
                } else if (!fileName.equals("HEAD") && !fileName.startsWith("main")){
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static File getBaseGitterDir() {
        return new File(GITTER_DIR);
    }

    public static boolean containsFile(List<File> files, Path baseDir, String relativeFilePath) {
        for (File file : files) {
            String filePath = baseDir.relativize(file.toPath()).toString().replace("\\", "/");
            if (filePath.equals(relativeFilePath)) {
                return true;
            }
        }
        return false;
    }

}
