package ui.settings;

import settings.Settings;
import ui.settings.state.*;
import util.UserInputValidator;

public class SettingsPanelModel {

    public static final int MIN_REPRESENTATIVE_POINTS = 2;
    public static final int MAX_REPRESENTATIVE_POINTS = 100;

    private String numberOfRepresentativePoints;
    private String symbolSaveDirectory;
    private boolean showRepresentativePointsWhileDataCollecting;
    private String symbolLoadDirectory;
    private boolean useRandomWeightColors;
    private boolean showRepresentativePointsWhilePredicting;
    private boolean updateHistogramWhileDrawing;

    private final Settings settings;
    private SettingsPanelModelListener listener;

    public SettingsPanelModel(Settings settings) {
        this.settings = settings;

        numberOfRepresentativePoints = settings.getStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS);
        symbolSaveDirectory = settings.getStringProperty(Settings.SYMBOL_SAVE_DIRECTORY);
        showRepresentativePointsWhileDataCollecting = settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING);
        symbolLoadDirectory = settings.getStringProperty(Settings.SYMBOL_LOAD_DIRECTORY);
        useRandomWeightColors = settings.getBooleanProperty(Settings.USE_RANDOM_WEIGHT_COLORS);
        showRepresentativePointsWhilePredicting = settings.getBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING);
        updateHistogramWhileDrawing = settings.getBooleanProperty(Settings.UPDATE_HISTOGRAM_WHILE_DRAWING);
    }

    public void setListener(SettingsPanelModelListener listener) {
        this.listener = listener;
    }

    private void notifyDataCollectingSectionChanged() {
        listener.onNextState(new SettingsPanelDataCollectingState(isNumberOfRepresentativePointsValid(), isSymbolSaveDirectoryValid()));
    }

    private void notifyTrainingSectionChanged() {
        listener.onNextState(new SettingsPanelTrainingState(isSymbolLoadDirectoryValid()));
    }

    // =============================================================================================
    //                                       Getters
    // =============================================================================================

    public String getNumberOfRepresentativePoints() {
        return numberOfRepresentativePoints;
    }

    public String getSymbolSaveDirectory() {
        return symbolSaveDirectory;
    }

    public boolean isShowRepresentativePointsWhileDataCollecting() {
        return showRepresentativePointsWhileDataCollecting;
    }

    public String getSymbolLoadDirectory() {
        return symbolLoadDirectory;
    }

    public boolean isUseRandomWeightColors() {
        return useRandomWeightColors;
    }

    public boolean isShowRepresentativePointsWhilePredicting() {
        return showRepresentativePointsWhilePredicting;
    }

    public boolean isUpdateHistogramWhileDrawing() {
        return updateHistogramWhileDrawing;
    }

    // =============================================================================================
    //                                       Setters
    // =============================================================================================

    public void setNumberOfRepresentativePoints(String numberOfRepresentativePoints) {
        this.numberOfRepresentativePoints = numberOfRepresentativePoints;

        if (isNumberOfRepresentativePointsValid()) {
            settings.setStringProperty(Settings.NUMBER_OF_REPRESENTATIVE_POINTS, numberOfRepresentativePoints);
        }

        notifyDataCollectingSectionChanged();
    }

    public void setSymbolSaveDirectory(String symbolSaveDirectory) {
        this.symbolSaveDirectory = symbolSaveDirectory;

        if (isSymbolSaveDirectoryValid()) {
            settings.setStringProperty(Settings.SYMBOL_SAVE_DIRECTORY, symbolSaveDirectory);
        }

        notifyDataCollectingSectionChanged();
    }

    public void setShowRepresentativePointsWhileDataCollecting(boolean showRepresentativePointsWhileDataCollecting) {
        this.showRepresentativePointsWhileDataCollecting = showRepresentativePointsWhileDataCollecting;
        settings.setBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING, showRepresentativePointsWhileDataCollecting);
    }

    public void setSymbolLoadDirectory(String symbolLoadDirectory) {
        this.symbolLoadDirectory = symbolLoadDirectory;

        if (isSymbolLoadDirectoryValid()) {
            settings.setStringProperty(Settings.SYMBOL_LOAD_DIRECTORY, symbolLoadDirectory);
        }

        notifyTrainingSectionChanged();
    }

    public void setUseRandomWeightColors(boolean useRandomWeightColors) {
        this.useRandomWeightColors = useRandomWeightColors;
        settings.setBooleanProperty(Settings.USE_RANDOM_WEIGHT_COLORS, useRandomWeightColors);
    }

    public void setShowRepresentativePointsWhilePredicting(boolean showRepresentativePointsWhilePredicting) {
        this.showRepresentativePointsWhilePredicting = showRepresentativePointsWhilePredicting;
        settings.setBooleanProperty(Settings.SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING, showRepresentativePointsWhilePredicting);
    }

    public void setUpdateHistogramWhileDrawing(boolean updateHistogramWhileDrawing) {
        this.updateHistogramWhileDrawing = updateHistogramWhileDrawing;
        settings.setBooleanProperty(Settings.UPDATE_HISTOGRAM_WHILE_DRAWING, updateHistogramWhileDrawing);
    }

    private boolean isNumberOfRepresentativePointsValid() {
        return UserInputValidator.assertIntegerInRange(numberOfRepresentativePoints, MIN_REPRESENTATIVE_POINTS, MAX_REPRESENTATIVE_POINTS);
    }

    private boolean isSymbolSaveDirectoryValid() {
        return !symbolSaveDirectory.isBlank();
    }

    private boolean isSymbolLoadDirectoryValid() {
        return !symbolLoadDirectory.isBlank();
    }
}
