package ui.views;

import math.Matrix;
import math.Vector;
import network.NeuralNetwork;
import network.NeuralNetworkFitFinishListener;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NeuralNetworkView extends JComponent implements NeuralNetworkFitFinishListener {

    private static final Random RANDOM = new Random();

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color INPUT_NEURON_COLOR = Color.BLACK;

    private static final float POSITIVE_WEIGHT_R = 0.156f;
    private static final float POSITIVE_WEIGHT_G = 0.298f;
    private static final float POSITIVE_WEIGHT_B = 0.524f;

    private static final float NEGATIVE_WEIGHT_R = 1;
    private static final float NEGATIVE_WEIGHT_G = 0;
    private static final float NEGATIVE_WEIGHT_B = 0;

    private static final int INPUT_NEURON_RADIUS = 3;
    private static final int BIAS_NEURON_RADIUS = 5;
    private static final int WIDTH = 800;
    private static final int PADDING = 10;

    private static final Stroke NEURON_STROKE = new BasicStroke(0f);
    private static final Stroke WEIGHT_STROKE = new BasicStroke(0f);

    private NeuralNetwork neuralNetwork;
    private WeightsDrawingMode drawingMode = WeightsDrawingMode.DRAW_ALL;

    private boolean useRandomColors;

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        if (this.neuralNetwork == neuralNetwork) return;

        if (this.neuralNetwork != null) {
            this.neuralNetwork.removeFitFinishListener(this);
        }

        this.neuralNetwork = neuralNetwork;

        if (this.neuralNetwork != null) {
            this.neuralNetwork.addFitFinishListener(this);
        }
    }

    public void setDrawingMode(WeightsDrawingMode drawingMode) {
        if (this.drawingMode == drawingMode) return;
        this.drawingMode = drawingMode;
        repaint();
    }

    public void setUseRandomColors(boolean useRandomColors) {
        this.useRandomColors = useRandomColors;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (neuralNetwork == null) return;
        paintWeights((Graphics2D) g);
        paintNeurons((Graphics2D) g);
    }

    private void paintWeights(Graphics2D g) {
        g.setStroke(WEIGHT_STROKE);
        Matrix[] weights = neuralNetwork.getWeights();
        int[] layers = neuralNetwork.getLayers();
        int width = getWidthWithPadding();
        int height = getHeight();
        int neuronLayerSpacingHorizontal = width / (layers.length - 1);

        double max = findAbsoluteMax(weights);

        for (int layer = 0; layer < weights.length; layer++) {
            var W = weights[layer];
            int rows = W.getRows();
            int cols = W.getColumns();

            int neuronLayerSpacingVertical1 = Math.round((float) height / (layers[layer] + 1));
            int neuronLayerSpacingVertical2 = Math.round((float) height / (layers[layer + 1] + 1));

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    var weight = W.get(row, col) / max;
                    if (!shouldDrawWeight(weight)) continue;

                    g.setColor(getColorForWeight(weight));

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
            case DRAW_POSITIVE:
                return weight >= 0;
            case DRAW_NEGATIVE:
                return weight < 0;
            case DRAW_ALL:
                return true;
            default:
                throw new IllegalStateException("Invalid drawing mode.");
        }
    }

    private static double findAbsoluteMax(Matrix[] matrices) {
        var max = Double.NEGATIVE_INFINITY;

        for (var matrix : matrices) {
            var matrixMax = findAbsoluteMax(matrix);

            if (matrixMax > max) {
                max = matrixMax;
            }
        }

        return max;
    }

    private static double findAbsoluteMax(Matrix matrix) {
        var max = Double.NEGATIVE_INFINITY;

        int rows = matrix.getRows();
        int cols = matrix.getColumns();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                var entry = Math.abs(matrix.get(row, col));

                if (entry > max) {
                    max = entry;
                }
            }
        }

        return max;
    }

    private void paintNeurons(Graphics2D g) {
        int[] layers = neuralNetwork.getLayers();
        int width = getWidthWithPadding();
        int height = getHeight();
        int neuronLayerSpacingHorizontal = width / (layers.length - 1);

        g.setColor(INPUT_NEURON_COLOR);
        g.setStroke(NEURON_STROKE);
        for (int i = 1; i <= layers[0]; i++) {
            int neuronLayerSpacingVertical = Math.round((float) height / (layers[0] + 1));
            int y = neuronLayerSpacingVertical * i;

            final int diameter = 2 * INPUT_NEURON_RADIUS;
            g.drawOval(PADDING - INPUT_NEURON_RADIUS, y - INPUT_NEURON_RADIUS, diameter, diameter);
        }

        var biases = neuralNetwork.getBiases();
        var max = findAbsoluteMax(biases);

        for (int i = 1; i < layers.length; i++) {
            int x = neuronLayerSpacingHorizontal * i + PADDING;
            int neuronLayerSpacingVertical = Math.round((float) height / (layers[i] + 1));

            for (int j = 0; j < layers[i]; j++) {
                int y = neuronLayerSpacingVertical * (j + 1);
                var bias = biases[i - 1].get(j) / max;
                if (!shouldDrawWeight(bias)) continue;

                g.setColor(getColorForWeight(bias));

                final int diameter = 2 * BIAS_NEURON_RADIUS;
                g.fillOval(x - BIAS_NEURON_RADIUS, y - BIAS_NEURON_RADIUS, diameter, diameter);
                g.setColor(INPUT_NEURON_COLOR);
                g.drawOval(x - BIAS_NEURON_RADIUS, y - BIAS_NEURON_RADIUS, diameter, diameter);
            }
        }
    }

    private Color getColorForWeight(double weight) {
        if (useRandomColors) {
            return new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), (float) Math.abs(weight));
        }
        else if (weight < 0) {
            return new Color(NEGATIVE_WEIGHT_R, NEGATIVE_WEIGHT_G, NEGATIVE_WEIGHT_B, (float) Math.abs(weight));
        }
        else {
            return new Color(POSITIVE_WEIGHT_R, POSITIVE_WEIGHT_G, POSITIVE_WEIGHT_B, (float) Math.abs(weight));
        }
    }

    private static double findAbsoluteMax(Vector[] vectors) {
        var max = Double.NEGATIVE_INFINITY;

        for (var vector : vectors) {
            var vectorMax = findAbsoluteMax(vector);

            if (vectorMax > max) {
                max = vectorMax;
            }
        }

        return max;
    }

    private static double findAbsoluteMax(Vector vector) {
        var max = Double.NEGATIVE_INFINITY;

        for (int i = 0, size = vector.size(); i < size; i++) {
            var entry = Math.abs(vector.get(i));

            if (entry > max) {
                max = entry;
            }
        }

        return max;
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

    public enum WeightsDrawingMode {
        DRAW_ALL("All"),
        DRAW_POSITIVE("Positive"),
        DRAW_NEGATIVE("Negative");

        private final String name;

        WeightsDrawingMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static WeightsDrawingMode from(String name) {
            for (var method : values()) {
                if (method.toString().equals(name)) {
                    return method;
                }
            }

            throw new IllegalArgumentException("Could not convert '" + name + "' to a specific weights drawing mode.");
        }
    }
}
