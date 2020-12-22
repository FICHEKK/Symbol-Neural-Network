package ui.panels;

import network.NeuralNetwork;
import network.holder.NeuralNetworkHolder;
import network.holder.NeuralNetworkChangeListener;
import settings.Settings;
import settings.SettingsListener;
import structures.Point;
import ui.symbolCanvas.SymbolCanvas;
import ui.views.HistogramView;
import util.DatasetLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

public class PredictingPanel extends JPanel implements SettingsListener, NeuralNetworkChangeListener {

    private static final Color PANEL_BACKGROUND_COLOR = new Color(40, 76, 134, 255);
    private static final Color PANEL_TEXT_COLOR = Color.WHITE;
    private static final Font ARIAL = new Font("Arial", Font.BOLD, 16);
    private static final int PADDING = 20;

    private final JLabel predictionLabel = new JLabel("I will write my prediction here!");
    private final HistogramView histogram = new HistogramView();
    private final SymbolCanvas symbolCanvas = new SymbolCanvas();
    private final Settings settings;
    private final NeuralNetworkHolder neuralNetworkHolder;

    public PredictingPanel(Settings settings, NeuralNetworkHolder neuralNetworkHolder) {
        this.settings = settings;
        this.settings.addListener(this);

        this.neuralNetworkHolder = neuralNetworkHolder;
        this.neuralNetworkHolder.addChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(PANEL_BACKGROUND_COLOR);

        symbolCanvas.setShowRepresentativePoints(settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING));
        symbolCanvas.setDrawingEnabled(false);

        symbolCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (symbolCanvas.isDrawingEnabled()) return;
                predictionLabel.setText("I have not been trained yet...");
            }
        });

        symbolCanvas.addSymbolUpdateListener(normalizedPoints -> {
            var network = neuralNetworkHolder.getNeuralNetwork();
            var prediction = network.predict(convertPointsToSample(normalizedPoints));

            var identifiers = DatasetLoader.getIdentifiers(
                    settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                    network.getInputNeuronCount() / 2
            ).toArray(new String[0]);

            histogram.setData(identifiers, prediction);

            if (prediction.length == 1) {
                predictionLabel.setText("It can only be '" + identifiers[0] + "' as it is the only symbol I've been taught!");
            }
            else {
                predictionLabel.setText(stringifyPrediction(prediction, identifiers));
            }
        });

        add(histogram, BorderLayout.NORTH);
        add(symbolCanvas, BorderLayout.CENTER);
        add(predictionLabel, BorderLayout.SOUTH);
        predictionLabel.setFont(ARIAL);
        predictionLabel.setHorizontalAlignment(JLabel.CENTER);
        predictionLabel.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
        predictionLabel.setForeground(PANEL_TEXT_COLOR);
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

    @Override
    public void onPropertyChange(String property) {
        if (property.equals(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING)) {
            symbolCanvas.setShowRepresentativePoints(settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING));
        }
    }

    @Override
    public void onNeuralNetworkChange(NeuralNetwork neuralNetwork) {
        var network = neuralNetworkHolder.getNeuralNetwork();
        symbolCanvas.setDrawingEnabled(network != null);

        if (network != null) {
            var inputPointCount = network.getInputNeuronCount() / 2;
            symbolCanvas.setNumberOfRepresentativePoints(inputPointCount);
        }
    }
}
