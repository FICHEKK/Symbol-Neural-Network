package settings;

public interface LearningStageSettings {
    int getBatchSize();
    double getLearningRate();
    double getMinAcceptableError();
    int getMaxIterations();
    int getNumberOfRepresentativePoints();
    String getSymbolLoadDirectory();
}
