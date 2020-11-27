package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.DropTarget;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles.SceneHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.pring.NodePring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.AbstractTring;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;

public class SceneDriver extends AbstractDriver {

	private final ApplicationContext context;

    public SceneDriver(ApplicationContext context, ContentPanelController contentPanelController) {
        super(contentPanelController);
        this.context = context;
    }

    @Override
    public AbstractHandles<?> makeHandles(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        assert fxomObject instanceof FXOMInstance;
        return new SceneHandles(context, contentPanelController, (FXOMInstance) fxomObject);
    }

    @Override
    public AbstractTring<?> makeTring(DropTarget dropTarget) {
        return null;
    }

    @Override
    public AbstractPring<?> makePring(FXOMObject fxomObject) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject root = designHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        assert root instanceof FXOMInstance;
        return new NodePring(contentPanelController, (FXOMInstance) root);
    }

    @Override
    public AbstractResizer<?> makeResizer(FXOMObject fxomObject) {
        // Resize gesture does not apply to Scenes
        return null;
    }

    @Override
    public AbstractCurveEditor<?> makeCurveEditor(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject) {
        return fxomObject;
    }

    @Override
    public AbstractDropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        assert fxomObject instanceof FXOMInstance;
        return new AccessoryDropTarget((FXOMInstance) fxomObject, DesignHierarchyMask.Accessory.ROOT);
    }

    @Override
    public Node getInlineEditorBounds(FXOMObject fxomObject) {
        return null;
    }

    @Override
    public boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds) {
        assert fxomObject.getSceneGraphObject() instanceof Scene;
        DesignHierarchyMask designHierarchyMask = new DesignHierarchyMask(fxomObject);
        FXOMObject root = designHierarchyMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert root != null;
        assert root.getSceneGraphObject() instanceof Node;
        Node rootNode = (Node) root.getSceneGraphObject();
        final Bounds rootNodeBounds = rootNode.localToScene(rootNode.getLayoutBounds(), true /* rootScene */);
        return rootNodeBounds.intersects(bounds);
    }
}
