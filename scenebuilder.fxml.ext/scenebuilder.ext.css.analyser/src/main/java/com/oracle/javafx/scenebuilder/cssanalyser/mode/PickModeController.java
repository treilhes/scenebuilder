/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.cssanalyser.mode;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.content.mode.AbstractModeController;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.collector.SceneGraphCollector;
import com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 *
 */

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class PickModeController extends AbstractModeController {

    private final Driver driver;
    private final FxmlDocumentManager documentManager;
    private final Selection selection;
    //private HitNodeChrome hitNodeChrome;

    public PickModeController(
            @Autowired Driver driver,
            @Autowired FxmlDocumentManager documentManager,
            @Autowired Selection selection,
    		@Autowired @Lazy Content contentPanelController) {
        super(contentPanelController);
        this.driver = driver;
        this.documentManager = documentManager;
        this.selection = selection;

        newLayer(HitNodeChrome.class, false, selection,
                // object selection
                s -> s.getGroup().getItems(),
                // pring creation
                fxomObject -> {
                    // TODO check consequence s of removed if ((hitNodeChrome == null)
                    // || (hitNodeChrome.getFxomObject() != selection.getHitItem())
                    // || (hitNodeChrome.getHitNode() != selection.getCheckedHitNode())) {
                    if ((selection.getHitItem() != null) && (selection.getCheckedHitNode() != null)){
                        return makeHitNodeChrome(selection.getHitItem(), selection.getCheckedHitNode());
                    } else {
                        return null;
                    }
                });
    }


    @Override
    public Object getModeId() {
        return PickModeController.class;
    }

    /*
     * AbstractModeController
     */

    @Override
    public void willResignActive(AbstractModeController nextModeController) {
        content.getGlassLayer().setCursor(Cursor.DEFAULT);
        stopListeningToInputEvents();
        clearLayers();
    }

    @Override
    public void didBecomeActive(AbstractModeController previousModeController) {
        assert content.getGlassLayer() != null;
        getLayers().forEach(l -> l.enable());
        getLayer(HitNodeChrome.class).update();
        startListeningToInputEvents();
        content.getGlassLayer().setCursor(ImageUtils.getCSSCursor());
    }

    @Override
    public void editorSelectionDidChange() {
        getLayer(HitNodeChrome.class).update();
    }

    @Override
    public void fxomDocumentDidChange(FXOMDocument oldDocument) {
        // Same logic as when the scene graph is changed
        fxomDocumentDidRefreshSceneGraph();
    }

    @Override
    public void fxomDocumentDidRefreshSceneGraph() {
        getLayer(HitNodeChrome.class).update();
    }

    @Override
    public void dropTargetDidChange() {
        // Should not be invoked : if drag gesture starts, editor controller
        // will switch to EditModeController.
        assert false;
    }


    /*
     * Private
     */


    private void startListeningToInputEvents() {
        final Node glassLayer = content.getGlassLayer();
        assert glassLayer.getOnMousePressed() == null;

        glassLayer.setOnMousePressed(mousePressedOnGlassLayerListener);
    }

    private void stopListeningToInputEvents() {
        final Node glassLayer = content.getGlassLayer();
        glassLayer.setOnMousePressed(null);
    }

    private final EventHandler<MouseEvent> mousePressedOnGlassLayerListener
            = e -> mousePressedOnGlassLayer(e);


    private void mousePressedOnGlassLayer(MouseEvent e) {

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        final FXOMObject hitObject;
        final Node hitNode;
        if ((fxomDocument == null) || (fxomDocument.getFxomRoot() == null)) {
            hitObject = null;
            hitNode = null;
        } else {
            final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
            final Object sceneGraphRoot = fxomRoot.getSceneGraphObject();
            if (sceneGraphRoot instanceof Node) {
                hitNode = Deprecation.pick((Node)sceneGraphRoot, e.getSceneX(), e.getSceneY());
                FXOMObject fxomObject = null;
                Node node = hitNode;
                while ((fxomObject == null) && (node != null)) {
                    fxomObject = fxomRoot.collect(SceneGraphCollector.findSceneGraphObject(node)).get();
                    node = node.getParent();
                }
                hitObject = fxomObject;
            } else {
                hitObject = null;
                hitNode = null;
            }
        }

        if (hitObject == null) {
            selection.clear();
        } else {
            if (selection.isSelected(hitObject)) {
                //assert selection.getGroup() instanceof ObjectSelectionGroup;
                selection.updateHitObject(hitObject, hitNode);
            } else {
                selection.select(hitObject, hitNode);
            }
        }
    }

    private HitNodeChrome makeHitNodeChrome(FXOMObject hitItem, Node hitNode) {
        final HitNodeChrome result;

        assert hitItem != null;

        /*
         * In some cases, we cannot make a chrome for some hitObject
         *
         *  MenuButton          <= OK
         *      CustomMenuItem  <= KO because MenuItem are not displayable (case #1)
         *          CheckBox    <= KO because this CheckBox is in a separate scene (Case #2)
         */

        if (driver == null) {
            // Case #1 above
            result = null;
        } else {
            final FXOMObject closestNodeObject = hitItem.getClosestNode();
            if (closestNodeObject == null) {
                // Document content is not displayable in content panel
                result = null;
            } else {
                assert closestNodeObject.getSceneGraphObject() instanceof Node;
                final Node closestNode = (Node)closestNodeObject.getSceneGraphObject();
                if (closestNode.getScene() == content.getRoot().getScene()) {
                    result = new HitNodeChrome(content, documentManager, hitNode);
                    result.setFxomObject(hitItem);
                    result.initialize();
                } else {
                    // Case #2 above
                    result = null;
                }
            }
        }

        return result;
    }

}
