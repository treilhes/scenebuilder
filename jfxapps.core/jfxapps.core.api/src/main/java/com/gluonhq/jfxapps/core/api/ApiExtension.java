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
package com.gluonhq.jfxapps.core.api;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.extension.RootExtension;
import com.gluonhq.jfxapps.boot.loader.extension.SealedExtension;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.application.javafx.internal.FxmlControllerBeanPostProcessor;
import com.gluonhq.jfxapps.core.api.application.javafx.internal.JavafxThreadBootstrapper;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.di.FxThreadAspect;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preferences.init.DocumentPreferencesNodeImpl;
import com.gluonhq.jfxapps.core.api.preferences.init.RootPreferencesNodeImpl;
import com.gluonhq.jfxapps.core.api.settings.MavenSetting;
import com.gluonhq.jfxapps.core.api.subjects.DockManager;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.NetworkManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.subjects.ViewManager;
import com.gluonhq.jfxapps.core.api.ui.ViewMenuController;
import com.gluonhq.jfxapps.core.api.ui.dock.DockNameHelper;
import com.gluonhq.jfxapps.core.api.ui.dock.SearchController;
import com.gluonhq.jfxapps.core.api.ui.dock.ViewController;
import com.gluonhq.jfxapps.core.api.ui.menu.MenuBuilder;

public class ApiExtension implements RootExtension {

    public final static UUID ID = ROOT_ID;

    @Override
    public UUID getParentId() {
        return SealedExtension.ROOT_ID;
    }

    @Override
    public UUID getId() {
        return ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return Arrays.asList(
                FxThreadAspect.class,

                FxmlControllerBeanPostProcessor.class,
                JavafxThreadBootstrapper.class,
                ActionExtensionFactory.class,
                ActionFactory.class,
                DiscardGesture.class,
                DiscardGesture.Factory.class,
                DockNameHelper.class,
                DockManager.DockManagerImpl.class,
                DocumentManager.DocumentManagerImpl.class,
                I18N.class,
                JobExtensionFactory.class,
                MavenSetting.class,
                MenuBuilder.class,
                NetworkManager.NetworkManagerImpl.class,
                PreferencesContext.class,
                SceneBuilderManager.SceneBuilderManagerImpl.class,
                SearchController.class,
                //Selection.class,
                ViewController.class,
                ViewManager.ViewManagerImpl.class,
                ViewMenuController.class,
                DocumentPreferencesNodeImpl.class,
                RootPreferencesNodeImpl.class
        );
    }

//    @Override
//    public InputStream getLicense() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public InputStream getDescription() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public InputStream getLoadingImage() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public InputStream getIcon() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public InputStream getIconX2() {
//        // TODO Auto-generated method stub
//        return null;
//    }


}