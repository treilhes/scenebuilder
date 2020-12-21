package com.oracle.javafx.scenebuilder.controls;

import java.util.Arrays;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.controls.ControlFactory;
import com.oracle.javafx.scenebuilder.api.controls.ControlsProvider;

public class DefaultControlsProvider implements ControlsProvider {

    public DefaultControlsProvider() {
    }

    @Override
    public List<ControlFactory<?>> getControls() {
        return Arrays.asList();
    }

}
