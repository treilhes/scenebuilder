package com.oracle.javafx.scenebuilder.controls.fxom;

import java.util.HashMap;
import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.ext.TransientStateBackup;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabPaneStateBackup implements TransientStateBackup {

    private final Map<FXOMObject, FXOMObject> tabPaneMap = new HashMap<>();
    @Override
    public boolean canHandle(FXOMObject candidate) {
        return candidate.getSceneGraphObject() instanceof TabPane;
    }

    @Override
    public void backup(FXOMObject candidate) {
        final TabPane tabPane = (TabPane) candidate.getSceneGraphObject();
        final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            final FXOMObject tabObject 
                    = candidate.searchWithSceneGraphObject(currentTab);
            if (tabObject != null) {
                tabPaneMap.put(candidate, tabObject);
            }
        }
    }

    @Override
    public void restore(FXOMObject candidate) {
        final TabPane tabPane = (TabPane) candidate.getSceneGraphObject();
        final FXOMObject tabObject = tabPaneMap.get(candidate);
        if ((tabObject != null) && (tabObject.getParentObject() == candidate)) {
            assert tabObject.getSceneGraphObject() instanceof Tab;
            final Tab tab = (Tab) tabObject.getSceneGraphObject();
            assert tabPane.getTabs().contains(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }


}
