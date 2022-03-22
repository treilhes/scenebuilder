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
package com.oracle.javafx.scenebuilder.prefedit.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

/**
 * Preferences window controller.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class PreferencesWindowController extends AbstractFxmlWindowController {

    private static final int PREFERENCE_MAX_HEIGHT = 700;

    @FXML
    private TabPane prefTabPane;
    @FXML
    private GridPane globalGrid;
    @FXML
    private Button globalResetButton;

    @FXML
    private GridPane documentGrid;
    @FXML
    private Button documentResetButton;

    private final SceneBuilderWindow ownerWindow;

    private List<UserPreference<?>> globalPreferences;
    private List<UserPreference<?>> documentPreferences;

    public PreferencesWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            DocumentWindow documentWindowController,
            List<ManagedGlobalPreference> globalPreferences,
            List<ManagedDocumentPreference> documentPreferences) {
        super(sceneBuilderManager, iconSetting, PreferencesWindowController.class.getResource("Preferences.fxml"),
                I18N.getBundle(), documentWindowController);
        this.ownerWindow = documentWindowController;

        this.globalPreferences = globalPreferences.stream()
                .filter(p -> UserPreference.class.isAssignableFrom(p.getClass())).map(p -> (UserPreference<?>) p)
                .filter(p -> p.getGroup() != null && p.getLabelI18NKey() != null).collect(Collectors.toList());
        this.documentPreferences = documentPreferences.stream()
                .filter(p -> UserPreference.class.isAssignableFrom(p.getClass())).map(p -> (UserPreference<?>) p)
                .filter(p -> p.getGroup() != null && p.getLabelI18NKey() != null).collect(Collectors.toList());
    }

    @FXML
    public void initialize() {
        prefTabPane.setMaxHeight(PREFERENCE_MAX_HEIGHT);
        populatePreferenceGrid(globalGrid, globalPreferences, globalResetButton);
        populatePreferenceGrid(documentGrid, documentPreferences, documentResetButton);
    }

    private static void populatePreferenceGrid(GridPane target, List<UserPreference<?>> preferences,
            Button resetButton) {
        Map<PreferenceGroup, List<UserPreference<?>>> preferencesMap = preferences.stream()
                .collect(Collectors.groupingBy(UserPreference::getGroup));

        List<PreferenceGroup> sortedKeys = new ArrayList<>(preferencesMap.keySet());

        Collections.sort(sortedKeys, (g1, g2) -> g1.getOrderKey().compareTo(g2.getOrderKey()));

        sortedKeys.stream().forEach(g -> {
            List<UserPreference<?>> preferenceList = preferencesMap.get(g);
            Collections.sort(preferenceList, (p1, p2) -> p1.getOrderKey().compareTo(p2.getOrderKey()));

            preferenceList.stream().forEach(p -> {
                if (p.getLabelI18NKey() != null) {
                    int rowIndex = target.getRowCount();
                    Label label = new Label(I18N.getString(p.getLabelI18NKey()));
                    Parent editor = p.getEditor();
                    target.addRow(rowIndex, label);
                    target.addRow(rowIndex, editor);
                    GridPane.setHgrow(label, Priority.ALWAYS);
                    GridPane.setHgrow(editor, Priority.ALWAYS);
                }
            });

            Separator separator = new Separator();
            target.addRow(target.getRowCount(), separator);
            GridPane.setColumnSpan(separator, 2);
            GridPane.setHgrow(separator, Priority.ALWAYS);
        });

    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("prefs.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
        getStage().initOwner(ownerWindow.getStage());
        getStage().setResizable(false);
    }

    @Override
    public void onCloseRequest() {
        super.closeWindow();
    }

    @Override
    public void onFocus() {
    }

    @FXML
    void resetToDocumentDefaultAction(ActionEvent event) {
        documentPreferences.forEach(u -> u.reset());
    }

    @FXML
    void resetToDefaultAction(ActionEvent event) {
        globalPreferences.forEach(u -> u.reset());
    }

}
