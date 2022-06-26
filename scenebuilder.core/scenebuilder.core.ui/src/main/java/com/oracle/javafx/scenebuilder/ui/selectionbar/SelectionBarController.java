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
package com.oracle.javafx.scenebuilder.ui.selectionbar;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.editor.selection.SelectionGroup;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlPanelController;
import com.oracle.javafx.scenebuilder.api.ui.selbar.SelectionBarContentFactory;
import com.oracle.javafx.scenebuilder.api.ui.selbar.SelectionBarContentFactory.BarItem;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;
import com.oracle.javafx.scenebuilder.om.api.OMObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class SelectionBarController extends AbstractFxmlPanelController {

    private static final Logger logger = LoggerFactory.getLogger(SelectionBarController.class);

    private final DocumentManager documentManager;
    private final Selection<OMDocument<OMObject>, OMObject> selection;
    private final SelectionBarContentFactory barContentFactory;

    @FXML
    private HBox pathBox;

    private final Image selectionChevronImage;

    private final boolean enabled;

    public SelectionBarController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            JobManager jobManager,
            Selection<OMDocument<OMObject>, OMObject> selection,
            @Autowired(required = false) SelectionBarContentFactory barContentFactory) {
        super(scenebuilderManager, documentManager, SelectionBarController.class.getResource("SelectionBar.fxml"), I18N.getBundle());
        this.documentManager = documentManager;
        this.selection = selection;
        this.barContentFactory = barContentFactory;
        this.enabled = barContentFactory != null;

        // Initialize selection chevron image
        final URL selectionChevronURL = SelectionBarController.class.getResource("selection-chevron.png");
        assert selectionChevronURL != null;
        selectionChevronImage = new Image(selectionChevronURL.toExternalForm());

        if (enabled) {
            documentManager.omDocument().subscribe(fd -> fxomDocumentDidChange(fd));
            documentManager.sceneGraphRevisionDidChange().subscribe(c -> sceneGraphRevisionDidChange());
            documentManager.selectionDidChange().subscribe(c -> editorSelectionDidChange());
            jobManager.revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());
        } else {
            logger.warn("No instance of {} provided, selection bar is disabled!", SelectionBarContentFactory.class);
        }

    }

    protected void fxomDocumentDidChange(OMDocument<?> oldDocument) {
        if (pathBox != null) {
            updateSelectionBar();
        }
    }

    protected void sceneGraphRevisionDidChange() {
        if (pathBox != null) {
            updateSelectionBar();
        }
    }

    protected void jobManagerRevisionDidChange() {
        sceneGraphRevisionDidChange();
    }

    protected void editorSelectionDidChange() {
        if (pathBox != null) {
            updateSelectionBar();
        }
    }

    /*
     * AbstractFxmlPanelController
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert pathBox != null;

        // Update
        updateSelectionBar();
    }

    /*
     * Private
     */
    private void updateSelectionBar() {
        pathBox.getChildren().clear();

        if (selection.isEmpty()) {
            pathBox.getChildren().add(new Label(I18N.getString("selectionbar.no.selected")));
        } else {
            final SelectionGroup<?, ?> osg = selection.getGroup();
            assert osg.getItems().isEmpty() == false;
            OMObject fxomObject = osg.getItems().iterator().next();

            LinkedList<OMObject> path = barContentFactory.buildOrderedPath(fxomObject);

            if (path != null) {

                Iterator<OMObject> it = path.iterator();

                while (it.hasNext()) {
                    OMObject pathItem = it.next();
                    BarItem item = barContentFactory.buildItem(pathItem);
                    final String entryText = item.getLabel();
                    final Hyperlink boxItem = new Hyperlink();
                    boxItem.setText(entryText);
                    final Node graphic;
                    // Do not display warning icon anymore :
                    // See DTL-6535 : Should we show warnings in the selection bar ?
//                    final List<ErrorReportEntry> entries = getErrorReportEntries(fxomObject, recursive);
//                    if (entries != null) {
//                        assert !entries.isEmpty();
//                        final ImageView classNameImageView
//                                = new ImageView(mask.getClassNameIcon());
//                        final ImageView warningBadgeImageView
//                                = new ImageView(warningBadgeImage);
//                        final StackPane iconsStack = new StackPane();
//                        iconsStack.getChildren().setAll(classNameImageView, warningBadgeImageView);
//                        // Update tooltip with the first entry
//                        final Tooltip iconsTooltip = new Tooltip(entries.get(0).toString());
//
//                        // We use a label to set a tooltip over the node icon
//                        // (StackPane does not allow to set tooltips)
//                        graphic = new Label();
//                        ((Label) graphic).setGraphic(iconsStack);
//                        ((Label) graphic).setTooltip(iconsTooltip);
//                    } else {
                        graphic = item.getGraphic();
//                    }
                    boxItem.setGraphic(graphic);
                    boxItem.setFocusTraversable(false);
                    boxItem.setUserData(fxomObject);
                    boxItem.setOnAction(hyperlinkHandler);
                    pathBox.getChildren().add(boxItem);

                 // Add selection chevron if needed
                    if (it.hasNext()) {
                        // We cannot share the image view to avoid
                        // Children: duplicate children added
                        ImageView img = new ImageView(selectionChevronImage);
                        StackPane sp = new StackPane();
                        sp.getChildren().add(img);
                        sp.setMinWidth(selectionChevronImage.getWidth());
                        pathBox.getChildren().add(sp);
                    }
                }
            } else {
                pathBox.getChildren().add(new Label(I18N.getString("selectionbar.not.object")));
            }
        }
    }

    private final EventHandler<ActionEvent> hyperlinkHandler = t -> {
        assert t.getSource() instanceof Hyperlink;
        final Hyperlink hyperlink = (Hyperlink) t.getSource();
        assert hyperlink.getUserData() instanceof FXOMObject;
        handleSelect((FXOMObject) hyperlink.getUserData());
        hyperlink.setVisited(false);
    };

    private void handleSelect(OMObject fxomObject) {
        assert fxomObject.getFxomDocument() == documentManager.omDocument().get();
        selection.select(fxomObject);
    }

//    private List<ErrorReportEntry> getErrorReportEntries(FXOMObject fxomObject, boolean recursive) {
//        assert fxomObject != null;
//        final ErrorReport errorReport = getEditorController().getErrorReport();
//        return errorReport.query(fxomObject, recursive);
//    }
}
