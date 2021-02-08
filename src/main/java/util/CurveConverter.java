package util;

import structures.Point;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class CurveConverter {

    private static final String CONTINUOUS_CURVE_PREFIX = "#";

    private CurveConverter() {
    }

    public static List<String> serializePartedCurve(List<List<Point>> partedCurve) {
        var fileContents = new ArrayList<String>();

        for (int i = 0; i < partedCurve.size(); i++) {
            var part = partedCurve.get(i);
            fileContents.add(CONTINUOUS_CURVE_PREFIX + i);

            for (var point : part) {
                fileContents.add(String.valueOf(point.x));
                fileContents.add(String.valueOf(point.y));
            }
        }

        return fileContents;
    }

    public static List<List<Point>> deserializePartedCurve(List<String> lines) {
        var partedCurve = new ArrayList<List<Point>>();

        for (var index = 0; index < lines.size();) {
            var line = lines.get(index);

            if (line.startsWith(CONTINUOUS_CURVE_PREFIX)) {
                partedCurve.add(new ArrayList<>());
                index++;
            }
            else {
                var lastCurve = partedCurve.get(partedCurve.size() - 1);
                var x = tryParsePointCoordinate(lines.get(index++));
                var y = tryParsePointCoordinate(lines.get(index++));
                lastCurve.add(new Point(x, y));
            }
        }

        return partedCurve;
    }

    public static List<String> convertFileToLinesIfPossible(File symbolFile, int numberOfRepresentativePoints) throws IOException {
        var lines = Files.readAllLines(symbolFile.toPath());

        if (countPoints(lines) != numberOfRepresentativePoints) {
            System.err.println("Corrupted symbol pattern file '" + symbolFile.getAbsolutePath() + "':");
            System.err.println("Does not contain " + numberOfRepresentativePoints + " points.");
            return null;
        }

        return lines;
    }

    public static double[] convertLinesToSampleDataIfPossible(List<String> lines, File symbolFile) {
        var sample = new ArrayList<Double>();

        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.startsWith(CONTINUOUS_CURVE_PREFIX)) continue;

            try {
                sample.add(tryParsePointCoordinate(line));
            } catch (NumberFormatException exception) {
                System.err.println("Corrupted symbol file '" + symbolFile.getAbsolutePath() + "':");
                System.err.println("Line " + (i + 1) + " \"" + line + "\": " + exception.getMessage());
                return null;
            }
        }

        return sample.stream().mapToDouble(d -> d).toArray();
    }

    private static double tryParsePointCoordinate(String line) {
        var pointCoordinate = Double.parseDouble(line);

        if (Double.isNaN(pointCoordinate))
            throw new NumberFormatException("NaN is not a valid point coordinate value.");

        if (Double.isInfinite(pointCoordinate))
            throw new NumberFormatException("Point coordinate must be a finite value.");

        return pointCoordinate;
    }

    private static int countPoints(List<String> lines) {
        var pointCount = 0;

        for (var line : lines) {
            if (line.startsWith(CONTINUOUS_CURVE_PREFIX)) continue;
            pointCount++;
        }

        return pointCount / 2;
    }

    public static int countParts(List<String> lines) {
        for (var i = lines.size() - 1; i >= 0; i--) {
            var line = lines.get(i);

            if (line.startsWith(CONTINUOUS_CURVE_PREFIX)) {
                return Integer.parseInt(line.substring(CONTINUOUS_CURVE_PREFIX.length())) + 1;
            }
        }

        return -1;
    }
}
