package ui.views;

import structures.Point;
import util.CurvePainter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolView extends JComponent {

    private static final Color SYMBOL_COLOR = new Color(40, 76, 134, 255);
    private static final Stroke SYMBOL_STROKE = new BasicStroke(1f);
    private static final float WINDOW_PADDING = 0.1f;

    private static final Color REPRESENTATIVE_POINT_COLOR = new Color(193, 0, 167, 255);
    private static final int REPRESENTATIVE_POINT_RADIUS = 3;

    private List<List<Point>> normalizedPartedCurve;

    public void setSymbol(List<List<Point>> normalizedPartedCurve) {
        this.normalizedPartedCurve = normalizedPartedCurve;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (normalizedPartedCurve == null || normalizedPartedCurve.isEmpty()) return;

        var g2d = (Graphics2D) g;
        var screenScaledPartedCurve = scalePoints();

        for (var continuousCurve : screenScaledPartedCurve) {
            g2d.setStroke(SYMBOL_STROKE);
            g2d.setColor(SYMBOL_COLOR);
            CurvePainter.drawContinuousCurve(g2d, continuousCurve);

            g2d.setStroke(SYMBOL_STROKE);
            g2d.setColor(REPRESENTATIVE_POINT_COLOR);
            CurvePainter.drawRepresentativePoints(g2d, continuousCurve, REPRESENTATIVE_POINT_RADIUS);
        }
    }

    private List<List<Point>> scalePoints() {
        var center = new Point(getWidth() / 2.0, getHeight() / 2.0);
        var scaleFactor = Math.min(getWidth(), getHeight()) * (0.5f - WINDOW_PADDING);

        return normalizedPartedCurve.stream()
                .map(part -> part.stream()
                        .map(point -> point.scale(scaleFactor))
                        .map(point -> point.plus(center))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
