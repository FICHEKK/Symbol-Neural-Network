package settings;

public interface Settings {

    // Data collecting
    String SYMBOL_IDENTIFIER = "SYMBOL_IDENTIFIER";

    // Training
    String TRAINING_METHOD = "TRAINING_METHOD";
    String MINI_BATCH_SIZE = "MINI_BATCH_SIZE";
    String HIDDEN_LAYERS_DEFINITION = "HIDDEN_LAYERS_DEFINITION";
    String LEARNING_RATE = "LEARNING_RATE";
    String MINIMUM_ACCEPTABLE_ERROR = "MINIMUM_ACCEPTABLE_ERROR";
    String MAXIMUM_NUMBER_OF_ITERATIONS = "MAXIMUM_NUMBER_OF_ITERATIONS";
    String ADDITIONAL_PERMUTATIONS_PER_SAMPLE = "ADDITIONAL_PERMUTATIONS_PER_SAMPLE";
    String WEIGHTS_DRAWING_MODE = "WEIGHTS_DRAWING_MODE";

    // Settings
    String NUMBER_OF_REPRESENTATIVE_POINTS = "NUMBER_OF_REPRESENTATIVE_POINTS";
    String SYMBOL_SAVE_DIRECTORY = "SYMBOL_SAVE_DIRECTORY";
    String SYMBOL_LOAD_DIRECTORY = "SYMBOL_LOAD_DIRECTORY";
    String SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING = "SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING";
    String SHOW_CONTINUOUS_CURVE_INDEX_IN_SYMBOL_VIEW = "SHOW_CONTINUOUS_CURVE_INDEX_IN_SYMBOL_VIEW";
    String SHOW_REPRESENTATIVE_POINTS_IN_SYMBOL_VIEW = "SHOW_REPRESENTATIVE_POINTS_IN_SYMBOL_VIEW";
    String ANIMATE_SYMBOL_IN_SYMBOL_VIEW = "ANIMATE_SYMBOL_IN_SYMBOL_VIEW";
    String UPDATE_HISTOGRAM_WHILE_DRAWING = "UPDATE_HISTOGRAM_WHILE_DRAWING";
    String SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING = "SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING";
    String USE_RANDOM_WEIGHT_COLORS = "USE_RANDOM_WEIGHT_COLORS";

    String getStringProperty(String property);
    int getIntProperty(String property);
    double getDoubleProperty(String property);
    boolean getBooleanProperty(String property);

    void setStringProperty(String property, String value);
    void setIntProperty(String property, int value);
    void setDoubleProperty(String property, double value);
    void setBooleanProperty(String property, boolean value);

    void addListener(SettingsListener listener);
    void removeListener(SettingsListener listener);

    void save();
}
