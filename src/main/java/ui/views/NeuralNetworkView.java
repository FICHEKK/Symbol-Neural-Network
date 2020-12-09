package ui.views;

import network.NeuralNetwork;
import network.NeuralNetworkListener;
import org.apache.commons.math3.linear.RealMatrix;

import javax.swing.*;
import java.awt.*;

public class NeuralNetworkView extends JComponent implements NeuralNetworkListener {

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color NEURON_COLOR = Color.WHITE;

    private static final float POSITIVE_WEIGHT_R = 0;
    private static final float POSITIVE_WEIGHT_G = 0.8f;
    private static final float POSITIVE_WEIGHT_B = 0.2f;

    private static final float NEGATIVE_WEIGHT_R = 1;
    private static final float NEGATIVE_WEIGHT_G = 0;
    private static final float NEGATIVE_WEIGHT_B = 0;

    private static final int NEURON_RADIUS = 3;
    private static final int NEURON_DIAMETER = 2 * NEURON_RADIUS;
    private static final int WIDTH = 800;
    private static final int PADDING = 10;

    private NeuralNetwork neuralNetwork;
    private DrawingMode drawingMode = DrawingMode.DRAW_ALL_WEIGHTS;

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        if (this.neuralNetwork != null) {
            this.neuralNetwork.removeListener(this);
        }

        this.neuralNetwork = neuralNetwork;

        if (this.neuralNetwork != null) {
            this.neuralNetwork.addListener(this);
        }
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        if (this.drawingMode == drawingMode) return;
        this.drawingMode = drawingMode;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (neuralNetwork == null) return;
        paintWeights(g);
        paintNeurons(g);
    }

    private void paintWeights(Graphics g) {
        RealMatrix[] weights = neuralNetwork.getWeights();
        int[] layers = neuralNetwork.getLayers();
        int width = getWidthWithPadding();
        int height = getHeight();
        int neuronLayerSpacingHorizontal = width / (layers.length - 1);

        double max = findAbsoluteMax(weights);

        for (int layer = 0; layer < weights.length; layer++) {
            var W = weights[layer];
            int rows = W.getRowDimension();
            int cols = W.getColumnDimension();

            int neuronLayerSpacingVertical1 = Math.round((float) height / (layers[layer] + 1));
            int neuronLayerSpacingVertical2 = Math.round((float) height / (layers[layer + 1] + 1));

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    var weight = W.getEntry(row, col) / max;
                    if (!shouldDrawWeight(weight)) continue;

                    if (weight < 0) {
                        g.setColor(new Color(NEGATIVE_WEIGHT_R, NEGATIVE_WEIGHT_G, NEGATIVE_WEIGHT_B, (float) Math.abs(weight)));
                    }
                    else {
                        g.setColor(new Color(POSITIVE_WEIGHT_R, POSITIVE_WEIGHT_G, POSITIVE_WEIGHT_B, (float) Math.abs(weight)));
                    }

                    var x1 = neuronLayerSpacingHorizontal * layer + PADDING;
                    var x2 = neuronLayerSpacingHorizontal * (layer + 1) + PADDING;
                    var y1 = neuronLayerSpacingVertical1 * (col + 1);
                    var y2 = neuronLayerSpacingVertical2 * (row + 1);

                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    private boolean shouldDrawWeight(double weight) {
        switch (drawingMode) {
            case DRAW_POSITIVE_WEIGHTS_ONLY:
                return weight >= 0;
            case DRAW_NEGATIVE_WEIGHTS_ONLY:
                return weight < 0;
            case DRAW_ALL_WEIGHTS:
                return true;
            default:
                throw new IllegalStateException("Invalid drawing mode.");
        }
    }

    private static double findAbsoluteMax(RealMatrix[] matrices) {
        var max = Double.NEGATIVE_INFINITY;

        for (RealMatrix matrix : matrices) {
            var matrixMax = findAbsoluteMax(matrix);

            if (matrixMax > max) {
                max = matrixMax;
            }
        }

        return max;
    }

    private static double findAbsoluteMax(RealMatrix matrix) {
        var max = Double.NEGATIVE_INFINITY;

        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                var entry = Math.abs(matrix.getEntry(row, col));

                if (entry > max) {
                    max = entry;
                }
            }
        }

        return max;
    }

    private void paintNeurons(Graphics g) {
        g.setColor(NEURON_COLOR);

        int[] layers = neuralNetwork.getLayers();
        int width = getWidthWithPadding();
        int height = getHeight();
        int neuronLayerSpacingHorizontal = width / (layers.length - 1);

        for (int i = 0; i < layers.length; i++) {
            int x = neuronLayerSpacingHorizontal * i + PADDING;
            int neuronLayerSpacingVertical = Math.round((float) height / (layers[i] + 1));

            for (int j = 1; j <= layers[i]; j++) {
                int y = neuronLayerSpacingVertical * j;
                g.drawOval(x - NEURON_RADIUS, y - NEURON_RADIUS, NEURON_DIAMETER, NEURON_DIAMETER);
            }
        }
    }

    private int getWidthWithPadding() {
        return getWidth() - 2 * PADDING;
    }

    @Override
    public void onFitFinish() {
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, 0);
    }

    public enum DrawingMode {
        DRAW_ALL_WEIGHTS("Draw all weights"),
        DRAW_POSITIVE_WEIGHTS_ONLY("Draw positive weights only"),
        DRAW_NEGATIVE_WEIGHTS_ONLY("Draw negative weights only");

        private final String name;

        DrawingMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
