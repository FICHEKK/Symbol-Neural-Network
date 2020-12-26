package ui.settings;

public abstract class SettingsState {

    public static class DataCollectingSection extends SettingsState {
        public final boolean isNumberOfRepresentativePointsValid;
        public final boolean isSymbolSaveDirectoryValid;

        public DataCollectingSection(boolean isNumberOfRepresentativePointsValid, boolean isSymbolSaveDirectoryValid) {
            this.isNumberOfRepresentativePointsValid = isNumberOfRepresentativePointsValid;
            this.isSymbolSaveDirectoryValid = isSymbolSaveDirectoryValid;
        }
    }

    public static class TrainingSection extends SettingsState {
        public final boolean isSymbolLoadDirectoryValid;

        public TrainingSection(boolean isSymbolLoadDirectoryValid) {
            this.isSymbolLoadDirectoryValid = isSymbolLoadDirectoryValid;
        }
    }
}
