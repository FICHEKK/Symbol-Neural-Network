package util;

import structures.Point;

import java.util.List;

public final class CurveGeometry {

    private CurveGeometry() {
    }

    public static double findPartedCurveMaximumAbsoluteCoordinate(List<List<Point>> partedCurve) {
        double max = Double.NEGATIVE_INFINITY;

        for (var row : partedCurve) {
            for (var point : row) {
                var absX = Math.abs(point.x);
                if (absX > max) max = absX;

                var absY = Math.abs(point.y);
                if (absY > max) max = absY;
            }
        }

        return max;
    }

    public static Point calculatePartedCurveCentroid(List<List<Point>> partedCurve) {
        double centroidX = 0;
        double centroidY = 0;
        int pointCount = 0;

        for (var part : partedCurve) {
            for (var point : part) {
                centroidX += point.x;
                centroidY += point.y;
            }

            pointCount += part.size();
        }

        return new Point(centroidX / pointCount, centroidY / pointCount);
    }
}
