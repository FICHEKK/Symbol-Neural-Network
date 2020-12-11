package settings;

@FunctionalInterface
public interface SettingsListener {
    void onPropertyChange(String property);
}
