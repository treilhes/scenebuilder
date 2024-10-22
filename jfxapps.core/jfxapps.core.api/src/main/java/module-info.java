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
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.core.api.ApiExtension;

open module jfxapps.core.api {

    exports com.gluonhq.jfxapps.core.api;
    exports com.gluonhq.jfxapps.core.api.action;
    exports com.gluonhq.jfxapps.core.api.action.editor;

    exports com.gluonhq.jfxapps.core.api.application;
    exports com.gluonhq.jfxapps.core.api.application.annotation;
    exports com.gluonhq.jfxapps.core.api.lifecycle;

    exports com.gluonhq.jfxapps.core.api.ui.controller.alert;
    exports com.gluonhq.jfxapps.core.api.clipboard;


    exports com.gluonhq.jfxapps.core.api.content.decoration;
    exports com.gluonhq.jfxapps.core.api.content.gesture;
    exports com.gluonhq.jfxapps.core.api.content.mode;
    exports com.gluonhq.jfxapps.core.api.content.mode.annotation;
    exports com.gluonhq.jfxapps.core.api.css;

    exports com.gluonhq.jfxapps.core.api.dnd;
    exports com.gluonhq.jfxapps.core.api.ui.controller.dock;
    exports com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation;
    exports com.gluonhq.jfxapps.core.api.editor.images;
    exports com.gluonhq.jfxapps.core.api.editor.selection;
    exports com.gluonhq.jfxapps.core.api.error;
    exports com.gluonhq.jfxapps.core.api.factory;
    exports com.gluonhq.jfxapps.core.api.fs;
    exports com.gluonhq.jfxapps.core.api.fxom;
    exports com.gluonhq.jfxapps.core.api.i18n;
    exports com.gluonhq.jfxapps.core.api.job;
    exports com.gluonhq.jfxapps.core.api.job.base;
    exports com.gluonhq.jfxapps.core.api.launcher;
    exports com.gluonhq.jfxapps.core.api.library;

    exports com.gluonhq.jfxapps.core.api.maven;
    exports com.gluonhq.jfxapps.core.api.mask;
    exports com.gluonhq.jfxapps.core.api.ui;
    exports com.gluonhq.jfxapps.core.api.ui.controller;
    exports com.gluonhq.jfxapps.core.api.ui.controller.selbar;
    exports com.gluonhq.jfxapps.core.api.ui.controller.menu;
    exports com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation;
    exports com.gluonhq.jfxapps.core.api.ui.controller.misc;
    exports com.gluonhq.jfxapps.core.api.ui.dialog;
    exports com.gluonhq.jfxapps.core.api.ui.tool;

    exports com.gluonhq.jfxapps.core.api.preference;

    exports com.gluonhq.jfxapps.core.api.settings;
    exports com.gluonhq.jfxapps.core.api.shortcut;
    exports com.gluonhq.jfxapps.core.api.shortcut.annotation;
    exports com.gluonhq.jfxapps.core.api.subjects;
    exports com.gluonhq.jfxapps.core.api.template;

    exports com.gluonhq.jfxapps.core.api.tooltheme;

    exports com.gluonhq.jfxapps.core.api.util;

    exports com.gluonhq.jfxapps.core.api.javafx;

    requires transitive jfxapps.javafx.starter;

    requires transitive jfxapps.boot.api;

//    requires transitive jfxapps.boot.loader;
//    requires transitive jfxapps.boot.platform;
//    requires transitive jfxapps.boot.maven;
    requires transitive jfxapps.boot.starter;

    requires transitive jfxapps.core.utils;
    requires transitive jfxapps.core.fxom;
    requires transitive jfxapps.core.metadata;
    //requires transitive jfxapps.core.controls;

    requires transitive io.reactivex.rxjava3;
    requires transitive org.reactivestreams;
    requires transitive org.pdfsam.rxjavafx;
    requires jfxapps.javafx.fxml.patch.link;

    provides Extension with ApiExtension;
}