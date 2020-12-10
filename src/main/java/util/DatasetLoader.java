package util;

import structures.Dataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatasetLoader {

    public static Dataset loadDataset(String loadDirectory, int numberOfRepresentativePoints) throws IOException {
        var loadDirPath = Paths.get(
                loadDirectory,
                String.valueOf(numberOfRepresentativePoints)
        );

        if (Files.notExists(loadDirPath)) return null;

        System.out.println("Loading symbols from '" + loadDirPath + "'...");
        return convertFilesToDataset(loadDirPath);
    }

    private static Dataset convertFilesToDataset(Path loadDirPath) throws IOException {
        int samples = countSamples(loadDirPath);
        var X = new double[samples][];
        var y = new double[samples][];
        int classIndex = 0;
        int sampleIndex = 0;

        var symbols = loadDirPath.toFile().listFiles(File::isDirectory);
        int numberOfClasses = symbols.length;
        var identifiers = new String[numberOfClasses];

        for (File symbolDir : symbols) {
            var expectedOutput = new double[numberOfClasses];
            expectedOutput[classIndex] = 1;
            identifiers[classIndex] = symbolDir.getName();
            classIndex++;

            for (File symbolPattern : symbolDir.listFiles(File::isFile)) {
                double[] sample = Files.readAllLines(symbolPattern.toPath()).stream()
                        .mapToDouble(Double::parseDouble)
                        .toArray();

                X[sampleIndex] = sample;
                y[sampleIndex] = expectedOutput;
                sampleIndex++;
            }
        }

        return new Dataset(X, y, identifiers);
    }

    private static int countSamples(Path loadDirPath) {
        int samples = 0;

        var symbolDirs = new File(loadDirPath.toString()).listFiles(File::isDirectory);

        for (var symbolDir : symbolDirs) {
            File[] symbolFiles = new File(symbolDir.toString()).listFiles(File::isFile);
            samples += symbolFiles.length;
        }

        return samples;
    }
}
