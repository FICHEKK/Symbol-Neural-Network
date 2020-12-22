package ui;

import settings.Settings;
import settings.SettingsListener;
import structures.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolCanvas extends JComponent implements SettingsListener {
    private static final float SYMBOL_STROKE_WIDTH = 3;
    private static final float REPRESENTATIVE_POINT_STROKE_WIDTH = 2;
    private static final double REPRESENTATIVE_POINT_RADIUS = 4;

    private static final Color BACKGROUND_COLOR_ENABLED = Color.WHITE;
    private static final Color BACKGROUND_COLOR_DISABLED = new Color(0xE7E9EF);
    private static final Color SYMBOL_COLOR_WHILE_DRAWING = Color.BLACK;
    private static final Color SYMBOL_COLOR_AFTER_DRAWING = new Color(246, 121, 53, 255);
    private static final Color REPRESENTATIVE_SYMBOL_COLOR = Color.BLUE;
    private static final Color REPRESENTATIVE_POINT_COLOR = Color.RED;

    private final List<SymbolCanvasFinishListener> finishListeners = new ArrayList<>();
    private final List<SymbolCanvasUpdateListener> updateListeners = new ArrayList<>();

    private List<Point> points = new ArrayList<>();
    private List<Point> representativePoints;

    private final Settings settings;

    private boolean isDrawing;
    private boolean isDrawingEnabled;

    public SymbolCanvas(Settings settings) {
        this.settings = settings;
        this.settings.addListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isDrawingEnabled) return;
                points = new ArrayList<>();
                representativePoints = null;
                isDrawing = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDrawingEnabled) return;
                isDrawing = false;

                if (points.isEmpty()) {
                    repaint();
                    return;
                }

                finishListeners.forEach(listener -> listener.onNextSymbolFinish(getNormalizedRepresentativePoints()));
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isDrawingEnabled) return;

                points.add(new Point(e.getX(), e.getY()));

                if (settings.getBooleanProperty(Settings.SHOULD_SHOW_REPRESENTATIVE_POINTS)) {
                    representativePoints = Point.getRepresentativePoints(points, settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));
                }

                if (!updateListeners.isEmpty()) {
                    var normalizedRepresentativePoints = getNormalizedRepresentativePoints();
                    updateListeners.forEach(listener -> listener.onNextSymbolUpdate(normalizedRepresentativePoints));
                }

                repaint();
            }
        });
    }

    public void addSymbolFinishListener(SymbolCanvasFinishListener listener) {
        finishListeners.add(listener);
    }

    public void addSymbolUpdateListener(SymbolCanvasUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void setDrawingEnabled(boolean isDrawingEnabled) {
        if (this.isDrawingEnabled == isDrawingEnabled) return;
        this.isDrawingEnabled = isDrawingEnabled;
        repaint();
    }

    private List<Point> getNormalizedRepresentativePoints() {
        var representativePoints = Point.getRepresentativePoints(points, settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));

        var centroid = Point.calculateCentroid(representativePoints);
        var translatedRepresentativePoints = representativePoints.stream().map(point -> point.minus(centroid)).collect(Collectors.toList());

        var maximumAbsoluteXY = Point.findMaximumAbsoluteXY(translatedRepresentativePoints);
        var scalar = 1 / Math.max(maximumAbsoluteXY.x, maximumAbsoluteXY.y);

        return translatedRepresentativePoints.stream().map(point -> point.scale(scalar)).collect(Collectors.toList());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(isDrawingEnabled ? BACKGROUND_COLOR_ENABLED : BACKGROUND_COLOR_DISABLED);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawCurveFromPoints((Graphics2D) g, points, isDrawing ? SYMBOL_COLOR_WHILE_DRAWING : SYMBOL_COLOR_AFTER_DRAWING, false);

        if (settings.getBooleanProperty(Settings.SHOULD_SHOW_REPRESENTATIVE_POINTS)) {
            drawCurveFromPoints((Graphics2D) g, representativePoints, REPRESENTATIVE_SYMBOL_COLOR, true);
        }
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

    @Override
    public void onPropertyChange(String property) {
        if (points.isEmpty()) return;
        if (property.equals(Settings.SHOULD_SHOW_REPRESENTATIVE_POINTS) || property.equals(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)) {
            representativePoints = Point.getRepresentativePoints(points, settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS));
        }
    }
}
