package ui.panels;

import settings.DataCollectingStageSettings;
import settings.LearningStageSettings;
import structures.Point;
import ui.SymbolCanvas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PredictingPanel extends JPanel {

    private static final Font ARIAL = new Font("Arial", Font.BOLD, 16);
    private static final int PADDING = 20;
    private final JLabel predictionLabel = new JLabel("I will write my prediction here!");

    public PredictingPanel(DataCollectingStageSettings dataCollectingSettings, LearningStageSettings learningSettings) {
        setLayout(new BorderLayout());

        SymbolCanvas symbolCanvas = new SymbolCanvas(dataCollectingSettings);

        symbolCanvas.addListener(points -> {
            double[] sample = new double[2 * points.size()];

            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                sample[i * 2] = point.x;
                sample[i * 2 + 1] = point.y;
            }

            var neuralNetwork = learningSettings.getNeuralNetwork();
            if (neuralNetwork == null) {
                predictionLabel.setText("I have not been trained yet...");
                return;
            }

            var prediction = neuralNetwork.predict(sample);
            var identifiers = learningSettings.getDataset().identifiers;

            System.out.println("=".repeat(30));

            for (int i = 0; i < prediction.length; i++) {
                System.out.println(identifiers[i] + ": " + String.format("%.2f", prediction[i] * 100));
            }

            int maxAt = 0;
            for (int i = 1; i < prediction.length; i++) {
                maxAt = prediction[i] > prediction[maxAt] ? i : maxAt;
            }

            predictionLabel.setText("I see symbol '" + learningSettings.getDataset().identifiers[maxAt] + "'. " +
                    "I am " + String.format("%.2f", prediction[maxAt] * 100) + "% certain.");
        });

        add(symbolCanvas, BorderLayout.CENTER);
        add(predictionLabel, BorderLayout.SOUTH);
        predictionLabel.setFont(ARIAL);
        predictionLabel.setHorizontalAlignment(JLabel.CENTER);
        predictionLabel.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
    }
}
