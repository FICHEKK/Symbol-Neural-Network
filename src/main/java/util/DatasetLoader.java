package util;

import structures.Dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DatasetLoader {

    // Used only for converting list to an array.
    private static final double[][] OF_DOUBLE = new double[0][];

    public static Dataset loadDataset(String loadDirectory, int numberOfRepresentativePoints) throws IOException {
        var loadDirPath = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints));

        if (Files.notExists(loadDirPath))
            throw new FileNotFoundException("Error loading symbols: Directory '" + loadDirPath + "' does not exist.");

        System.out.println("Loading symbols from '" + loadDirPath + "'...");
        return convertFilesToDataset(loadDirPath, numberOfRepresentativePoints);
    }

    private static Dataset convertFilesToDataset(Path loadDirPath, int numberOfRepresentativePoints) throws IOException {
        var X = new ArrayList<double[]>();
        var Y = new ArrayList<double[]>();

        var symbols = loadDirPath.toFile().listFiles(File::isDirectory);
        int numberOfClasses = symbols.length;
        int classIndex = 0;

        for (File symbolDir : symbols) {
            var expectedOutput = new double[numberOfClasses];
            expectedOutput[classIndex] = 1;
            classIndex++;

            var symbolFiles = symbolDir.listFiles(File::isFile);
            var samples = convertAllNonCorruptSymbolFilesToSamples(symbolFiles, numberOfRepresentativePoints);

            for (var sample : samples) {
                X.add(sample);
                Y.add(expectedOutput);
            }
        }

        System.out.println("Loaded " + X.size() + " samples.");
        return new Dataset(X.toArray(OF_DOUBLE), Y.toArray(OF_DOUBLE));
    }

    public static List<String> getIdentifiers(String loadDirectory, int numberOfRepresentativePoints) {
        var loadDirPath = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints));
        if (Files.notExists(loadDirPath)) return new ArrayList<>();

        return Arrays.stream(loadDirPath.toFile().listFiles(File::isDirectory))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private static List<double[]> convertAllNonCorruptSymbolFilesToSamples(File[] symbolFiles, int numberOfRepresentativePoints) throws IOException {
        var samples = new ArrayList<double[]>();

        for (File symbolFile : symbolFiles) {
            var lines = convertFileToLinesIfPossible(symbolFile, numberOfRepresentativePoints);
            if (lines == null) continue;

            var sample = convertLinesToSampleDataIfPossible(lines, symbolFile);
            if (sample == null) continue;

            samples.add(sample);
        }

        return samples;
    }

    private static List<String> convertFileToLinesIfPossible(File symbolFile, int numberOfRepresentativePoints) throws IOException {
        var lines = Files.readAllLines(symbolFile.toPath());

        if (lines.size() != numberOfRepresentativePoints * 2) {
            System.err.println("Corrupted symbol pattern file '" + symbolFile.getAbsolutePath() + "':");
            System.err.println("Does not contain " + numberOfRepresentativePoints + " points.");
            return null;
        }

        return lines;
    }

    private static double[] convertLinesToSampleDataIfPossible(List<String> lines, File symbolFile) {
        var sample = new double[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            try {
                sample[i] = Double.parseDouble(lines.get(i));
            } catch (NumberFormatException exception) {
                System.err.println("Corrupted symbol file '" + symbolFile.getAbsolutePath() + "':");
                System.err.println("Line " + (i + 1) + " \"" + lines.get(i) + "\" not convertible to a double value.");
                return null;
            }
        }

        return sample;
    }

    public static Map<String, Integer> getSymbolToSampleCount(String loadDirectory, int numberOfRepresentativePoints) {
        var loadDirPath = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints));
        var sampleToCount = new LinkedHashMap<String, Integer>();
        if (Files.notExists(loadDirPath)) return sampleToCount;

        var symbolDirs = new File(loadDirPath.toString()).listFiles(File::isDirectory);

        for (var symbolDir : symbolDirs) {
            File[] symbolFiles = new File(symbolDir.toString()).listFiles(File::isFile);
            if (symbolFiles == null) continue;
            sampleToCount.put(symbolDir.getName(), symbolFiles.length);
        }

        return sampleToCount;
    }

    public static List<String> getSymbolSamples(String loadDirectory, int numberOfRepresentativePoints, String identifier) {
        var path = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints), identifier);
        var samples = new ArrayList<String>();
        if (Files.notExists(path)) return samples;

        var sampleFiles = new File(path.toString()).listFiles(File::isFile);

        for (var sampleFile : sampleFiles) {
            samples.add(sampleFile.getName());
        }

        return samples;
    }
}
