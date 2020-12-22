package ui.views;

import structures.Point;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolView extends JComponent {

    private static final Color SYMBOL_COLOR = new Color(40, 76, 134, 255);
    private static final float SYMBOL_STROKE = 1f;
    private static final float PADDING = 0.1f;

    private List<Point> normalizedPoints;

    public void setSymbol(List<Point> normalizedPoints) {
        this.normalizedPoints = normalizedPoints;
        repaint();
    }

    public void clear() {
        this.normalizedPoints = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (normalizedPoints == null) return;

        g.setColor(SYMBOL_COLOR);
        ((Graphics2D) g).setStroke(new BasicStroke(SYMBOL_STROKE));

        var screenScaledPoints = scalePoints();
        var lastPoint = screenScaledPoints.get(0);

        for (int i = 1; i < screenScaledPoints.size(); i++) {
            var currentPoint = screenScaledPoints.get(i);
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) currentPoint.x, (int) currentPoint.y);
            lastPoint = currentPoint;
        }
    }

    private List<Point> scalePoints() {
        var center = new Point(getWidth() / 2.0, getHeight() / 2.0);
        var scaleFactor = Math.min(getWidth(), getHeight()) * (0.5f - PADDING);

        return normalizedPoints.stream()
                .map(p -> p.scale(scaleFactor))
                .map(p -> p.plus(center))
                .collect(Collectors.toList());
    }
}
