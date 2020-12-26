package ui.panels.dataCollecting;

import structures.Point;

import java.util.List;
import java.util.Map;

public abstract class DataCollectingState {

    public static class SymbolCanvas extends DataCollectingState {
        public final int numberOfRepresentativePoints;
        public final boolean showRepresentativePoints;
        public final boolean isDrawingEnabled;

        public SymbolCanvas(int numberOfRepresentativePoints, boolean showRepresentativePoints, boolean isDrawingEnabled) {
            this.numberOfRepresentativePoints = numberOfRepresentativePoints;
            this.showRepresentativePoints = showRepresentativePoints;
            this.isDrawingEnabled = isDrawingEnabled;
        }
    }

    public static class AllSymbolsTable extends DataCollectingState {
        public final Map<String, Integer> symbolToSampleCount;
        public final int totalSampleCount;
        public final int numberOfPoints;

        public AllSymbolsTable(Map<String, Integer> symbolToSampleCount, int totalSampleCount, int numberOfPoints) {
            this.symbolToSampleCount = symbolToSampleCount;
            this.totalSampleCount = totalSampleCount;
            this.numberOfPoints = numberOfPoints;
        }
    }

    public static class SingleSymbolTable extends DataCollectingState {
        public final String identifier;
        public final List<String> samples;

        public SingleSymbolTable(String identifier, List<String> samples) {
            this.identifier = identifier;
            this.samples = samples;
        }
    }

    public static class SymbolView extends DataCollectingState {
        public final List<Point> normalizedPoints;

        public SymbolView(List<Point> normalizedPoints) {
            this.normalizedPoints = normalizedPoints;
        }
    }

    public static class SymbolIdentifier extends DataCollectingState {
        public final boolean isSymbolIdentifierValid;

        public SymbolIdentifier(boolean isSymbolIdentifierValid) {
            this.isSymbolIdentifierValid = isSymbolIdentifierValid;
        }
    }
}
