package structures;

import java.util.ArrayList;
import java.util.List;

public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point plus(Point point) {
        return new Point(x + point.x, y + point.y);
    }

    public Point minus(Point point) {
        return new Point(x - point.x, y - point.y);
    }

    public Point scale(double scalar) {
        return new Point(x * scalar, y * scalar);
    }

    public static Point calculateCentroid(List<Point> points) {
        double centroidX = 0;
        double centroidY = 0;

        for (var point : points) {
            centroidX += point.x;
            centroidY += point.y;
        }

        centroidX /= points.size();
        centroidY /= points.size();

        return new Point(centroidX, centroidY);
    }

    public static Point findMaximumAbsoluteXY(List<Point> points) {
        double maxX = Float.NEGATIVE_INFINITY;
        double maxY = Float.NEGATIVE_INFINITY;

        for (var point : points) {
            var absX = Math.abs(point.x);
            var absY = Math.abs(point.y);
            if (absX > maxX) maxX = absX;
            if (absY > maxY) maxY = absY;
        }

        return new Point(maxX, maxY);
    }

    public static double distanceBetweenPoints(Point point1, Point point2) {
        double dx = point1.x - point2.x;
        double dy = point1.y - point2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double calculateCurveLength(List<Point> curvePoints) {
        double length = 0;
        var lastPoint = curvePoints.get(0);

        for (int i = 1; i < curvePoints.size(); i++) {
            var currentPoint = curvePoints.get(i);
            length += distanceBetweenPoints(lastPoint, currentPoint);
            lastPoint = currentPoint;
        }

        return length;
    }

    public static List<Point> getRepresentativePoints(List<Point> curvePoints, int numberOfPoints) {
        var representativePoints = new ArrayList<Point>();
        var curveLength = calculateCurveLength(curvePoints);

        for (int k = 0; k < numberOfPoints; k++) {
            var kLength = k * curveLength / (numberOfPoints - 1);
            representativePoints.add(findClosestMatchingPointOnCurveForGivenLength(kLength, curvePoints));
        }

        return representativePoints;
    }

    private static Point findClosestMatchingPointOnCurveForGivenLength(double length, List<Point> curvePoints) {
        var currentLength = 0.0;
        var previousPoint = curvePoints.get(0);

        for (int i = 1; i < curvePoints.size(); i++) {
            var currentPoint = curvePoints.get(i);
            var distance = distanceBetweenPoints(previousPoint, currentPoint);

            if (length >= currentLength && length <= currentLength + distance) {
                var distanceToPrevious = Math.abs(length - currentLength);
                var distanceToCurrent = Math.abs(length - (currentLength + distance));
                return distanceToPrevious < distanceToCurrent ? previousPoint : currentPoint;
            }

            currentLength += distanceBetweenPoints(previousPoint, currentPoint);
            previousPoint = currentPoint;
        }

        return curvePoints.get(curvePoints.size() - 1);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}