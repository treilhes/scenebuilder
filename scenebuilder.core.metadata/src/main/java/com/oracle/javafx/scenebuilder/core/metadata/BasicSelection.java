package com.oracle.javafx.scenebuilder.core.metadata;

import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface BasicSelection {

    FXOMObject getCommonParentObject();

    Set<FXOMInstance> getSelectedInstances();

}
