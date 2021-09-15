package com.oracle.javafx.scenebuilder.controls.fxom;

import java.util.HashMap;
import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.ext.TransientStateBackup;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class AccordionStateBackup implements TransientStateBackup {

    private final Map<FXOMObject, FXOMObject> accordionMap = new HashMap<>();
    
    @Override
    public boolean canHandle(FXOMObject candidate) {
        return candidate.getSceneGraphObject() instanceof Accordion;
    }

    @Override
    public void backup(FXOMObject candidate) {
        final Accordion accordion  = (Accordion) candidate.getSceneGraphObject();
        final TitledPane currentTitledPane = accordion.getExpandedPane();
        if (currentTitledPane != null) {
            final FXOMObject titledPaneObject
                    = candidate.searchWithSceneGraphObject(currentTitledPane);
            if (titledPaneObject != null) {
                accordionMap.put(candidate, titledPaneObject);
            }
        }
    }
    
    @Override
    public void restore(FXOMObject candidate) {
        final Accordion accordion  = (Accordion) candidate.getSceneGraphObject();
        final FXOMObject titlePaneObject = accordionMap.get(candidate);
        if ((titlePaneObject != null) && (titlePaneObject.getParentObject() == candidate)) {
            assert titlePaneObject.getSceneGraphObject() instanceof TitledPane;
            final TitledPane titledPane = (TitledPane) titlePaneObject.getSceneGraphObject();
            assert accordion.getPanes().contains(titledPane);
            accordion.setExpandedPane(titledPane);
        }
    }
}
