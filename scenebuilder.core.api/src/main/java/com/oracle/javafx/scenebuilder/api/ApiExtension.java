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
package com.oracle.javafx.scenebuilder.api;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ExtendedAction;
import com.oracle.javafx.scenebuilder.api.content.ModeManagerController;
import com.oracle.javafx.scenebuilder.api.control.driver.DriverExtensionRegistry;
import com.oracle.javafx.scenebuilder.api.control.driver.GenericDriver;
import com.oracle.javafx.scenebuilder.api.control.inlineedit.SimilarInlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.pickrefiner.NoPickRefiner;
import com.oracle.javafx.scenebuilder.api.dock.SearchController;
import com.oracle.javafx.scenebuilder.api.dock.ViewController;
import com.oracle.javafx.scenebuilder.api.editor.job.ExtendedJob;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager.DockManagerImpl;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager.DocumentManagerImpl;
import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager.NetworkManagerImpl;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager.SceneBuilderManagerImpl;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager.ViewManagerImpl;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

public class ApiExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("5c288676-df8c-4755-ba74-8001ce5d1c6b");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
        return Arrays.asList(
            I18N.class,
            PreferencesContext.class,
            MavenSetting.class,
            SceneBuilderBeanFactory.class,
            ExtendedJob.class,
            ExtendedAction.class,
            Api.class,
            PreferencesContext.class,
            DockManagerImpl.class,
            DocumentManagerImpl.class,
            NetworkManagerImpl.class,
            SceneBuilderManagerImpl.class,
            ViewManagerImpl.class,
            DriverExtensionRegistry.class,
            ApiDoc.class,
            GenericDriver.class,
            SearchController.class,
            ModeManagerController.class,
            ViewController.class,
            SimilarInlineEditorBounds.class,
            NoPickRefiner.class,
            ActionFactory.class
        );
    }

    
}
