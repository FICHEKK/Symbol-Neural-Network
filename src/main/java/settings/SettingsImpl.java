package settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingsImpl implements Settings {

    private static final Map<String, String> DEFAULT_PROPERTY_MAP = new LinkedHashMap<>();
    private static final Path SETTINGS_FILE_PATH = Paths.get("res/settings.txt");
    private static final String COMMENT_INDICATOR = "#";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> propertyMap = new LinkedHashMap<>();
    private final List<SettingsListener> listeners = new ArrayList<>();

    static {
        DEFAULT_PROPERTY_MAP.put(SYMBOL_IDENTIFIER, "alpha");

        DEFAULT_PROPERTY_MAP.put(TRAINING_METHOD, "Mini-batch");
        DEFAULT_PROPERTY_MAP.put(MINI_BATCH_SIZE, "5");
        DEFAULT_PROPERTY_MAP.put(HIDDEN_LAYERS_DEFINITION, "10x8");
        DEFAULT_PROPERTY_MAP.put(LEARNING_RATE, "0.05");
        DEFAULT_PROPERTY_MAP.put(MINIMUM_ACCEPTABLE_ERROR, "0.001");
        DEFAULT_PROPERTY_MAP.put(MAXIMUM_NUMBER_OF_ITERATIONS, "10000");
        DEFAULT_PROPERTY_MAP.put(ADDITIONAL_PERMUTATIONS_PER_SAMPLE, "0");
        DEFAULT_PROPERTY_MAP.put(WEIGHTS_DRAWING_MODE, "All");

        DEFAULT_PROPERTY_MAP.put(NUMBER_OF_REPRESENTATIVE_POINTS, "30");
        DEFAULT_PROPERTY_MAP.put(SYMBOL_SAVE_DIRECTORY, "res/symbols");
        DEFAULT_PROPERTY_MAP.put(SYMBOL_LOAD_DIRECTORY, "res/symbols");
        DEFAULT_PROPERTY_MAP.put(SHOW_REPRESENTATIVE_POINTS_WHILE_DATA_COLLECTING, "false");
        DEFAULT_PROPERTY_MAP.put(UPDATE_HISTOGRAM_WHILE_DRAWING, "false");
        DEFAULT_PROPERTY_MAP.put(SHOW_REPRESENTATIVE_POINTS_WHILE_PREDICTING, "false");
        DEFAULT_PROPERTY_MAP.put(USE_RANDOM_WEIGHT_COLORS, "false");
    }

    public SettingsImpl() {
        try {
            initializeSettings();
        } catch (IOException e) {
            System.err.println("Could not load the property data.");
        }
    }

    private void initializeSettings() throws IOException {
        if (Files.notExists(SETTINGS_FILE_PATH)) {
            System.out.println("Settings file does not exist. Initializing to default values...");
            initializeToDefaultSettings();
            return;
        }

        for(String line : Files.readAllLines(SETTINGS_FILE_PATH)) {
            if(line.isBlank() || line.trim().startsWith(COMMENT_INDICATOR)) continue;

            if(!line.contains(KEY_VALUE_SEPARATOR)) {
                System.err.println("Expected separator '=' in line '" + line + "'.");
                continue;
            }

            String[] parts = line.split(KEY_VALUE_SEPARATOR);

            if(parts.length != 2) {
                System.err.println("Multiple separators '" + KEY_VALUE_SEPARATOR + "' in line '" + line + "'.");
                continue;
            }

            var property = parts[0].trim();
            var value = parts[1].trim();

            if (!DEFAULT_PROPERTY_MAP.containsKey(property)) {
                System.err.println("Skipping deprecated property '" + property + "'.");
                continue;
            }

            System.out.println("Loading property: " + property + " " + KEY_VALUE_SEPARATOR + " " + value);
            propertyMap.put(property, value);
        }
    }

    private void initializeToDefaultSettings() throws IOException {
        propertyMap.putAll(DEFAULT_PROPERTY_MAP);
        Files.createDirectories(SETTINGS_FILE_PATH.getParent());
        Files.createFile(SETTINGS_FILE_PATH);
    }

    @Override
    public String getStringProperty(String property) {
        return propertyMap.containsKey(property) ? propertyMap.get(property) : getDefault(property);
    }

    @Override
    public int getIntProperty(String property) {
        return Integer.parseInt(getStringProperty(property));
    }

    @Override
    public double getDoubleProperty(String property) {
        return Double.parseDouble(getStringProperty(property));
    }

    @Override
    public boolean getBooleanProperty(String property) {
        return Boolean.parseBoolean(getStringProperty(property));
    }

    @Override
    public void setStringProperty(String property, String value) {
        propertyMap.put(property, value);
        notifyListeners(property);
    }

    @Override
    public void setIntProperty(String property, int value) {
        propertyMap.put(property, String.valueOf(value));
        notifyListeners(property);
    }

    @Override
    public void setDoubleProperty(String property, double value) {
        propertyMap.put(property, String.valueOf(value));
        notifyListeners(property);
    }

    @Override
    public void setBooleanProperty(String property, boolean value) {
        propertyMap.put(property, String.valueOf(value));
        notifyListeners(property);
    }

    @Override
    public void addListener(SettingsListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SettingsListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String property) {
        listeners.forEach(l -> l.onPropertyChange(property));
    }

    private String getDefault(String property) {

        var defaultPropertyValue = DEFAULT_PROPERTY_MAP.get(property);

        if (defaultPropertyValue != null) {
            System.out.print("Fetching default property '" + property + "' " + KEY_VALUE_SEPARATOR + " " + defaultPropertyValue);
            propertyMap.put(property, defaultPropertyValue);
        }
        else {
            System.err.println("Warning: Not even default property map contains property '" + property + "'.");
        }

        return defaultPropertyValue;
    }

    @Override
    public void save() {
        var lines = new ArrayList<String>();

        for (var entry : propertyMap.entrySet()) {
            lines.add(entry.getKey() + " " + KEY_VALUE_SEPARATOR + " " + entry.getValue());
        }

        try {
            Files.write(SETTINGS_FILE_PATH, lines);
        } catch (IOException e) {
            System.err.println("Failed to save settings.");
        }
    }
}
