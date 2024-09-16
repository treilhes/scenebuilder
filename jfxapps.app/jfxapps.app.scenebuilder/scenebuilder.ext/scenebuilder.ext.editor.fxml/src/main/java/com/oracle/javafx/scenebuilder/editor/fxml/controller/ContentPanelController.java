/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.editor.fxml.controller;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.SceneGraphObject;
import com.gluonhq.jfxapps.core.fxom.collector.SceneGraphCollector;
import com.gluonhq.jfxapps.util.javafx.Picker;
import com.oracle.javafx.scenebuilder.api.control.Driver;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * This class creates and controls the <b>Content Panel</b> of Scene Builder
 * Kit.
 *
 */
@ApplicationInstanceSingleton
public class ContentPanelController //extends AbstractFxmlController
        implements Content, FXOMDocument.SceneGraphHolder {

    private static Logger logger = LoggerFactory.getLogger(ContentPanelController.class);

//    @FXML
//    private ScrollPane scrollPane;
//    @FXML
//    private Pane workspacePane;
//    @FXML
//    private Rectangle extensionRect;
//    @FXML
//    private Label backgroundPane;
//    @FXML
//    private Group scalingGroup;
//    @FXML
//    private SubScene contentSubScene;
//    @FXML
//    private Group contentGroup;
//    @FXML
//    private Pane glassLayer;
    @FXML
    private Group outlineLayer;

    private final Driver driver;
    //private final ModeManager modeManager;
    //private final Drag drag;
    //private final Selection selection;
    private final ApplicationInstanceEvents documentManager;
    private final MessageLogger messageLogger;
    //private final FXOMObjectMask.Factory maskFactory;
    //private final Workspace workspaceController;

    private final Picker picker = new Picker();

    private FXOMDocument fxomDocument;
    private RuntimeException layoutException;

    private Subject<Boolean> contentChanged = PublishSubject.create();
    /*
     * Public
     */

    /**
     * Creates a content panel controller for the specified editor controller.
     *
     * @param editorController the editor controller (never null).
     */
    // @formatter:off
    public ContentPanelController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            Driver driver,
            //FXOMObjectMask.Factory maskFactory,


            //@Lazy ModeManager modeManager,
            //Drag drag,
            //Workspace workspaceController,
            //JobManager jobManager,
            //Selection selection,
            MessageLogger messageLogger
            ) {
        // @formatter:on
        //super(i18n, scenebuilderManager, documentManager, ContentPanelController.class.getResource("ContentPanel.fxml"));
        this.driver = driver;
        //this.modeManager = modeManager;
        //this.maskFactory = maskFactory;
        //this.drag = drag;
        //this.selection = selection;
        //this.workspaceController = workspaceController;
        this.documentManager = documentManager;
        this.messageLogger = messageLogger;
        //this.contextMenu = contextMenu;
//        this.editModeController = editModeController;
//        this.pickModeController = pickModeController;
//        this.workspaceController = workspaceController;

        //this.alignmentGuidesColorPreference = alignmentGuidesColorPreference;

//        this.parentRingColorPreference = parentRingColorPreference;

        documentManager.fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));



    }

    @FXML
    public void initialize() {

//        drag.dragSourceProperty().addListener((ov, t, t1) -> dragSourceDidChange());
//
//        drag.dropTargetProperty().addListener((ov, t, t1) -> dropTargetDidChange());

        //setGuidesColor(alignmentGuidesColorPreference.getValue());

        // setPringColor(parentRingColorPreference.getValue());

        //alignmentGuidesColorPreference.getObservableValue().addListener((ob, o, n) -> setGuidesColor(n));

        // parentRingColorPreference.getObservableValue().addListener((ob,o,n) ->
        // setPringColor(n));

        controllerDidLoadFxml();
    }

    public void controllerDidLoadFxml() {

        // Sanity checks

        assert outlineLayer != null;
        assert outlineLayer.isMouseTransparent();
        assert outlineLayer.isFocusTraversable() == false;
//        assert pringLayer != null;
//        assert pringLayer.isMouseTransparent() == false;
//        assert pringLayer.isFocusTraversable() == false;
//        assert handleLayer != null;
//        assert handleLayer.isMouseTransparent() == false;
//        assert handleLayer.isFocusTraversable() == false;
//        assert rudderLayer != null;
//        assert rudderLayer.isMouseTransparent() == true;
//        assert rudderLayer.isFocusTraversable() == false;

        outlineLayer.setManaged(false);
//        pringLayer.setManaged(false);
//        handleLayer.setManaged(false);
//        rudderLayer.setManaged(false);
//
//        // Setup our workspace controller
//        workspaceController.panelControllerDidLoadFxml(scrollPane, scalingGroup, contentSubScene, contentGroup,
//                backgroundPane, extensionRect);
//        themeDidChange(); // To setup initial value of WorkspaceController.themeStyleSheet

//        resetViewport();
//        setupEventTracingFilter();
//
//        // Setup the context menu
//        scrollPane.setContextMenu(contextMenu.getContextMenu());

        // Setup default workspace background
        // setWorkspaceBackground(ImageUtils.getImage(getDefaultWorkspaceBackgroundURL()));
    }

    /**
     * @treatAsPrivate Returns true if this content panel is able to display the
     *                 content ie <br/>
     *                 1) fxomDocument != null<br/>
     *                 2) (fxomDocument.getFxomRoot() == null) or
     *                 fxomDocument.getFxomRoot().isNode()<br/>
     *
     * @return true if this content panel is able to display the content
     */
    @Override
    public boolean isDisplayable() {
        final boolean result;

        if (fxomDocument == null) {
            result = false;
        } else if (fxomDocument.getFxomRoot() == null) {
            result = true;
        } else {
            result = fxomDocument.getDisplayNodeOrSceneGraphRoot() instanceof Node
                    && layoutException == null;
        }

        return result;
    }


//    /**
//     * Return the scaling factor used by this content panel.
//     *
//     * @return the scaling factor used by this content panel.
//     */
//    @Override
//    public double getScaling() {
//        return workspaceController.getScaling();
//    }

//    /**
//     * Sets the scaling factor to be used by this content panel.
//     *
//     * @param scaling the scaling factor to be used by this content panel.
//     */
//    @Override
//    public void setScaling(double scaling) {
//        this.workspaceController.setScaling(scaling);
//    }

//    /**
//     * Returns true if this content panel automatically resize 3D content.
//     *
//     * @return true if this content panel automatically resize 3D content.
//     */
//    public boolean isAutoResize3DContent() {
//        return workspaceController.isAutoResize3DContent();
//    }
//
//    /**
//     * Enables or disables autoresizing of 3D content.
//     *
//     * @param autoResize3DContent true if this content panel should autoresize 3D
//     *                            content.
//     */
//    public void setAutoResize3DContent(boolean autoResize3DContent) {
//        workspaceController.setAutoResize3DContent(autoResize3DContent);
//    }
//
//    /**
//     * Returns null or the image used for tiling the background of this content
//     * panel.
//     *
//     * @return null or the image used for tiling the background of this content
//     *         panel.
//     */
//    public Image getWorkspaceBackground() {
//        final Image result;
//
//        final Background bg = workspacePane.getBackground();
//        if (bg == null) {
//            result = null;
//        } else {
//            assert bg.getImages().size() == 1;
//            result = bg.getImages().get(0).getImage();
//        }
//
//        return result;
//    }

    /**
     * Returns URL of the default workspace background.
     *
     * @return URL of the default workspace background (never null).
     */
//    public static URL getDefaultWorkspaceBackgroundURL() {
//        assert ImageUtils.getUIURL("Background-Neutral-Uniform.png") != null;
//        return ImageUtils.getUIURL("Background-Neutral-Uniform.png");
//    }


    /**
     * Returns the topmost FXOMObject at (sceneX, sceneY) in this content panel.
     *
     * @param sceneX x coordinate of a scene point
     * @param sceneY y coordinate of a scene point
     * @return null or the topmost FXOMObject located at (sceneX, sceneY)
     */
    @Override
    public FXOMObject pick(double sceneX, double sceneY) {
        return pick(sceneX, sceneY, Collections.emptySet());
    }

    /**
     * Returns the topmost FXOMObject at (sceneX, sceneY) but ignoring objects from
     * the exclude set.
     *
     * @param sceneX   x coordinate of a scene point
     * @param sceneY   y coordinate of a scene point
     * @param excludes null or a set of FXOMObject to be excluded from the pick.
     * @return null or the topmost FXOMObject located at (sceneX, sceneY)
     */
    @Override
    public FXOMObject pick(double sceneX, double sceneY, Set<FXOMObject> excludes) {
        final FXOMObject result;

        if (isDisplayable()) {
            result = pick(fxomDocument, sceneX, sceneY, excludes);
        } else {
            result = null;
        }

        return result;
    }

    public FXOMObject pick(FXOMDocument fxomDocument, double sceneX, double sceneY, Set<FXOMObject> excludes) {
        assert fxomDocument != null;

        if (fxomDocument.getFxomRoot() == null) {
            return null;
        }

        Node displayNode = fxomDocument.getDisplayNode();
        if (displayNode != null) {
            FXOMObject startObject = fxomDocument.getFxomRoot().collect(SceneGraphCollector.findSceneGraphObject(displayNode)).get();
            if (startObject == null || excludes.contains(startObject)) {
                return null;
            }
            return pick(startObject, sceneX, sceneY, excludes);
        }

        if (excludes.contains(fxomDocument.getFxomRoot())) {
            return null;
        }

        return pick(fxomDocument.getFxomRoot(), sceneX, sceneY, excludes);
    }

    /**
     * Returns the topmost FXOMObject at (sceneX, sceneY) but ignoring objects from
     * the exclude set and starting the search from startObject.
     *
     * @param startObject starting point of the search
     * @param sceneX      x coordinate of a scene point
     * @param sceneY      y coordinate of a scene point
     * @param excludes    null or a set of FXOMObject to be excluded from the pick.
     * @return null or the topmost FXOMObject located at (sceneX, sceneY)
     */
    public FXOMObject pick(FXOMObject startObject, double sceneX, double sceneY, Set<FXOMObject> excludes) {

        final FXOMObject result;

        assert isDisplayable();
        assert startObject != null;
        assert startObject.getSceneGraphObject().isInstanceOf(Node.class);
        assert excludes != null;
        assert excludes.contains(startObject) == false;

        picker.getExcludes().clear();

        excludes.stream().map(FXOMObject::getSceneGraphObject).filter(SceneGraphObject::isNode)
                .map(sgo -> sgo.getAs(Node.class)).forEach(picker.getExcludes()::add);

//        for (FXOMObject exclude : excludes) {
//            if (exclude.getSceneGraphObject().isInstanceOf(Node.class)) {
//                picker.getExcludes().add(exclude.getSceneGraphObject().getAs(Node.class));
//            }
//        }

        final Node startNode = startObject.getSceneGraphObject().getAs(Node.class);
        final List<Node> hitNodes = picker.pick(startNode, sceneX, sceneY);
        if (hitNodes == null) {
            result = null;
        } else {
            assert hitNodes.isEmpty() == false;

            FXOMObject hitObject = null;
            final Iterator<Node> it = hitNodes.iterator();
            while ((hitObject == null) && it.hasNext()) {
                final Node hitNode = it.next();
                hitObject = searchWithNode(hitNode, sceneX, sceneY);
                if (excludes.contains(hitObject)) {
                    hitObject = null;
                }
            }
            result = hitObject;
        }

        return result;
    }

    /**
     * Returns the FXOMObject which matches (sceneGraphNode, sceneX, sceneY).
     *
     * @param sceneGraphNode scene graph node
     * @param sceneX         x coordinate of a scene point
     * @param sceneY         y coordinate of a scene point
     * @return an FXOMObject that matches (sceneGraphNode, sceneX, sceneY)
     */
    @Override
    public FXOMObject searchWithNode(Node sceneGraphNode, double sceneX, double sceneY) {
        final FXOMObject result;

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        final FXOMObject match = fxomDocument.collect(SceneGraphCollector.findSceneGraphObject(sceneGraphNode)).get();
        /*
         * Refine the search. With the logic above, a click in a 'tab header' returns
         * the fxom object associated to the 'tab pane'. We would like to get the fxom
         * object associated to the 'tab'. When the pick result is a 'TabPane' we need
         * to refine this result. This refinement logic is available in AbstractDriver.
         */
        if (match != null) {
            result = driver.refinePick(sceneGraphNode, sceneX, sceneY, match);
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void layoutContent() {
        final Exception currentLayoutException = getLayoutException();

        var sceneGraphObject = fxomDocument.getFxomRoot().getSceneGraphObject();
        if (sceneGraphObject.isParent()) {
            var node = sceneGraphObject.getAs(Parent.class);

            try {
                node.layout();
                layoutException = null;
            } catch (RuntimeException x) {
                logger.error("Layout failure", x);
                layoutException = x;

                if (layoutException != currentLayoutException) {
                    messageLogger.logWarningMessage("log.warning.layout.failed", layoutException.getMessage());
                }
            }
        }
    }
//
//    /*
//     * Public which are *private*...
//     */
//
//    /**
//     * @treatAsPrivate Returns the background object of this content panel.
//     * @return the background object of this content panel.
//     */
//    @Override
//    public Pane getWorkspacePane() {
//        return workspacePane;
//    }
//
//    /**
//     * @treatAsPrivate Returns the glass layer container.
//     * @return the glass layer container.
//     */
//    @Override
//    public Pane getGlassLayer() {
//        return glassLayer;
//    }

    /**
     * @treatAsPrivate Returns the outline layer container.
     * @return the outline layer container.
     */
    public Group getOutlineLayer() {
        return outlineLayer;
    }

//    /**
//     * @treatAsPrivate Returns the parent ring layer container.
//     * @return the parent ring layer container.
//     */
//    @Override
//    public Group getPringLayer() {
//        return pringLayer;
//    }
//
//
//    /**
//     * @treatAsPrivate Returns the handle layer container.
//     * @return the handle layer container.
//     */
//    @Override
//    public Group getHandleLayer() {
//        return handleLayer;
//    }
//
//    /**
//     * @treatAsPrivate Returns the rudder layer container.
//     * @return the rudder layer container.
//     */
//    @Override
//    public Group getRudderLayer() {
//        return rudderLayer;
//    }

//    /**
//     * @treatAsPrivate Returns the sub scene holding the user scene graph.
//     * @return the sub scene holding the user scene graph.
//     */
//    @Override
//    public SubScene getContentSubScene() {
//        return contentSubScene;
//    }

    // TODO this method seems used for testing, need to find an alternative
//    /**
//     * @treatAsPrivate Returns the handles associated an fxom object.
//     * Returns null if the fxom object is currently not selected or
//     * if content panel is not in 'edit mode'.
//     * @param fxomObject an fxom object
//     * @return null or the associated handles
//     */
//    @Override
//    public Handles<?> lookupHandles(FXOMObject fxomObject) {
//        final Handles<?> result;
//
//        if (currentModeController != editModeController) {
//            result = null;
//        } else {
//            result = editModeController.lookupHandles(fxomObject);
//        }
//
//        return result;
//    }



    /*
     * AbstractPanelController<TreeView>
     */

    /**
     * @treatAsPrivate fxom document has changed
     * @param fxomDocument the new fxom document
     */
    protected void fxomDocumentDidChange(FXOMDocument newFxomDocument) {

        if (fxomDocument != null) {
            assert fxomDocument.getSceneGraphHolder() == this;
            fxomDocument.endHoldingSceneGraph();
        }

        if (newFxomDocument != null) {
            assert newFxomDocument.getSceneGraphHolder() == null;
            newFxomDocument.beginHoldingSceneGraph(this);
        }

        if (newFxomDocument != null) {
            newFxomDocument.refreshSceneGraph();
        }

        fxomDocument = newFxomDocument;

        contentChanged.onNext(true);
    }

    /*
     * FXOMDocument.SceneGraphHolder
     */

    /**
     * @treatAsPrivate fxom document will reconstruct the user scene graph
     */
    @Override
    public void fxomDocumentWillRefreshSceneGraph(FXOMDocument fxomDocument) {
        // Nothing special to do
    }

    /**
     * @treatAsPrivate fxom document did reconstruct the user scene graph
     */
    @Override
    public void fxomDocumentDidRefreshSceneGraph(FXOMDocument fxomDocument) {
        contentChanged.onNext(true);
    }

//    @Override
//    public ModeManager getModeManager() {
//        return modeManager;
//    }

    @Override
    public Subject<Boolean> contentChanged() {
        return contentChanged;
    }

    @Override
    public Object getRoot() {
        return isDisplayable() ? fxomDocument.getDisplayNodeOrSceneGraphRoot() : null;
    }

    @Override
    public boolean hasContent() {
        return documentManager.fxomDocument().get() != null;
    }

    @Override
    public RuntimeException getLayoutException() {
        return layoutException;
    }
}
