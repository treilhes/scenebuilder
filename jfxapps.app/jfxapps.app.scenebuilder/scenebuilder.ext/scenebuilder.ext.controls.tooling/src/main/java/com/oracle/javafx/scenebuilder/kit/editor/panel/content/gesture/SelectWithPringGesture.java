/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseDragGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;

import javafx.scene.input.MouseEvent;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SelectWithPringGesture extends AbstractMouseDragGesture {

    private final Selection selection;
    private final DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory;
    private FXOMInstance fxomInstance;

    protected SelectWithPringGesture(
            Content contentPanelController,
            Selection selection,
            DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(contentPanelController);
        this.selection = selection;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    private void setupGestureParameters(FXOMInstance fxomInstance) {
        this.fxomInstance = fxomInstance;
    }

    /*
     * AbstractMouseDragGesture
     */

    @Override
    protected void mousePressed(MouseEvent e) {
        selection.select(objectSelectionGroupFactory.getGroup(fxomInstance));

        /*
         * This selection operation will callback EditModeController
         * which will remove the pring where mouse has been pressed.
         * Thus mouseExited() will be called. But not mouseDragDetected()
         * neither mouseReleased().
         */
    }

    @Override
    protected void mouseDragDetected(MouseEvent e) {
        // Should not be called : see comment in mousePressed().
        assert false;
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        // Should not be called : see comment in mousePressed().
        assert false;
    }

    @Override
    protected void mouseExited(MouseEvent e) {
        // Mouse has exited pring because it has been removed from the
        // scene graph by the selection operation in mousePressed().

//        final Selection selection
//                = contentPanelController.getEditorController().getSelection();
//
//        if (selection.getAncestor() != null) {
//
//            assert selection.isSelected(fxomInstance);
//            assert selection.getGroup() instanceof ObjectSelectionGroup;
//
//            final ObjectSelectionGroup
//                    osg = (ObjectSelectionGroup) selection.getGroup();
//
//            assert osg.hasSingleParent();
//
//            final EditorController editorController
//                    = contentPanelController.getEditorController();
//            final DocumentDragSource dragSource
//                    = new DocumentDragSource(osg.getItems());
//
//            final Dragboard db
//                    = contentPanelController.getGlassLayer().startDragAndDrop(TransferMode.ANY);
//            db.setContent(dragSource.makeClipboardContent());
//            db.setDragView(dragSource.makeDragView());
//
////                assert editorController.getDragSource() == null;
//            editorController.setDragSource(dragSource);
//        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<SelectWithPringGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public SelectWithPringGesture getGesture(FXOMInstance fxomInstance) {
            return create(SelectWithPringGesture.class, g -> g.setupGestureParameters(fxomInstance));
        }
    }
}
