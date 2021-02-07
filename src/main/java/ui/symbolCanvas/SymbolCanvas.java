package ui.symbolCanvas;

import structures.Point;
import ui.Colors;
import util.CurvePainter;
import util.CurveSampler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SymbolCanvas extends JComponent {
    private static final float SYMBOL_STROKE_WIDTH = 3;
    private static final float REPRESENTATIVE_POINT_STROKE_WIDTH = 2;
    private static final int REPRESENTATIVE_POINT_RADIUS = 4;

    private static final Color BACKGROUND_COLOR_ENABLED = Colors.WHITE;
    private static final Color BACKGROUND_COLOR_DISABLED = Colors.TINTED_WHITE;
    private static final Color SYMBOL_COLOR_WHILE_DRAWING = Colors.BLACK;
    private static final Color SYMBOL_COLOR_AFTER_DRAWING = Colors.MAGENTA;
    private static final Color REPRESENTATIVE_SYMBOL_COLOR = Colors.DARK_BLUE;
    private static final Color REPRESENTATIVE_POINT_COLOR = Colors.BLUE;

    private final List<SymbolCanvasUpdateListener> updateListeners = new ArrayList<>();
    private final List<SymbolCanvasFinishListener> finishListeners = new ArrayList<>();

    private final List<List<Point>> partedCurve = new ArrayList<>();
    private List<List<Point>> partedCurveRepresentativePoints;

    private int numberOfRepresentativePoints;
    private boolean showRepresentativePoints;
    private boolean isDrawing;
    private boolean isDrawingEnabled;

    public SymbolCanvas() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isDrawingEnabled || !SwingUtilities.isLeftMouseButton(e)) return;
                partedCurve.add(new ArrayList<>());
                isDrawing = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDrawingEnabled) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    removeLastContinuousCurveIfEmpty();
                }
                else if (SwingUtilities.isMiddleMouseButton(e)) {
                    discardSymbol();
                }
                else if (SwingUtilities.isRightMouseButton(e)) {
                    finishSymbol();
                }

                isDrawing = false;
                repaint();
            }

            private void removeLastContinuousCurveIfEmpty() {
                var lastPartIndex = partedCurve.size() - 1;
                var lastPart = partedCurve.get(lastPartIndex);

                if (lastPart.isEmpty()) {
                    partedCurve.remove(lastPartIndex);
                }
            }

            private void discardSymbol() {
                partedCurve.clear();
                partedCurveRepresentativePoints = null;
            }

            private void finishSymbol() {
                if (!finishListeners.isEmpty() && partedCurve.size() != 0) {
                    var normalizedPartedCurve = CurveSampler.getNormalizedRepresentativePoints(
                            partedCurve,
                            numberOfRepresentativePoints
                    );

                    finishListeners.forEach(listener -> listener.onNextSymbolFinish(normalizedPartedCurve));
                }

                partedCurve.clear();
                partedCurveRepresentativePoints = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isDrawingEnabled || !SwingUtilities.isLeftMouseButton(e)) return;

                var latestPart = partedCurve.get(partedCurve.size() - 1);
                latestPart.add(new Point(e.getX(), e.getY()));

                if (showRepresentativePoints) {
                    partedCurveRepresentativePoints = CurveSampler.getRepresentativePoints(partedCurve, numberOfRepresentativePoints);
                }

                if (!updateListeners.isEmpty()) {
                    var normalizedRepresentativePoints = CurveSampler.getNormalizedRepresentativePoints(
                            partedCurve,
                            numberOfRepresentativePoints
                    );

                    updateListeners.forEach(listener -> listener.onNextSymbolUpdate(normalizedRepresentativePoints));
                }

                repaint();
            }
        });
    }

    public void addSymbolUpdateListener(SymbolCanvasUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void addSymbolFinishListener(SymbolCanvasFinishListener listener) {
        finishListeners.add(listener);
    }

    public void setNumberOfRepresentativePoints(int numberOfRepresentativePoints) {
        if (this.numberOfRepresentativePoints == numberOfRepresentativePoints) return;
        this.numberOfRepresentativePoints = numberOfRepresentativePoints;

        if (!partedCurve.isEmpty()) {
            partedCurveRepresentativePoints = CurveSampler.getRepresentativePoints(partedCurve, numberOfRepresentativePoints);
            repaint();
        }
    }

    public void setShowRepresentativePoints(boolean showRepresentativePoints) {
        if (this.showRepresentativePoints == showRepresentativePoints) return;
        this.showRepresentativePoints = showRepresentativePoints;

        if (!partedCurve.isEmpty()) {
            partedCurveRepresentativePoints = CurveSampler.getRepresentativePoints(partedCurve, numberOfRepresentativePoints);
            repaint();
        }
    }

    public void setDrawingEnabled(boolean isDrawingEnabled) {
        if (this.isDrawingEnabled == isDrawingEnabled) return;
        this.isDrawingEnabled = isDrawingEnabled;
        repaint();
    }

    public boolean isDrawingEnabled() {
        return isDrawingEnabled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(isDrawingEnabled ? BACKGROUND_COLOR_ENABLED : BACKGROUND_COLOR_DISABLED);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawPartedCurve((Graphics2D) g, partedCurve, isDrawing ? SYMBOL_COLOR_WHILE_DRAWING : SYMBOL_COLOR_AFTER_DRAWING, true, false);

        if (showRepresentativePoints) {
            drawPartedCurve((Graphics2D) g, partedCurveRepresentativePoints, REPRESENTATIVE_SYMBOL_COLOR, false, true);
        }
    }

    private static void drawPartedCurve(Graphics2D g, List<List<Point>> partedCurve, Color color, boolean drawLines, boolean drawDots) {
        if (partedCurve == null || partedCurve.isEmpty()) return;

        for (var continuousCurve : partedCurve) {
            if (drawLines) {
                g.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH));
                g.setColor(color);
                CurvePainter.drawContinuousCurve(g, continuousCurve);
            }

            if (drawDots) {
                g.setStroke(new BasicStroke(REPRESENTATIVE_POINT_STROKE_WIDTH));
                g.setColor(SymbolCanvas.REPRESENTATIVE_POINT_COLOR);
                CurvePainter.drawRepresentativePoints(g, continuousCurve, REPRESENTATIVE_POINT_RADIUS);
            }
        }
    }
}
