package util;

import structures.Point;

import java.util.List;

public final class CurveMeter {

    private CurveMeter() {
    }

    public static double measurePartedCurveLength(List<List<Point>> partedCurve) {
        double length = 0;

        for (var part : partedCurve) {
            length += measureContinuousCurveLength(part);
        }

        return length;
    }

    public static double measureContinuousCurveLength(List<Point> continuousCurve) {
        double length = 0;
        var lastPoint = continuousCurve.get(0);

        for (int i = 1; i < continuousCurve.size(); i++) {
            var currentPoint = continuousCurve.get(i);
            length += distanceBetweenPoints(lastPoint, currentPoint);
            lastPoint = currentPoint;
        }

        return length;
    }

    public static double distanceBetweenPoints(Point point1, Point point2) {
        double dx = point1.x - point2.x;
        double dy = point1.y - point2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
