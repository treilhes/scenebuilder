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
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.api.ApiExtension;

open module jfxapps.core.api {

    exports com.oracle.javafx.scenebuilder.api;
    exports com.oracle.javafx.scenebuilder.api.action;
    exports com.oracle.javafx.scenebuilder.api.action.editor;

    exports com.oracle.javafx.scenebuilder.api.application;
    exports com.oracle.javafx.scenebuilder.api.application.annotation;
    exports com.oracle.javafx.scenebuilder.api.application.lifecycle;

    exports com.oracle.javafx.scenebuilder.api.ui.alert;
    exports com.oracle.javafx.scenebuilder.api.clipboard;


    exports com.oracle.javafx.scenebuilder.api.content.decoration;
    exports com.oracle.javafx.scenebuilder.api.content.gesture;
    exports com.oracle.javafx.scenebuilder.api.content.mode;
    exports com.oracle.javafx.scenebuilder.api.content.mode.annotation;
    exports com.oracle.javafx.scenebuilder.api.di;
    exports com.oracle.javafx.scenebuilder.api.dnd;
    exports com.oracle.javafx.scenebuilder.api.ui.dock;
    exports com.oracle.javafx.scenebuilder.api.ui.dock.annotation;
    exports com.oracle.javafx.scenebuilder.api.editor.images;
    exports com.oracle.javafx.scenebuilder.api.editor.selection;
    exports com.oracle.javafx.scenebuilder.api.error;
    exports com.oracle.javafx.scenebuilder.api.factory;
    exports com.oracle.javafx.scenebuilder.api.fs;
    exports com.oracle.javafx.scenebuilder.api.i18n;
    exports com.oracle.javafx.scenebuilder.api.job;
    exports com.oracle.javafx.scenebuilder.api.launcher;
    exports com.oracle.javafx.scenebuilder.api.library;

    exports com.oracle.javafx.scenebuilder.api.maven;
    exports com.oracle.javafx.scenebuilder.api.metadata;
    exports com.oracle.javafx.scenebuilder.api.ui;
    exports com.oracle.javafx.scenebuilder.api.ui.selbar;
    exports com.oracle.javafx.scenebuilder.api.ui.menu;
    exports com.oracle.javafx.scenebuilder.api.ui.menu.annotation;
    exports com.oracle.javafx.scenebuilder.api.ui.misc;
    exports com.oracle.javafx.scenebuilder.api.ui.dialog;
    exports com.oracle.javafx.scenebuilder.api.preferences;
    exports com.oracle.javafx.scenebuilder.api.preferences.type;

    exports com.oracle.javafx.scenebuilder.api.settings;
    exports com.oracle.javafx.scenebuilder.api.shortcut;
    exports com.oracle.javafx.scenebuilder.api.shortcut.annotation;
    exports com.oracle.javafx.scenebuilder.api.subjects;
    exports com.oracle.javafx.scenebuilder.api.template;
    exports com.oracle.javafx.scenebuilder.api.theme;
    exports com.oracle.javafx.scenebuilder.api.tooltheme;

    exports com.oracle.javafx.scenebuilder.api.util;

    exports com.oracle.javafx.scenebuilder.core.action.editor;

    exports com.oracle.javafx.scenebuilder.api.application.javafx;

    requires transitive jfxapps.javafx.starter;

    requires transitive jfxapps.boot.loader;
    requires transitive jfxapps.boot.platform;
    requires transitive jfxapps.boot.maven;
    requires transitive jfxapps.boot.starter;

    requires transitive jfxapps.core.utils;
    requires transitive jfxapps.core.fxom;
    requires transitive jfxapps.core.metadata;

    requires transitive io.reactivex.rxjava3;
    requires transitive org.reactivestreams;
    requires transitive org.pdfsam.rxjavafx;

    provides Extension with ApiExtension;
}