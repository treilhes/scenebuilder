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
package com.oracle.javafx.scenebuilder.api.dock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlController;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class SearchController extends AbstractFxmlController implements ViewSearch {

    private final static Logger logger = LoggerFactory.getLogger(SearchController.class);

    @FXML
    private TextField searchField;

    // This StackPane contains the searchImage.
    @FXML
    private StackPane searchIcon;

    public SearchController(@Autowired Api api) {
        super(api, SearchController.class.getResource("Search.fxml"));
    }

    @FXML
    protected void initialize() {
        logger.info("SearchController initialize {} {}", this.getClass().getSimpleName(), this);
    }

    @Autowired
    private void initialize2(@Autowired Editor test) {
        logger.info("SearchController initialize2 {} {}", this.getClass().getSimpleName(), this);
    }

    @Override
    public StringProperty textProperty() {
        return searchField.textProperty();
    }

    @Override
    public void requestFocus() {
        searchField.requestFocus();
    }

    @Override
    public void controllerDidLoadFxml() {
        if (searchField.getLength() == 0) {
            searchIcon.getStyleClass().add("search-magnifying-glass"); // NOCHECK
        }

        // For SQE tests
        searchField.setId("Search Text"); // NOCHECK

        searchField.textProperty().addListener((ChangeListener<String>) (ov, oldStr, newStr) -> {
            if (newStr.isEmpty()) {
                searchIcon.getStyleClass().clear();
                searchIcon.getStyleClass().add("search-magnifying-glass"); // NOCHECK
            } else {
                searchIcon.getStyleClass().clear();
                searchIcon.getStyleClass().add("search-clear"); // NOCHECK
            }
        });

        searchField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
            }
        });

        // Select all text when this editor is selected
        searchField.setOnMousePressed(event -> searchField.selectAll());
        searchField.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                searchField.selectAll();
            }
        }));

        searchIcon.setOnMouseClicked(t -> searchField.clear());
    }

}