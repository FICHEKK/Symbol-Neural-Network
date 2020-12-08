package settings;

public interface DataCollectingStageSettings {
    String getSymbolIdentifier();
    String getSymbolSaveDirectory();
    int getNumberOfRepresentativePoints();
    boolean showRepresentativeSymbol();
}
