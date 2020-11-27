package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;

public class SceneHandles extends AbstractGenericHandles<Scene> {
    private Node sceneGraphObject;

    public SceneHandles(
    		ApplicationContext context,
    		Content contentPanelController,
            FXOMInstance fxomInstance) {
        super(context, contentPanelController, fxomInstance, Scene.class);

        final DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(getFxomObject());
        final FXOMObject root = designHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root instanceof FXOMInstance;
        assert root.getSceneGraphObject() instanceof Node;
        sceneGraphObject = (Node) root.getSceneGraphObject();
    }

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return sceneGraphObject.getLayoutBounds();
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return sceneGraphObject;
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        startListeningToLayoutBounds(sceneGraphObject);
        startListeningToLocalToSceneTransform(sceneGraphObject);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        stopListeningToLayoutBounds(sceneGraphObject);
        stopListeningToLocalToSceneTransform(sceneGraphObject);
    }

}
