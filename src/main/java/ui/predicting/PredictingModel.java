package ui.predicting;

import network.NeuralNetwork;
import network.holder.NeuralNetworkChangeListener;
import network.holder.NeuralNetworkHolder;
import settings.Settings;
import settings.SettingsListener;
import structures.Point;
import ui.ModelListener;
import ui.symbolCanvas.SymbolCanvasFinishListener;
import ui.symbolCanvas.SymbolCanvasUpdateListener;
import util.DatasetLoader;

import java.util.Arrays;
import java.util.List;

public class PredictingModel implements SettingsListener, NeuralNetworkChangeListener, SymbolCanvasUpdateListener, SymbolCanvasFinishListener {

    private final Settings settings;
    private final NeuralNetworkHolder neuralNetworkHolder;

    private ModelListener<PredictingState> listener;

    private int numberOfRepresentativePoints;
    private boolean isDrawingEnabled;

    public PredictingModel(Settings settings, NeuralNetworkHolder neuralNetworkHolder) {
        this.settings = settings;
        this.settings.addListener(this);

        this.neuralNetworkHolder = neuralNetworkHolder;
        this.neuralNetworkHolder.addChangeListener(this);
    }

    public void setListener(ModelListener<PredictingState> listener) {
        this.listener = listener;
    }

    @Override
    public void onPropertyChange(String property) {
        if (!property.equals(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING)) return;

        if (listener == null) return;
        listener.onNextState(new PredictingState.SymbolCanvas(
                numberOfRepresentativePoints,
                settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING),
                isDrawingEnabled
        ));
    }

    @Override
    public void onNeuralNetworkChange(NeuralNetwork neuralNetwork) {
        numberOfRepresentativePoints = neuralNetwork != null ? neuralNetwork.getInputNeuronCount() / 2 : -1;
        isDrawingEnabled = neuralNetwork != null;

        if (listener == null) return;
        listener.onNextState(new PredictingState.SymbolCanvas(
                numberOfRepresentativePoints,
                settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING),
                isDrawingEnabled
        ));
    }

    @Override
    public void onNextSymbolUpdate(List<Point> normalizedPoints) {
        if (!settings.getBooleanProperty(Settings.UPDATE_HISTOGRAM_WHILE_DRAWING)) return;
        updateHistogram(normalizedPoints);
    }

    @Override
    public void onNextSymbolFinish(List<Point> normalizedPoints) {
        updateHistogram(normalizedPoints);
    }

    private void updateHistogram(List<Point> normalizedPoints) {
        var network = neuralNetworkHolder.getNeuralNetwork();
        var sample = convertPointsToSample(normalizedPoints);
        var prediction = network.predict(sample);

        var identifiers = DatasetLoader.getIdentifiers(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                network.getInputNeuronCount() / 2
        ).toArray(new String[0]);

        listener.onNextState(new PredictingState.Histogram(identifiers, prediction));
        listener.onNextState(new PredictingState.Message(prediction.length == 1 ?
                "It can only be '" + identifiers[0] + "' as it is the only symbol I've been taught!" :
                stringifyPrediction(prediction, identifiers)
        ));
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

    private static double getCertainty(double[] prediction) {
        var copy = Arrays.copyOf(prediction, prediction.length);
        Arrays.sort(copy);
        return copy[copy.length - 1] - copy[copy.length - 2];
    }

    private static int findIndexOfMax(double[] prediction) {
        int maxAt = 0;

        for (int i = 1; i < prediction.length; i++) {
            maxAt = prediction[i] > prediction[maxAt] ? i : maxAt;
        }

        return maxAt;
    }
}
