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
import com.oracle.javafx.scenebuilder.editor.fxml.FxmlEditorExtension;

open module scenebuilder.ext.editor.fxml {
    exports com.oracle.javafx.scenebuilder.editor.fxml.preferences.global;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key;
    exports com.oracle.javafx.scenebuilder.editor.fxml.actions;
    exports com.oracle.javafx.scenebuilder.editor.fxml.guides;
    exports com.oracle.javafx.scenebuilder.editor.fxml;
    exports com.oracle.javafx.scenebuilder.editor.fxml.gesture;
    exports com.oracle.javafx.scenebuilder.editor.fxml.controller;
    exports com.oracle.javafx.scenebuilder.editor.fxml.i18n;
    exports com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse;

    requires scenebuilder.starter;
    requires transitive scenebuilder.api;
    requires scenebuilder.core.drag.and.drop;
//    requires io.reactivex.rxjava2;
//    requires java.logging;
//    requires java.prefs;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    requires scenebuilder.core.selection;

    //requires scenebuilder.ext.controls.tooling;
    //requires org.slf4j;
    //requires scenebuilder.ext.sb;

    requires scenebuilder.core.extension.api;
    requires scenebuilder.ext.menu;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with FxmlEditorExtension;
}