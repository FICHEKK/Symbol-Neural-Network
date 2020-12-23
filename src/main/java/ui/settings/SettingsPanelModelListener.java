package ui.settings;

import ui.settings.state.SettingsPanelState;

@FunctionalInterface
public interface SettingsPanelModelListener {
    void onNextState(SettingsPanelState state);
}
