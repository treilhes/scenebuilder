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
package com.oracle.javafx.scenebuilder.debugmenu.view;

import java.util.List;
import java.util.stream.Stream;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.preferences.ManagedDocumentPreference;
import com.gluonhq.jfxapps.core.api.preferences.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preferences.Preference;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 */
@ApplicationInstanceSingleton
@ViewAttachment(name = DebugPreferencesView.VIEW_NAME, id = DebugPreferencesView.VIEW_ID, debug = true)
public class DebugPreferencesView extends AbstractFxmlViewController {

    public final static String VIEW_ID = "03c79b0c-0366-4238-b82c-ce901048e91a";
    public final static String VIEW_NAME = "debug pref";

    private final List<ManagedGlobalPreference> globalPreferences;
    private final List<ManagedDocumentPreference> documentPreferences;

    @FXML
    private VBox vboxPreferences;


    /**
     * @param scenebuilderManager
     * @param documentManager
     * @param viewMenuController
     * @param fxmlURL
     * @param resources
     */
    // @formatter:off
    public DebugPreferencesView(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenuController,
            List<ManagedGlobalPreference> globalPreferences,
            List<ManagedDocumentPreference> documentPreferences) {
     // @formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenuController, DebugPreferencesView.class.getResource("DebugPreferences.fxml"));
        this.globalPreferences = globalPreferences;
        this.documentPreferences = documentPreferences;
    }


    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        Stream.concat(globalPreferences.stream(), documentPreferences.stream()).forEach(preference -> {
            Preference<?> p = (Preference<?>)preference;
            String name = p.getName();

            HBox line = new HBox();
            line.getChildren().add(new Label(name + "  :  "));

            Label valueLabel = new Label();

            StringBinding binding = Bindings.createStringBinding(() -> p.getValue() == null ? "null" : p.getValue().toString(), p.getObservableValue());
            valueLabel.textProperty().bind(binding);
            line.getChildren().add(valueLabel);

            vboxPreferences.getChildren().add(line);
        });

    }


    @Override
    public void onShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

}
