package com.oracle.javafx.scenebuilder.api.controls;

import java.util.List;

public interface ControlsProvider {
    List<ControlFactory<?>> getControls();
}
