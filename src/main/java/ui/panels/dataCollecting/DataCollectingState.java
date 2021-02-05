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
        public final Map<String, Integer> sampleToPartCount;

        public SingleSymbolTable(String identifier, Map<String, Integer> sampleToPartCount) {
            this.identifier = identifier;
            this.sampleToPartCount = sampleToPartCount;
        }
    }

    public static class SymbolViewPartedCurve extends DataCollectingState {
        public final List<List<Point>> partedCurve;
        public final boolean animate;

        public SymbolViewPartedCurve(List<List<Point>> partedCurve, boolean animate) {
            this.partedCurve = partedCurve;
            this.animate = animate;
        }
    }

    public static class SymbolViewShowContinuousCurveIndex extends DataCollectingState {
        public final boolean showContinuousCurveIndex;

        public SymbolViewShowContinuousCurveIndex(boolean showContinuousCurveIndex) {
            this.showContinuousCurveIndex = showContinuousCurveIndex;
        }
    }

    public static class SymbolViewShowRepresentativePoints extends DataCollectingState {
        public final boolean showRepresentativePoints;

        public SymbolViewShowRepresentativePoints(boolean showRepresentativePoints) {
            this.showRepresentativePoints = showRepresentativePoints;
        }
    }

    public static class SymbolIdentifier extends DataCollectingState {
        public final boolean isSymbolIdentifierValid;

        public SymbolIdentifier(boolean isSymbolIdentifierValid) {
            this.isSymbolIdentifierValid = isSymbolIdentifierValid;
        }
    }
}
