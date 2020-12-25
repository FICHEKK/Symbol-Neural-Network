package ui;

@FunctionalInterface
public interface ModelListener<S> {
    void onNextState(S state);
}
