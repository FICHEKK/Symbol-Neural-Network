package settings;

public enum LearningMethod {
    STOCHASTIC("Stochastic"),
    MINI_BATCH("Mini-batch"),
    BATCH("Batch");

    private final String name;

    LearningMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
