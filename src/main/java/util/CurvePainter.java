package util;

import structures.Point;

import java.awt.*;
import java.util.List;

public final class CurvePainter {

    private static final int FIRST = 0;
    private static final int MIN_POINT_COUNT_FOR_PAINTING = 2;

    private CurvePainter() {
    }

    public static void drawContinuousCurve(Graphics2D g, List<Point> continuousCurve) {
        if (continuousCurve == null || continuousCurve.size() < MIN_POINT_COUNT_FOR_PAINTING) return;

        var lastPoint = continuousCurve.get(FIRST);

        for (int i = 1; i < continuousCurve.size(); i++) {
            var currentPoint = continuousCurve.get(i);
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) currentPoint.x, (int) currentPoint.y);
            lastPoint = currentPoint;
        }
    }

    public static void drawRepresentativePoints(Graphics2D g, List<Point> dots, int radius) {
        final int diameter = 2 * radius;

        for (var dot : dots) {
            var x = (int) (dot.x - radius);
            var y = (int) (dot.y - radius);
            g.drawOval(x, y, diameter, diameter);
        }
    }
}
