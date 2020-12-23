package ui.settings.state;

public class SettingsPanelDataCollectingState extends SettingsPanelState {

    public final boolean isNumberOfRepresentativePointsValid;
    public final boolean isSymbolSaveDirectoryValid;

    public SettingsPanelDataCollectingState(boolean isNumberOfRepresentativePointsValid, boolean isSymbolSaveDirectoryValid) {
        this.isNumberOfRepresentativePointsValid = isNumberOfRepresentativePointsValid;
        this.isSymbolSaveDirectoryValid = isSymbolSaveDirectoryValid;
    }
}
