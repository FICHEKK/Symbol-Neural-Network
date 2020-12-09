package ui.panels;

import settings.DataCollectingStageSettings;
import settings.LearningStageSettings;
import structures.Point;
import ui.Histogram;
import ui.SymbolCanvas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PredictingPanel extends JPanel {

    private static final Font ARIAL = new Font("Arial", Font.BOLD, 16);
    private static final int PADDING = 20;
    private final JLabel predictionLabel = new JLabel("I will write my prediction here!");
    private final Histogram histogram = new Histogram();

    public PredictingPanel(DataCollectingStageSettings dataCollectingSettings, LearningStageSettings learningSettings) {
        setLayout(new BorderLayout());

        SymbolCanvas symbolCanvas = new SymbolCanvas(dataCollectingSettings);

        symbolCanvas.addListener(points -> {
            var neuralNetwork = learningSettings.getNeuralNetwork();

            if (neuralNetwork == null) {
                predictionLabel.setText("I have not been trained yet...");
                return;
            }

            var prediction = neuralNetwork.predict(convertPointsToSample(points));
            var identifiers = learningSettings.getDataset().identifiers;
            histogram.setData(identifiers, prediction);
            predictionLabel.setText(stringifyPrediction(prediction, identifiers));
        });

        add(histogram, BorderLayout.NORTH);
        add(symbolCanvas, BorderLayout.CENTER);
        add(predictionLabel, BorderLayout.SOUTH);
        predictionLabel.setFont(ARIAL);
        predictionLabel.setHorizontalAlignment(JLabel.CENTER);
        predictionLabel.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
    }

    private static double[] convertPointsToSample(List<Point> points) {
        double[] sample = new double[2 * points.size()];

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            sample[i * 2] = point.x;
            sample[i * 2 + 1] = point.y;
        }

        return sample;
    }

    private static String stringifyPrediction(double[] prediction, String[] identifiers) {
        double certainty = getCertainty(prediction);
        String identifier = identifiers[findIndexOfMax(prediction)];

        if (certainty > 0.95) {
            return "There is no way this is not '" + identifier + "'!";
        }
        else if (certainty > 0.9) {
            return "This must be '" + identifier + "'!";
        }
        else if(certainty > 0.8) {
            return "I am pretty sure I see '" + identifier + "'!";
        }
        else if(certainty > 0.7) {
            return "Looks like '" + identifier + "' to me!";
        }
        else if(certainty > 0.6) {
            return "This is probably '" + identifier + "', but I could be wrong.";
        }
        else if(certainty > 0.5) {
            return "Not sure, but I think I see '" + identifier + "'.";
        }
        else {
            return "I am confused, but my guess is '" + identifier + "'...";
        }
    }

    private static int findIndexOfMax(double[] prediction) {
        int maxAt = 0;

        for (int i = 1; i < prediction.length; i++) {
            maxAt = prediction[i] > prediction[maxAt] ? i : maxAt;
        }

        return maxAt;
    }

    private static double getCertainty(double[] prediction) {
        var copy = Arrays.copyOf(prediction, prediction.length);
        Arrays.sort(copy);
        return copy[copy.length - 1] - copy[copy.length - 2];
    }
}
