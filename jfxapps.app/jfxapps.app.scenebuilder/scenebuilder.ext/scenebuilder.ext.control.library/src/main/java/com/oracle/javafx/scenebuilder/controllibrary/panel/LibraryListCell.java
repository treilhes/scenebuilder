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
package com.oracle.javafx.scenebuilder.controllibrary.panel;

import java.net.URL;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.library.LibraryItem;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.controllibrary.action.InsertControlAction;
import com.oracle.javafx.scenebuilder.controllibrary.controller.LibraryController;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.LibraryItemImpl;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * ListCell for the Library panel.
 * Used to dynamically construct items and their graphic, as well as set the cursor.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class LibraryListCell extends ListCell<LibraryListItem> {
    private final LibraryController libraryController;

    private final HBox graphic = new HBox();
    private final ImageView iconImageView = new ImageView();
    private final Label classNameLabel = new Label();
    private final Label qualifierLabel = new Label();
    private final Label sectionLabel = new Label();

    private final ActionFactory actionFactory;


    public LibraryListCell(
            final LibraryController libraryController,
            final ActionFactory actionFactory) {
        super();
        this.libraryController = libraryController;
        this.actionFactory = actionFactory;

        graphic.getStyleClass().add("list-cell-graphic"); //NOCHECK
        classNameLabel.getStyleClass().add("library-classname-label"); //NOCHECK
        qualifierLabel.getStyleClass().add("library-qualifier-label"); //NOCHECK
        sectionLabel.getStyleClass().add("library-section-label"); //NOCHECK

        graphic.getChildren().add(iconImageView);
        graphic.getChildren().add(classNameLabel);
        graphic.getChildren().add(qualifierLabel);
        graphic.getChildren().add(sectionLabel);

        HBox.setHgrow(sectionLabel, Priority.ALWAYS);
        sectionLabel.setMaxWidth(Double.MAX_VALUE);

        final EventHandler<MouseEvent> mouseEventHandler = e -> handleMouseEvent(e);
        // Mouse events
        this.addEventHandler(MouseEvent.ANY, mouseEventHandler);

        setOnDragDetected(t -> libraryController.performDragDetected(this));
    }

    @Override
    public void updateItem(LibraryListItem item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);

        if (!empty && item != null) {
            updateLayout(item);
            if (item.getLibItem() != null) {
                // A qualifier needed to discriminate items is kept in the ID:
                // this applies to orientation as well as empty qualifiers.
                // FX8 qualifier is not kept as there's no ambiguity there.
                String id = item.getLibItem().getName();

                // If QE were about to test a localized version the ID should
                // remain unchanged.
                String qualifier = item.getLibItem().getQualifier().getLabel();
                if (qualifier != null) {
                    id += String.format(" (%s)", item.getLibItem().getQualifier().getLabel());
                }
                graphic.setId(id); // for QE
            }

            setGraphic(graphic);
        } else {
            setGraphic(null);
        }
    }

    private Cursor getOpenHandCursor() {
        // DTL-6477
        if (JfxAppsPlatform.IS_WINDOWS) {
            return ImageUtils.getOpenHandCursor();
        } else {
            return Cursor.OPEN_HAND;
        }
    }

    private Cursor getClosedHandCursor() {
        // DTL-6477
        if (JfxAppsPlatform.IS_WINDOWS) {
            return ImageUtils.getClosedHandCursor();
        } else {
            return Cursor.CLOSED_HAND;
        }
    }

    private void handleMouseEvent(MouseEvent me) {
        // Handle cursor
        final Scene scene = getScene();

        if (scene == null) {
            return;
        }

        // When another window is focused (just like the preview window),
        // we use default cursor
        if (scene.getWindow() != null && !scene.getWindow().isFocused()) {
            setCursor(Cursor.DEFAULT);
            return;
        }

        final LibraryListItem listItem = getItem();
        LibraryItem item = null;

        if (listItem != null) {
            item = listItem.getLibItem();
        }

        boolean isSection = false;
        if (listItem != null && listItem.getSectionName() != null) {
            isSection = true;
        }

        if (me.getEventType() == MouseEvent.MOUSE_ENTERED) {
            if (isEmpty() || isSection) {
                setCursor(Cursor.DEFAULT);
            } else {
                setCursor(getOpenHandCursor());
            }
        } else if (me.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (isEmpty() || isSection) {
                setCursor(Cursor.DEFAULT);
            } else {
                setCursor(getClosedHandCursor());
            }
        } else if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (isEmpty() || isSection) {
                setCursor(Cursor.DEFAULT);
            } else {
                setCursor(getOpenHandCursor());
            }
        } else if (me.getEventType() == MouseEvent.MOUSE_EXITED) {
            setCursor(Cursor.DEFAULT);
        } else if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
             // On double click ask for addition of the drag able item on Content
            if (me.getClickCount() == 2 && me.getButton() == MouseButton.PRIMARY) {
                if (!isEmpty() && !isSection && item != null) {
                    InsertControlAction action = actionFactory.create(InsertControlAction.class);
                    action.setLibraryItem(item);
                    action.checkAndPerform();
                }
            }
        }
    }

    public static String makeQualifierLabel(Qualifier qualifier) {
        String label = qualifier.getLabel();
        String description = qualifier.getDescription();

        if (label == null && description == null) {
            return "";
        }

        String output = "";

        if (label != null && !"default".equals(label.toLowerCase())) {
            output += "(" + I18N.getStringOrDefault(String.format("label.qualifier.%s",label), label) + ")";
        }

        if (description != null && !description.isBlank()) {
            output += output.length() == 0 ? "" : " ";
            output += "(" + I18N.getStringOrDefault(String.format("description.qualifier.%s",description), description) + ")";
        }
        return output;
    }


    private void updateLayout(LibraryListItem listItem) {
        assert listItem != null;

        if (listItem.getLibItem() != null) {
            final LibraryItemImpl item = listItem.getLibItem();
            // The classname shall be space character free (it is an API name).
            // If there is a space character then it means a qualifier comes
            // right after. In the case there is several qualifiers in a row
            // only the latest one is taken as is, others are kept with class
            // name.
            String classname = item.getName();
            iconImageView.setManaged(true);
            classNameLabel.setManaged(true);
            qualifierLabel.setManaged(true);
            sectionLabel.setManaged(false);
            iconImageView.setVisible(true);
            classNameLabel.setVisible(true);
            qualifierLabel.setVisible(true);
            sectionLabel.setVisible(false);
            classNameLabel.setText(classname);
            qualifierLabel.setText(makeQualifierLabel(item.getQualifier()));
            // getIconURL can return null, this is deliberate.
            URL iconURL = item.getIconURL();
            iconImageView.setImage(new Image(iconURL.toExternalForm()));
        } else if (listItem.getSectionName() != null) {
            iconImageView.setManaged(false);
            classNameLabel.setManaged(false);
            qualifierLabel.setManaged(false);
            sectionLabel.setManaged(true);
            iconImageView.setVisible(false);
            classNameLabel.setVisible(false);
            qualifierLabel.setVisible(false);
            sectionLabel.setVisible(true);
            sectionLabel.setText(listItem.getSectionName());
        }
    }

}
