/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.contenteditor.controller;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.content.ModeManager;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.outline.Outline;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.contenteditor.preferences.global.AlignmentGuidesColorPreference;
import com.oracle.javafx.scenebuilder.contenteditor.preferences.global.BackgroundImagePreference;
import com.oracle.javafx.scenebuilder.core.content.util.BoundsUnion;
import com.oracle.javafx.scenebuilder.core.content.util.BoundsUtils;
import com.oracle.javafx.scenebuilder.core.content.util.Picker;
import com.oracle.javafx.scenebuilder.core.content.util.ScrollPaneBooster;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlPanelController;
import com.oracle.javafx.scenebuilder.sb.preferences.global.ParentRingColorPreference;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * This class creates and controls the <b>Content Panel</b> of Scene Builder Kit.
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ContentPanelController extends AbstractFxmlPanelController
                                    implements Content, FXOMDocument.SceneGraphHolder {

    private static Logger logger = LoggerFactory.getLogger(ContentPanelController.class);
    
    @FXML private ScrollPane scrollPane;
    @FXML private Pane workspacePane;
    @FXML private Rectangle extensionRect;
    @FXML private Label backgroundPane;
    @FXML private Group scalingGroup;
    @FXML private SubScene contentSubScene;
    @FXML private Group contentGroup;
    @FXML private Pane glassLayer;
    @FXML private Group outlineLayer;
    //@FXML private Group pringLayer;
    //@FXML private Group handleLayer;
    //@FXML private Group rudderLayer;

    private boolean guidesVisible = true;
    private Paint pringColor = Color.rgb(238, 168, 47);
    private Paint guidesColor = Color.RED;

    private WorkspaceController workspaceController;
    private final HudWindowController hudWindowController;

    //private final EditModeController editModeController;
    //private final PickModeController pickModeController;
    //private AbstractModeController currentModeController;

    private boolean tracingEvents; // For debugging purpose

    private final Picker picker = new Picker();
	private final AlignmentGuidesColorPreference alignmentGuidesColorPreference;
	private final BackgroundImagePreference backgroundImagePreference;
	private final ParentRingColorPreference parentRingColorPreference;
	private final Editor editorController;
	private final ApplicationContext context;
    private FXOMDocument oldDocument;
    private final Driver driver;
    private final ModeManager modeManager;
    private final Drag drag;

    /*
     * Public
     */

    /**
     * Creates a content panel controller for the specified editor controller.
     *
     * @param editorController the editor controller (never null).
     */
    public ContentPanelController(
            @Autowired Api api,
    		@Autowired @Lazy Editor editorController,
    		@Autowired Driver driver,
    		@Autowired AlignmentGuidesColorPreference alignmentGuidesColorPreference,
    		@Autowired BackgroundImagePreference backgroundImagePreference,
    		@Autowired ParentRingColorPreference parentRingColorPreference,
    		@Autowired DocumentManager documentManager,
    		@Autowired @Lazy HudWindowController hudWindowController,
    		@Autowired @Lazy ModeManager modeManager

//    		@Autowired @Lazy EditModeController editModeController,
//    		@Autowired @Lazy PickModeController pickModeController,
//    		@Autowired WorkspaceController workspaceController
    		) {
        super(api, ContentPanelController.class.getResource("ContentPanel.fxml"), I18N.getBundle());
        this.context = api.getContext();
        this.editorController = editorController;
        this.driver = driver;
        this.modeManager = modeManager;
        this.drag = api.getApiDoc().getDrag();
        //TODO try to use getBean without parameters
        this.workspaceController = context.getBean(WorkspaceController.class, editorController, documentManager);
        
        this.hudWindowController = hudWindowController;
//        this.editModeController = editModeController;
//        this.pickModeController = pickModeController;
//        this.workspaceController = workspaceController;

        this.alignmentGuidesColorPreference = alignmentGuidesColorPreference;
        this.backgroundImagePreference = backgroundImagePreference;
        this.parentRingColorPreference = parentRingColorPreference;

        api.getApiDoc().getDocumentManager().fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));
        api.getApiDoc().getDocumentManager().selectionDidChange().subscribe(s -> editorSelectionDidChange());
        api.getApiDoc().getJobManager().revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());
        
        if (logger.isDebugEnabled()) {
            tracingEvents = false;
            setupEventTracingFilter();
        }
    }

    @FXML
    public void initialize() {

        drag.dragSourceProperty().addListener((ov, t, t1) -> dragSourceDidChange());

        drag.dropTargetProperty().addListener((ov, t, t1) -> dropTargetDidChange());

    	setGuidesColor(alignmentGuidesColorPreference.getValue());
    	setWorkspaceBackground(backgroundImagePreference.getBackgroundImageImage());
    	setPringColor(parentRingColorPreference.getValue());

    	alignmentGuidesColorPreference.getObservableValue().addListener((ob,o,n) -> setGuidesColor(n));
    	backgroundImagePreference.getObservableValue().addListener(
    			(ob,o,n) -> setWorkspaceBackground(BackgroundImagePreference.getImage(n)));
    	parentRingColorPreference.getObservableValue().addListener((ob,o,n) -> setPringColor(n));
    }
    /**
     * Returns true if this content panel displays outlines.
     *
     * @return true if this content panel displays outlines.
     */
    public boolean isOutlinesVisible() {
        return (contentGroup != null) && (contentGroup.isVisible() == false);
    }

    /**
     * Enables or disables outline display in this content panel.
     * @param outlinesVisible true if outlines should be visible.
     */
    public void setOutlinesVisible(boolean outlinesVisible) {
        if (outlinesVisible != isOutlinesVisible()) {
            if (outlinesVisible) {
                beginShowingOutlines();
            } else {
                endShowingOutlines();
            }
        }
    }

    /**
     * Returns true if this content panel displays alignment guides.
     *
     * @return true if this content panel displays alignment guides.
     */
    @Override
    public boolean isGuidesVisible() {
        return guidesVisible;
    }

    /**
     * Enables or disables alignment guide display in this content panel.
     *
     * @param guidesVisible  true if alignment guides should be visible.
     */
    public void setGuidesVisible(boolean guidesVisible) {
        this.guidesVisible = guidesVisible;
    }

    /**
     * Returns the color used by this content panel to draw parent rings.
     *
     * @return the color used by this content panel to draw parent rings.
     */
    @Override
    public Paint getPringColor() {
        return pringColor;
    }

    /**
     * Sets the color used by this content panel to draw parent rings.
     *
     * @param pringColor the color used by this content panel to draw parent rings.
     */
    public void setPringColor(Paint pringColor) {
        this.pringColor = pringColor;
    }

    /**
     * Returns the color used by this content panel to draw alignment guides.
     *
     * @return the color used by this content panel to draw alignment guides.
     */
    @Override
    public Paint getGuidesColor() {
        return guidesColor;
    }

    /**
     * Sets the color used by this content panel to draw alignment guides.
     *
     * @param guidesColor the color used by this content panel to draw alignment guides.
     */
    public void setGuidesColor(Paint guidesColor) {
        this.guidesColor = guidesColor;
    }

    /**
     * Return the scaling factor used by this content panel.
     *
     * @return the scaling factor used by this content panel.
     */
    @Override
    public double getScaling() {
        return workspaceController.getScaling();
    }

    /**
     * Sets the scaling factor to be used by this content panel.
     *
     * @param scaling the scaling factor to be used by this content panel.
     */
    @Override
    public void setScaling(double scaling) {
        this.workspaceController.setScaling(scaling);
    }


    /**
     * Returns true if this content panel automatically resize 3D content.
     *
     * @return  true if this content panel automatically resize 3D content.
     */
    public boolean isAutoResize3DContent() {
        return workspaceController.isAutoResize3DContent();
    }

    /**
     * Enables or disables autoresizing of 3D content.
     *
     * @param autoResize3DContent  true if this content panel should autoresize 3D content.
     */
    public void setAutoResize3DContent(boolean autoResize3DContent) {
        workspaceController.setAutoResize3DContent(autoResize3DContent);
    }

    /**
     * Returns null or the image used for tiling the background of this content panel.
     *
     * @return null or the image used for tiling the background of this content panel.
     */
    public Image getWorkspaceBackground() {
        final Image result;

        final Background bg = workspacePane.getBackground();
        if (bg == null) {
            result = null;
        } else {
            assert bg.getImages().size() == 1;
            result = bg.getImages().get(0).getImage();
        }

        return result;
    }

    /**
     * Sets the image used for tiling the background of this content panel.
     *
     * @param image null or the image for tiling the background of this content panel.
     */
    public void setWorkspaceBackground(Image image) {
        final Background bg;
        if (image == null) {
            bg = null;
        } else {
            final BackgroundImage bgi = new javafx.scene.layout.BackgroundImage(image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            bg = new Background(bgi);
        }
        workspacePane.setBackground(bg);
    }

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
     * Scrolls this content panel so that the selected objects are visible.
     */
    @Override
    // TODO Need to use CoordinateHelper here ?
    public void scrollToSelection() {
        // Walk through the selected objects and computes the enclosing bounds.
        final BoundsUnion union = new BoundsUnion();
        final Selection selection = getApi().getApiDoc().getSelection();
        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            for (FXOMObject i : osg.getItems()) {
                final DesignHierarchyMask mask = new DesignHierarchyMask(i);
                final FXOMObject nodeFxomObject = mask.getClosestFxNode();
                if (nodeFxomObject != null) {
                    final Node node = (Node) nodeFxomObject.getSceneGraphObject();
                    assert node.getLayoutBounds() != null;
                    final Bounds nodeBounds = node.localToScene(node.getLayoutBounds(), true /* rootScene */);
                    assert nodeBounds != null;
                    union.add(nodeBounds);
                }
            }
        }

        if (union.getResult() != null) {
            final Node content = scrollPane.getContent();
            final Bounds sceneEnclosing = BoundsUtils.to2DBounds(union.getResult());
            assert sceneEnclosing.getMinZ() == 0.0; // Side effect of SubScene
            assert sceneEnclosing.getMaxZ() == 0.0;
         // TODO do i need to use CoordinateHelper here?
            final Bounds localEnclosing = content.sceneToLocal(sceneEnclosing, true /* rootScene */);
            assert localEnclosing != null;
            final ScrollPaneBooster spb = new ScrollPaneBooster(scrollPane);
            spb.scrollTo(localEnclosing);
        }
    }


    @Override
    public void reveal(FXOMObject targetFxomObject) {
        FXOMObject fxomObject = targetFxomObject;
        // TODO special case if to move to metadata
        while (fxomObject != null) {
            final Object sceneGraphObject = fxomObject.getSceneGraphObject();

            if (sceneGraphObject instanceof Tab) {
                final Tab tab = (Tab) sceneGraphObject;
                final TabPane tabPane = tab.getTabPane();
                assert tabPane != null;
                tabPane.getSelectionModel().select(tab);
            } else if (sceneGraphObject instanceof TitledPane) {
                final TitledPane titledPane = (TitledPane) sceneGraphObject;
                if (titledPane.getParent() instanceof Accordion) {
                    final Accordion accordion = (Accordion) titledPane.getParent();
                    accordion.setExpandedPane(titledPane);
                }
            }

            DesignHierarchyMask mask = new DesignHierarchyMask(fxomObject);
            fxomObject = mask.getParentFXOMObject();
        }
    }

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
     * Returns the topmost FXOMObject at (sceneX, sceneY) but ignoring
     * objects from the exclude set.
     *
     * @param sceneX x coordinate of a scene point
     * @param sceneY y coordinate of a scene point
     * @param excludes null or a set of FXOMObject to be excluded from the pick.
     * @return null or the topmost FXOMObject located at (sceneX, sceneY)
     */
    @Override
    public FXOMObject pick(double sceneX, double sceneY, Set<FXOMObject> excludes) {
        final FXOMObject result;

        if (isContentDisplayable()) {
            final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
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
            FXOMObject startObject = fxomDocument.getFxomRoot().searchWithSceneGraphObject(displayNode);
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
     * Returns the topmost FXOMObject at (sceneX, sceneY) but ignoring
     * objects from the exclude set and starting the search from startObject.
     *
     * @param startObject starting point of the search
     * @param sceneX x coordinate of a scene point
     * @param sceneY y coordinate of a scene point
     * @param excludes null or a set of FXOMObject to be excluded from the pick.
     * @return null or the topmost FXOMObject located at (sceneX, sceneY)
     */
    public FXOMObject pick(
            FXOMObject startObject,
            double sceneX,
            double sceneY,
            Set<FXOMObject> excludes) {

        final FXOMObject result;

        assert isContentDisplayable();
        assert startObject != null;
        assert startObject.getSceneGraphObject() instanceof Node;
        assert excludes != null;
        assert excludes.contains(startObject) == false;

        picker.getExcludes().clear();
        for (FXOMObject exclude : excludes) {
            if (exclude.getSceneGraphObject() instanceof Node) {
                picker.getExcludes().add((Node) exclude.getSceneGraphObject());
            }
        }

        final Node startNode = (Node) startObject.getSceneGraphObject();
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
     * @param sceneX x coordinate of a scene point
     * @param sceneY y coordinate of a scene point
     * @return an FXOMObject that matches (sceneGraphNode, sceneX, sceneY)
     */
    @Override
    public FXOMObject searchWithNode(Node sceneGraphNode, double sceneX, double sceneY) {
       final FXOMObject result;

        final FXOMDocument fxomDocument
                = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        final FXOMObject match
                = fxomDocument.searchWithSceneGraphObject(sceneGraphNode);
         /*
         * Refine the search.
         * With the logic above, a click in a 'tab header' returns the
         * fxom object associated to the 'tab pane'. We would like to get
         * the fxom object associated to the 'tab'. When the pick result is
         * a 'TabPane' we need to refine this result. This refinement logic
         * is available in AbstractDriver.
         */
        if (match != null) {
            result = driver.refinePick(sceneGraphNode, sceneX, sceneY, match);
        } else {
            result = null;
        }

        return result;
    }

    public boolean isTracingEvents() {
        return tracingEvents;
    }


    public void layoutContent(boolean applyCSS) {
        workspaceController.layoutContent(applyCSS);
    }

    @Override
    public void beginInteraction() {
        workspaceController.beginInteraction();
    }

    @Override
    public void endInteraction() {
        workspaceController.endInteraction();
    }


    /*
     * Public which are *private*...
     */

    /**
     * @treatAsPrivate Returns the background object of this content panel.
     * @return the background object of this content panel.
     */
    @Override
    public Pane getWorkspacePane() {
        return workspacePane;
    }


    /**
     * @treatAsPrivate Returns the glass layer container.
     * @return the glass layer container.
     */
    @Override
    public Pane getGlassLayer() {
        return glassLayer;
    }


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


    /**
     * @treatAsPrivate Returns the sub scene holding the user scene graph.
     * @return the sub scene holding the user scene graph.
     */
    @Override
    public SubScene getContentSubScene() {
        return contentSubScene;
    }


    

    /**
     * @treatAsPrivate Returns the hud window controller.
     * @return the hud window controller.
     */
    @Override
    public HudWindowController getHudWindowController() {
        return hudWindowController;
    }

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

    /**
     * @treatAsPrivate
     * Returns true if this content panel is able to display the content ie
     * 1) fxomDocument != null
     * 2) (fxomDocument.getFxomRoot() == null) or fxomDocument.getFxomRoot().isNode()
     * 3) workspaceController.getLayoutException() == null
     *
     * @return true if this content panel is able to display the content
     */
    @Override
    public boolean isContentDisplayable() {
        final boolean result;

        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        if (fxomDocument == null) {
            result = false;
        } else if (fxomDocument.getFxomRoot() == null) {
            result = true;
        } else {
            result = fxomDocument.getDisplayNodeOrSceneGraphRoot() instanceof Node
                    && workspaceController.getLayoutException() == null;
        }

        return result;
    }

    /*
     * AbstractPanelController<TreeView>
     */

    /**
     * @treatAsPrivate fxom document has changed
     * @param fxomDocument the new fxom document
     */
    protected void fxomDocumentDidChange(FXOMDocument fxomDocument) {
        
        // Setup the mode controller
        if (!this.modeManager.hasModeEnabled()) {
            getApi().getApiDoc().getDocumentManager().selectionDidChange()
                .set(new SelectionState(getApi().getApiDoc().getSelection()));
            this.modeManager.enableMode(EditModeController.ID);
        }
        
        if (oldDocument != null) {
            assert oldDocument.getSceneGraphHolder() == this;
            oldDocument.endHoldingSceneGraph();
        }

        if (fxomDocument != null) {
            assert fxomDocument.getSceneGraphHolder() == null;
            fxomDocument.beginHoldingSceneGraph(this);
        }

        final Exception currentLayoutException
                = workspaceController.getLayoutException();
        workspaceController.setFxomDocument(fxomDocument);
        final Exception newLayoutException
                = workspaceController.getLayoutException();
        if ((newLayoutException != null) && (newLayoutException != currentLayoutException)) {
            getApi().getApiDoc().getMessageLogger().logWarningMessage(
                    "log.warning.layout.failed", newLayoutException.getMessage());
        }

        if (fxomDocument != null) {
            fxomDocument.refreshSceneGraph();
        }

        modeManager.fxomDocumentDidChange(oldDocument);

        resetViewport();
    }

    /**
     * @treatAsPrivate job manager revision has changed
     */
    protected void jobManagerRevisionDidChange() {
        modeManager.enableMode(EditModeController.ID);
    }

    /**
     * @treatAsPrivate selection has changed
     */
    protected void editorSelectionDidChange() {
        modeManager.editorSelectionDidChange();
    }

    /*
     * AbstractFxmlPanelController
     */

    /**
     * @treatAsPrivate controller did load fxml
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert scrollPane != null;
        assert workspacePane != null;
        assert workspacePane.getPrefWidth() == Region.USE_COMPUTED_SIZE;
        assert workspacePane.getPrefHeight() == Region.USE_COMPUTED_SIZE;
        assert workspacePane.getMaxWidth() == Double.MAX_VALUE;
        assert workspacePane.getMaxHeight() == Double.MAX_VALUE;
        assert workspacePane.getMinWidth() == Region.USE_PREF_SIZE;
        assert workspacePane.getMinHeight() == Region.USE_PREF_SIZE;
        assert extensionRect != null;
        assert extensionRect.getLayoutX() == 0.0;
        assert extensionRect.getLayoutY() == 0.0;
        assert backgroundPane != null;
        assert backgroundPane.getLayoutX() == 0.0;
        assert backgroundPane.getLayoutY() == 0.0;
        assert backgroundPane.getMaxWidth() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMaxHeight() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMinWidth() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMinHeight() == Region.USE_PREF_SIZE;
        assert scalingGroup != null;
        assert contentSubScene != null;
        assert contentSubScene.getLayoutX() == 0.0;
        assert contentSubScene.getLayoutY() == 0.0;
        assert contentSubScene.getParent() == scalingGroup;
        assert contentGroup == contentSubScene.getRoot();
        assert contentGroup.getLayoutX() == 0.0;
        assert contentGroup.getLayoutY() == 0.0;
        assert glassLayer != null;
        assert glassLayer.isMouseTransparent() == false;
        assert glassLayer.isFocusTraversable();
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

        // Remove fake content used to help design
        backgroundPane.setText(""); //NOCHECK

        // Setup our workspace controller
        workspaceController.panelControllerDidLoadFxml(
                scrollPane,
                scalingGroup,
                contentSubScene,
                contentGroup,
                backgroundPane,
                extensionRect);
//        themeDidChange(); // To setup initial value of WorkspaceController.themeStyleSheet

        resetViewport();
        setupEventTracingFilter();

        // Setup the context menu
        final ContextMenu contextMenuController = getApi().getApiDoc().getContextMenu();
        scrollPane.setContextMenu(contextMenuController.getContextMenu());

        // Setup default workspace background
        //setWorkspaceBackground(ImageUtils.getImage(getDefaultWorkspaceBackgroundURL()));
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
        // Scene graph has been reconstructed so:
        //  - new scene graph must replace the old one below contentHook
        //  - mode controller must be informed so that it can updates handles
        workspaceController.sceneGraphDidChange();
        modeManager.fxomDocumentDidRefreshSceneGraph();
   }

    private void resetViewport() {
        if (scrollPane != null) {
            scrollPane.setHvalue(0.5);
            scrollPane.setVvalue(0.5);
        }
    }

    private void setupEventTracingFilter() {
        if (glassLayer != null) {
            if (tracingEvents) {
                glassLayer.addEventFilter(InputEvent.ANY, eventTracingFilter);
            } else {
                glassLayer.removeEventFilter(InputEvent.ANY, eventTracingFilter);
            }
        }
    }

    private void traceEvent(Event e) {
        final StringBuilder sb = new StringBuilder();

        sb.append("ContentPanelController: eventType="); //NOCHECK
        sb.append(e.getEventType());
        sb.append(", target="); //NOCHECK
        sb.append(e.getTarget());
        if (e instanceof KeyEvent) {
            final KeyEvent ke = (KeyEvent) e;
            sb.append(", keyCode="); //NOCHECK
            sb.append(ke.getCode());
        }
        
        logger.info(sb.toString());
    }

    private final EventHandler<Event> eventTracingFilter
            = e -> traceEvent(e);


    private void dragSourceDidChange() {
        modeManager.enableMode(EditModeController.ID);
    }


    private void dropTargetDidChange() {
        modeManager.dropTargetDidChange();
    }

    /*
     * Private (outline layer)
     */

    private void beginShowingOutlines() {
        assert contentGroup.isVisible();

        contentGroup.setVisible(false);
        modeManager.enableMode(EditModeController.class);
        modeManager.getEnabledMode().getLayer(Outline.class).update();
    }

    private void endShowingOutlines() {
        assert contentGroup.isVisible() == false;
        modeManager.enableMode(EditModeController.class);
        modeManager.getEnabledMode().getLayer(Outline.class).disable();
        contentGroup.setVisible(true);
    }
    
    @Override
    public Editor getEditorController() {
        return editorController;
    }

    @Override
    public ModeManager getModeManager() {
        return modeManager;
    }

    
}
