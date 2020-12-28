package ui.panels.dataCollecting;

import settings.Settings;
import settings.SettingsListener;
import structures.Point;
import ui.panels.ModelListener;
import ui.symbolCanvas.SymbolCanvasFinishListener;
import util.CurveConverter;
import util.DatasetLoader;
import util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class DataCollectingModel implements SettingsListener, SymbolCanvasFinishListener {

    private final Settings settings;
    private ModelListener<DataCollectingState> listener;

    private String symbolIdentifier;
    private String selectedSymbolIdentifier;

    public DataCollectingModel(Settings settings) {
        this.settings = settings;
        this.settings.addListener(this);

        symbolIdentifier = settings.getStringProperty(Settings.SYMBOL_IDENTIFIER);
    }

    public void setListener(ModelListener<DataCollectingState> listener) {
        this.listener = listener;
        notifyAllSymbolsTableChanged();
        notifySymbolIdentifierChanged();
    }

    public void deleteIdentifiers(List<String> identifiers) throws IOException {
        var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
        var numberOfPoints = settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);

        for (var identifier : identifiers) {
            var identifierDirectory = Paths.get(loadDirectory, numberOfPoints, identifier);
            FileUtils.deleteDirectory(identifierDirectory);
        }

        notifyAllSymbolsTableChanged();
        notifySingleSymbolTableChanged(null, null);
        notifySymbolViewPartedCurveChanged(null);
    }

    public void deleteSamples(List<String> samples) throws IOException {
        var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
        var numberOfPoints = settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
        var identifierDirectory = Paths.get(loadDirectory, numberOfPoints, selectedSymbolIdentifier);

        for (var sample : samples) {
            Files.delete(identifierDirectory.resolve(sample));
        }

        notifyAllSymbolsTableChanged();
        updateSingleSymbolTable();
        notifySymbolViewPartedCurveChanged(null);
    }

    // =============================================================================================
    //                                       Getters
    // =============================================================================================

    public String getSymbolIdentifier() {
        return symbolIdentifier;
    }

    public int getNumberOfRepresentativePoints() {
        return settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
    }

    public boolean getShowRepresentativePointsWhileDataCollecting() {
        return settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING);
    }

    public boolean isDrawingEnabled() {
        return !symbolIdentifier.isBlank();
    }

    // =============================================================================================
    //                                       Setters
    // =============================================================================================

    public void setSymbolIdentifier(String symbolIdentifier) {
        this.symbolIdentifier = symbolIdentifier;
        settings.setStringProperty(Settings.SYMBOL_IDENTIFIER, symbolIdentifier);
        notifySymbolIdentifierChanged();
    }

    public void setSelectedSymbolIdentifier(String selectedSymbolIdentifier) {
        this.selectedSymbolIdentifier = selectedSymbolIdentifier;
        updateSingleSymbolTable();
    }

    public void setSelectedSample(String selectedSample) {
        try {
            var loadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
            var numberOfRepresentativePoints = settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
            var path = Paths.get(loadDirectory, String.valueOf(numberOfRepresentativePoints), selectedSymbolIdentifier, selectedSample);
            notifySymbolViewPartedCurveChanged(CurveConverter.deserializePartedCurve(Files.readAllLines(path)));
        } catch (IOException exception) {
            exception.printStackTrace();
            notifySymbolViewPartedCurveChanged(null);
        }
    }

    private void updateSingleSymbolTable() {
        var samples = DatasetLoader.getSampleToPartCount(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS),
                selectedSymbolIdentifier
        );

        notifySingleSymbolTableChanged(selectedSymbolIdentifier, samples);
    }

    @Override
    public void onNextSymbolFinish(List<List<Point>> partedCurve) {
        var symbolIdentifier = settings.getStringProperty(Settings.SYMBOL_IDENTIFIER);

        if (symbolIdentifier.equals(selectedSymbolIdentifier)) {
            updateSingleSymbolTable();
        }

        notifyAllSymbolsTableChanged();
    }

    @Override
    public void onPropertyChange(String property) {
        switch (property) {
            case Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING:
                notifySymbolCanvasChanged();
                break;

            case Settings.NUMBER_OF_REPRESENTATIVE_POINTS:
                notifySymbolCanvasChanged();
                notifyAllSymbolsTableChanged();
                notifySingleSymbolTableChanged(null, null);
                notifySymbolViewPartedCurveChanged(null);
                break;

            case Settings.SYMBOL_LOAD_DIRECTORY:
                notifyAllSymbolsTableChanged();
                notifySingleSymbolTableChanged(null, null);
                notifySymbolViewPartedCurveChanged(null);
                break;

            case Settings.SHOW_CONTINUOUS_CURVE_INDEX_IN_SYMBOL_VIEW:
                listener.onNextState(new DataCollectingState.SymbolViewShowContinuousCurveIndex(
                        settings.getBooleanProperty(Settings.SHOW_CONTINUOUS_CURVE_INDEX_IN_SYMBOL_VIEW)
                ));
                break;

            case Settings.SHOW_REPRESENTATIVE_POINTS_IN_SYMBOL_VIEW:
                listener.onNextState(new DataCollectingState.SymbolViewShowRepresentativePoints(
                        settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_IN_SYMBOL_VIEW)
                ));
                break;
        }
    }

    private void notifySymbolCanvasChanged() {
        listener.onNextState(new DataCollectingState.SymbolCanvas(
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS),
                settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING),
                isSymbolIdentifierValid()
        ));
    }

    private void notifyAllSymbolsTableChanged() {
        var symbolToSampleCount = DatasetLoader.getSymbolToSampleCount(
                settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY),
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)
        );

        var totalSampleCount = 0;
        for (var sampleCount : symbolToSampleCount.values()) {
            totalSampleCount += sampleCount;
        }

        listener.onNextState(new DataCollectingState.AllSymbolsTable(
                symbolToSampleCount,
                totalSampleCount,
                settings.getIntProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS)
        ));
    }

    private void notifySingleSymbolTableChanged(String identifier, Map<String, Integer> sampleToPartCount) {
        listener.onNextState(new DataCollectingState.SingleSymbolTable(identifier, sampleToPartCount));
    }

    private void notifySymbolViewPartedCurveChanged(List<List<Point>> partedCurve) {
        listener.onNextState(new DataCollectingState.SymbolViewPartedCurve(partedCurve));
    }

    private void notifySymbolIdentifierChanged() {
        listener.onNextState(new DataCollectingState.SymbolIdentifier(isSymbolIdentifierValid()));
    }

    private boolean isSymbolIdentifierValid() {
        return !symbolIdentifier.isBlank();
    }
}
