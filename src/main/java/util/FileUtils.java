package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

    private FileUtils() {
    }

    public static void deleteDirectory(Path directory) throws IOException {
        var files = directory.toFile().listFiles();

        if (files != null) {
            for (var file : files) {
                deleteDirectory(file.toPath());
            }
        }

        Files.delete(directory);
    }
}
