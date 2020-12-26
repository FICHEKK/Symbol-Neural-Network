package ui.panels;

@FunctionalInterface
public interface ModelListener<S> {
    void onNextState(S state);
}
