package ui.training;

public enum TrainingMethod {
    STOCHASTIC("Stochastic"),
    MINI_BATCH("Mini-batch"),
    BATCH("Batch");

    private final String name;

    TrainingMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static TrainingMethod from(String name) {
        for (var method : values()) {
            if (method.toString().equals(name)) {
                return method;
            }
        }

        throw new IllegalArgumentException("Could not convert '" + name + "' to a specific training method.");
    }
}
