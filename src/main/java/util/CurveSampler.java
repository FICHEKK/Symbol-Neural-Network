package util;

import structures.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CurveSampler {

    private CurveSampler() {
    }

    public static List<List<Point>> getNormalizedRepresentativePoints(List<List<Point>> partedCurve, int numberOfRepresentativePoints) {
        final var representativePoints = getRepresentativePoints(partedCurve, numberOfRepresentativePoints);
        final var centroid = CurveGeometry.calculatePartedCurveCentroid(representativePoints);

        var translatedRepresentativePoints = representativePoints.stream()
                .map(part -> part.stream()
                        .map(point -> point.minus(centroid))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        final var scalar = 1 / CurveGeometry.findPartedCurveMaximumAbsoluteCoordinate(translatedRepresentativePoints);

        return translatedRepresentativePoints.stream()
                .map(part -> part.stream()
                        .map(point -> point.scale(scalar))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<List<Point>> getRepresentativePoints(List<List<Point>> partedCurve, int numberOfRepresentativePoints) {
        var representativePointsPerPart = new ArrayList<List<Point>>(partedCurve.size());

        for (int i = 0; i < partedCurve.size(); i++)
            representativePointsPerPart.add(new ArrayList<>());

        var partedCurveLength = CurveMeter.measurePartedCurveLength(partedCurve);

        for (int k = 0; k < numberOfRepresentativePoints; k++) {
            var kLength = k * partedCurveLength / (numberOfRepresentativePoints - 1);
            var match = findClosestMatchingPointForGivenLength(kLength, partedCurve);
            representativePointsPerPart.get(match.partIndex).add(match.point);
        }

        return representativePointsPerPart;
    }

    private static Match findClosestMatchingPointForGivenLength(double length, List<List<Point>> partedCurve) {
        var currentLength = 0.0;

        for (int partIndex = 0; partIndex < partedCurve.size(); partIndex++) {
            var part = partedCurve.get(partIndex);
            var previousPoint = part.get(0);

            for (int pointIndex = 1; pointIndex < part.size(); pointIndex++) {
                final var currentPoint = part.get(pointIndex);
                final var distance = CurveMeter.distanceBetweenPoints(previousPoint, currentPoint);

                if (length >= currentLength && length <= currentLength + distance) {
                    var distanceToPrevious = Math.abs(length - currentLength);
                    double t = distanceToPrevious / distance;
                    return new Match(interpolate(previousPoint, currentPoint, t), partIndex);
                }

                currentLength += distance;
                previousPoint = currentPoint;
            }
        }

        var lastPartIndex = partedCurve.size() - 1;
        var lastPart = partedCurve.get(lastPartIndex);
        var lastPoint = lastPart.get(lastPart.size() - 1);
        return new Match(lastPoint, lastPartIndex);
    }

    private static Point interpolate(Point start, Point end, double percentage) {
        var x = (1 - percentage) * start.x + percentage * end.x;
        var y = (1 - percentage) * start.y + percentage * end.y;
        return new Point(x, y);
    }

    private static class Match {
        public final Point point;
        public final int partIndex;

        private Match(Point point, int partIndex) {
            this.point = point;
            this.partIndex = partIndex;
        }
    }
}
