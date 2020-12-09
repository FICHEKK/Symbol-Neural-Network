package ui.views;

import javax.swing.*;
import java.awt.*;

public class HistogramView extends JComponent {

    private static final int HEIGHT = 64;
    private static final Color BAR_COLOR = new Color(184, 76, 0, 255);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private String[] identifiers;
    private double[] prediction;

    public void setData(String[] identifiers, double[] prediction) {
        if (identifiers.length != prediction.length)
            throw new IllegalArgumentException("Number of identifiers must match number of predictions.");

        this.identifiers = identifiers;
        this.prediction = prediction;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (identifiers == null && prediction == null) return;

        var barWidth = getWidth() / identifiers.length;

        for (int i = 0; i < identifiers.length; i++) {
            paintPredictionBar(g, i, barWidth);
            paintIdentifierText(g, i, barWidth);
        }
    }

    private void paintPredictionBar(Graphics g, int index, int barWidth) {
        g.setColor(BAR_COLOR);
        int startX = index * barWidth;
        var barHeight = (int) (prediction[index] * HEIGHT);
        g.fillRect(startX, HEIGHT - barHeight, barWidth, barHeight);
    }

    private void paintIdentifierText(Graphics g, int index, int barWidth) {
        g.setColor(TEXT_COLOR);
        int startX = index * barWidth;

        int identifierWidth = g.getFontMetrics().stringWidth(identifiers[index]);
        g.drawString(identifiers[index], startX + barWidth / 2 - identifierWidth / 2, HEIGHT / 2);

        var predictionString = String.format("%.2f", prediction[index]);
        var predictionWidth = g.getFontMetrics().stringWidth(predictionString);
        var predictionHeight = g.getFontMetrics().getHeight();
        g.drawString(predictionString, startX + barWidth / 2 - predictionWidth / 2, HEIGHT / 2 + predictionHeight);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, HEIGHT);
    }
}
