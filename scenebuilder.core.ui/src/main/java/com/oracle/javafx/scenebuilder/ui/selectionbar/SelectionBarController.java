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
package com.oracle.javafx.scenebuilder.ui.selectionbar;

import java.net.URL;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlPanelController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class SelectionBarController extends AbstractFxmlPanelController {

    @FXML
    private HBox pathBox;

    private final Image selectionChevronImage;

    public SelectionBarController(
            Api api) {
        super(api, SelectionBarController.class.getResource("SelectionBar.fxml"), I18N.getBundle());

        // Initialize selection chevron image
        final URL selectionChevronURL = SelectionBarController.class.getResource("selection-chevron.png");
        assert selectionChevronURL != null;
        selectionChevronImage = new Image(selectionChevronURL.toExternalForm());
        
        api.getApiDoc().getDocumentManager().fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));
        api.getApiDoc().getDocumentManager().sceneGraphRevisionDidChange().subscribe(c -> sceneGraphRevisionDidChange());
        api.getApiDoc().getDocumentManager().selectionDidChange().subscribe(c -> editorSelectionDidChange());
        api.getApiDoc().getJobManager().revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());
    }

    protected void fxomDocumentDidChange(FXOMDocument oldDocument) {
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
        final Selection selection = getApi().getApiDoc().getSelection();

        pathBox.getChildren().clear();

        if (selection.isEmpty()) {
            pathBox.getChildren().add(new Label(I18N.getString("selectionbar.no.selected")));
        } else {
            if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
                assert osg.getItems().isEmpty() == false;

                FXOMObject fxomObject = osg.getItems().iterator().next();
                // Recursive error report for the leaf object only
//                boolean recursive = true;
                while (fxomObject != null) {
                    final DesignHierarchyMask mask = new DesignHierarchyMask(fxomObject);
                    final String entryText = makeEntryText(mask);
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
                        graphic = new ImageView(mask.getClassNameIcon());
//                    }
                    boxItem.setGraphic(graphic);
                    boxItem.setFocusTraversable(false);
                    boxItem.setUserData(fxomObject);
                    boxItem.setOnAction(hyperlinkHandler);
                    pathBox.getChildren().add(0, boxItem);

                    // The last 2 box item should never show ellipsis
                    if (pathBox.getChildren().size() <= 3) {
                        boxItem.setMinWidth(Region.USE_PREF_SIZE);
                        HBox.setHgrow(boxItem, Priority.ALWAYS);
                    } else {
                        boxItem.setMinWidth(graphic.getBoundsInLocal().getWidth());
                    }

                    fxomObject = mask.getParentFXOMObject();
                    // Add selection chevron if needed
                    if (fxomObject != null) {
                        // We cannot share the image view to avoid
                        // Children: duplicate children added
                        ImageView img = new ImageView(selectionChevronImage);
                        StackPane sp = new StackPane();
                        sp.getChildren().add(img);
                        sp.setMinWidth(selectionChevronImage.getWidth());
                        pathBox.getChildren().add(0, sp);
                    }
                    // Non recursive error report for the parent
//                    recursive = false;
                }

            } else {
                pathBox.getChildren().add(new Label(I18N.getString("selectionbar.not.object")));
            }
        }
    }

    private String makeEntryText(DesignHierarchyMask mask) {
        final StringBuilder result = new StringBuilder();

        result.append(mask.getClassNameInfo());
        final String description = mask.getSingleLineDescription();
        if (description != null) {
            result.append(" : "); //NOCHECK
            result.append(description);
        }
        return result.toString();
    }

    private final EventHandler<ActionEvent> hyperlinkHandler = t -> {
        assert t.getSource() instanceof Hyperlink;
        final Hyperlink hyperlink = (Hyperlink) t.getSource();
        assert hyperlink.getUserData() instanceof FXOMObject;
        handleSelect((FXOMObject) hyperlink.getUserData());
        hyperlink.setVisited(false);
    };

    private void handleSelect(FXOMObject fxomObject) {
        final Selection selection = getApi().getApiDoc().getSelection();

        assert fxomObject.getFxomDocument() == getApi().getApiDoc().getDocumentManager().fxomDocument().get();

        selection.select(fxomObject);
    }

//    private List<ErrorReportEntry> getErrorReportEntries(FXOMObject fxomObject, boolean recursive) {
//        assert fxomObject != null;
//        final ErrorReport errorReport = getEditorController().getErrorReport();
//        return errorReport.query(fxomObject, recursive);
//    }
}