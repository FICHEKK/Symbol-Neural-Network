package ui.predicting;

public abstract class PredictingState {

    public static class Histogram extends PredictingState {
        public final String[] identifiers;
        public final double[] prediction;

        public Histogram(String[] identifiers, double[] prediction) {
            this.identifiers = identifiers;
            this.prediction = prediction;
        }
    }

    public static class SymbolCanvas extends PredictingState {
        public final int numberOfRepresentativePoints;
        public final boolean showRepresentativePoints;
        public final boolean isDrawingEnabled;

        public SymbolCanvas(int numberOfRepresentativePoints, boolean showRepresentativePoints, boolean isDrawingEnabled) {
            this.numberOfRepresentativePoints = numberOfRepresentativePoints;
            this.showRepresentativePoints = showRepresentativePoints;
            this.isDrawingEnabled = isDrawingEnabled;
        }
    }

    public static class Message extends PredictingState {
        public final String message;

        public Message(String message) {
            this.message = message;
        }
    }
}
