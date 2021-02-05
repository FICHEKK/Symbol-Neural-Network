package ui.views;

import structures.Point;
import util.ColorUtils;
import util.CurveMeter;
import util.CurvePainter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolView extends JComponent {

    private static final Color SYMBOL_COLOR_START = Color.MAGENTA;
    private static final Color SYMBOL_COLOR_END = Color.BLUE;
    private static final BasicStroke SYMBOL_STROKE = new BasicStroke(2f);

    private static final Color REPRESENTATIVE_POINT_COLOR = new Color(41, 0, 55, 255);
    private static final BasicStroke REPRESENTATIVE_POINT_STROKE = new BasicStroke(0f);
    private static final int REPRESENTATIVE_POINT_RADIUS = 2;

    private static final Color CONTINUOUS_CURVE_FIRST_POINT_TEXT_COLOR = Color.BLACK;
    private static final int COLOR_EXPLANATION_LINE_COUNT = 30;
    private static final float WINDOW_PADDING = 0.1f;

    private List<List<Point>> normalizedPartedCurve;
    private boolean showContinuousCurveIndex;
    private boolean showRepresentativePoints;

    private SymbolAnimationWorker animationWorker;

    public void setSymbol(List<List<Point>> normalizedPartedCurve) {
        this.normalizedPartedCurve = normalizedPartedCurve;
        repaint();
    }

    public void animateSymbol(List<List<Point>> normalizedPartedCurve) {
        if (animationWorker != null && !animationWorker.isDone()) {
            animationWorker.cancel(true);
        }

        animationWorker = new SymbolAnimationWorker(this, normalizedPartedCurve);
        animationWorker.execute();
    }

    public void setShowContinuousCurveIndex(boolean showContinuousCurveIndex) {
        if (this.showContinuousCurveIndex == showContinuousCurveIndex) return;
        this.showContinuousCurveIndex = showContinuousCurveIndex;
        repaint();
    }

    public void setShowRepresentativePoints(boolean showRepresentativePoints) {
        if (this.showRepresentativePoints == showRepresentativePoints) return;
        this.showRepresentativePoints = showRepresentativePoints;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (normalizedPartedCurve == null || normalizedPartedCurve.isEmpty()) return;
        var g2d = (Graphics2D) g;

        var totalPointCount = getTotalPointCount();
        var currentPointCount = 0f;
        var scaledPartedCurve = getScaledPartedCurve();

        for (int index = 0; index < scaledPartedCurve.size(); index++) {
            List<Point> continuousCurve = scaledPartedCurve.get(index);
            if (continuousCurve.isEmpty()) continue;

            g2d.setStroke(SYMBOL_STROKE);

            var startColor = ColorUtils.interpolateHSB(SYMBOL_COLOR_START, SYMBOL_COLOR_END, currentPointCount / totalPointCount);
            currentPointCount += continuousCurve.size();
            var endColor = ColorUtils.interpolateHSB(SYMBOL_COLOR_START, SYMBOL_COLOR_END, currentPointCount / totalPointCount);
            CurvePainter.drawColorInterpolatedContinuousCurve(g2d, continuousCurve, startColor, endColor);

            if (showContinuousCurveIndex) {
                g.setColor(CONTINUOUS_CURVE_FIRST_POINT_TEXT_COLOR);
                paintContinuousCurveIndex((Graphics2D) g, continuousCurve, index);
            }

            if (showRepresentativePoints) {
                g2d.setStroke(REPRESENTATIVE_POINT_STROKE);
                g2d.setColor(REPRESENTATIVE_POINT_COLOR);
                CurvePainter.drawRepresentativePoints(g2d, continuousCurve, REPRESENTATIVE_POINT_RADIUS);
            }
        }

        paintColorExplanationLine(g2d);
        paintColorExplanationText(g2d);
    }

    private void paintContinuousCurveIndex(Graphics2D g, List<Point> continuousCurve, int index) {
        var firstPoint = continuousCurve.get(0);
        var firstPointText = String.valueOf(index);
        var firstPointTextWidth = g.getFontMetrics().stringWidth(firstPointText);
        var firstPointTextHeight = g.getFontMetrics().getHeight();

        g.drawString(
                firstPointText,
                (int) firstPoint.x - firstPointTextWidth / 2,
                (int) firstPoint.y - firstPointTextHeight / 2
        );
    }

    private void paintColorExplanationLine(Graphics2D g) {
        g.setStroke(SYMBOL_STROKE);

        var singleLineWidth = getWidth() / (float) COLOR_EXPLANATION_LINE_COUNT;
        final var y = getHeight() - SYMBOL_STROKE.getLineWidth();

        for (int i = 0; i < COLOR_EXPLANATION_LINE_COUNT; i++) {
            var x1 = singleLineWidth * i;
            var x2 = singleLineWidth * (i + 1);

            g.setColor(ColorUtils.interpolateHSB(SYMBOL_COLOR_START, SYMBOL_COLOR_END, i / (COLOR_EXPLANATION_LINE_COUNT - 1f)));
            g.drawLine((int) x1, (int) y, (int) x2, (int) y);
        }
    }

    private void paintColorExplanationText(Graphics2D g) {
        final var textY = getHeight() - g.getFontMetrics().getHeight() / 2;
        final var startText = "Start";
        final var startTextX = g.getFontMetrics().stringWidth(startText) / 2;

        final var endText = "End";
        final var endTextX = (int) (getWidth() - g.getFontMetrics().stringWidth(endText) * 1.5);

        g.setColor(SYMBOL_COLOR_START);
        g.drawString(startText, startTextX, textY);

        g.setColor(SYMBOL_COLOR_END);
        g.drawString(endText, endTextX, textY);
    }

    private int getTotalPointCount() {
        var pointCount = 0;

        for (var part : normalizedPartedCurve) {
            pointCount += part.size();
        }

        return pointCount;
    }

    private List<List<Point>> getScaledPartedCurve() {
        var center = new Point(getWidth() / 2.0, getHeight() / 2.0);
        var scaleFactor = Math.min(getWidth(), getHeight()) * (0.5f - WINDOW_PADDING);

        return normalizedPartedCurve.stream()
                .map(part -> part.stream()
                        .map(point -> point.scale(scaleFactor))
                        .map(point -> point.plus(center))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static class SymbolAnimationWorker extends SwingWorker<Void, List<List<Point>>> {

        private static final double VELOCITY = 0.005;

        private final SymbolView symbolView;
        private final List<List<Point>> partedCurve;

        public SymbolAnimationWorker(SymbolView symbolView, List<List<Point>> partedCurve) {
            super();
            this.symbolView = symbolView;
            this.partedCurve = partedCurve;
        }

        @Override
        protected Void doInBackground() throws InterruptedException {
            final List<List<Point>> partialPartedCurve = new ArrayList<>();

            for (var continuousCurve : partedCurve) {
                partialPartedCurve.add(new ArrayList<>());
                var previousPoint = continuousCurve.get(0);
                partialPartedCurve.get(partialPartedCurve.size() - 1).add(previousPoint);

                for (var i = 1; i < continuousCurve.size(); i++) {
                    var currentPoint = continuousCurve.get(i);
                    var distance = CurveMeter.distanceBetweenPoints(previousPoint, currentPoint);

                    partialPartedCurve.get(partialPartedCurve.size() - 1).add(currentPoint);

                    final var N = 10;
                    final var lastPart = partialPartedCurve.get(partialPartedCurve.size() - 1);

                    for (var j = 0; j < N; j++) {
                        var t = (double) j / (N - 1);
                        var x = (1 - t) * previousPoint.x + t * currentPoint.x;
                        var y = (1 - t) * previousPoint.y + t * currentPoint.y;

                        lastPart.set(lastPart.size() - 1, new Point(x, y));

                        sleep(distance / (VELOCITY * N));
                        publish(partialPartedCurve);
                    }

                    previousPoint = currentPoint;
                }
            }

            return null;
        }

        private void sleep(double timeInMs) throws InterruptedException {
            long millis = (long) timeInMs;
            int nanos = (int) ((timeInMs - (double) millis) * 1_000_000);
            Thread.sleep(millis, nanos);
        }

        @Override
        protected void process(List<List<List<Point>>> chunks) {
            var lastCurve = chunks.get(chunks.size() - 1);
            symbolView.setSymbol(lastCurve);
        }
    }
}
