package ui;

import settings.DataCollectingStageSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolCanvas extends JComponent {
    private static final float SYMBOL_STROKE_WIDTH = 3;
    private static final float REPRESENTATIVE_POINT_STROKE_WIDTH = 2;
    private static final double REPRESENTATIVE_POINT_RADIUS = 4;

    private static final Color SYMBOL_COLOR = Color.WHITE;
    private static final Color REPRESENTATIVE_SYMBOL_COLOR = Color.BLUE;
    private static final Color REPRESENTATIVE_POINT_COLOR = Color.GREEN;

    private final DataCollectingStageSettings settings;
    private final List<SymbolCanvasListener> listeners = new ArrayList<>();
    private List<Point> points = new ArrayList<>();
    private List<Point> representativePoints;

    public SymbolCanvas(DataCollectingStageSettings settings) {
        this.settings = settings;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                representativePoints = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (points.isEmpty()) {
                    repaint();
                    return;
                }

                var numberOfRepresentativePoints = settings.getNumberOfRepresentativePoints();
                representativePoints = Point.getRepresentativePoints(
                        points,
                        numberOfRepresentativePoints
                );

                var centroid = Point.calculateCentroid(points);
                points = points.stream().map(point -> point.minus(centroid)).collect(Collectors.toList());

                var maximumAbsoluteXY = Point.findMaximumAbsoluteXY(points);
                var scalar = 1 / Math.max(maximumAbsoluteXY.x, maximumAbsoluteXY.y);
                points = points.stream().map(point -> point.scale(scalar)).collect(Collectors.toList());

                var normalizedRepresentativePoints = Point.getRepresentativePoints(
                        points,
                        numberOfRepresentativePoints
                );

                listeners.forEach(listener -> listener.onNextSymbol(normalizedRepresentativePoints));
                points = new ArrayList<>();
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    public void addListener(SymbolCanvasListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SymbolCanvasListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawCurveFromPoints((Graphics2D) g, points, SYMBOL_COLOR, false);

        if (settings.showRepresentativeSymbol())
            drawCurveFromPoints((Graphics2D) g, representativePoints, REPRESENTATIVE_SYMBOL_COLOR, true);
    }

    private static void drawCurveFromPoints(Graphics2D g, List<Point> points, Color color, boolean drawDotForEachPoint) {
        if (points == null || points.isEmpty()) return;

        g.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH));
        g.setColor(color);

        var lastPoint = points.get(0);

        for (int i = 1; i < points.size(); i++) {
            var currentPoint = points.get(i);
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) currentPoint.x, (int) currentPoint.y);
            lastPoint = currentPoint;
        }

        if (drawDotForEachPoint) {
            g.setStroke(new BasicStroke(REPRESENTATIVE_POINT_STROKE_WIDTH));
            g.setColor(REPRESENTATIVE_POINT_COLOR);
            final int diameter = (int) (2 * REPRESENTATIVE_POINT_RADIUS);

            for (Point point : points) {
                var x = (int) (point.x - REPRESENTATIVE_POINT_RADIUS);
                var y = (int) (point.y - REPRESENTATIVE_POINT_RADIUS);
                g.drawOval(x, y, diameter, diameter);
            }
        }
    }
}
