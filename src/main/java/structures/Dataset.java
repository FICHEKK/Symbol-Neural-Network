package structures;

public class Dataset {
    public final double[][] X;
    public final double[][] y;
    public final String[] identifiers;

    public Dataset(double[][] X, double[][] y, String[] identifiers) {
        this.X = X;
        this.y = y;
        this.identifiers = identifiers;
    }
}
