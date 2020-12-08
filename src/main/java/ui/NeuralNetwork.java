package ui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NeuralNetwork {

    public void loadSymbols(String loadDirectory, int numberOfRepresentativePoints) {
        var loadDirPath = Paths.get(
                loadDirectory,
                String.valueOf(numberOfRepresentativePoints)
        );

        if (Files.notExists(loadDirPath)) {
            System.out.println("Error while loading symbols - load directory does not exist.");
            return;
        }
        else {
            System.out.println("Loading symbols from '" + loadDirPath + "'...");
        }

        for (var symbolDir : new File(loadDirPath.toString()).listFiles(File::isDirectory)) {
            File[] symbolFiles = new File(symbolDir.toString()).listFiles(File::isFile);
            int numberOfFiles = symbolFiles.length;
            System.out.println("Loaded " + numberOfFiles + " files for symbol '" + symbolDir + "'.");
        }
    }
}
