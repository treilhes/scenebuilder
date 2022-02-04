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
import com.oracle.javafx.scenebuilder.drivers.BaseToolingExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.controls.tooling {
    exports com.oracle.javafx.scenebuilder.drivers.imageview;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture;
    exports com.oracle.javafx.scenebuilder.drivers.ellipse;
    exports com.oracle.javafx.scenebuilder.drivers.text;
    exports com.oracle.javafx.scenebuilder.drivers.pane;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles;
    exports com.oracle.javafx.scenebuilder.drivers.line;
    exports com.oracle.javafx.scenebuilder.drivers;
    exports com.oracle.javafx.scenebuilder.drivers.anchorpane;
    exports com.oracle.javafx.scenebuilder.drivers.flowpane;
    exports com.oracle.javafx.scenebuilder.drivers.scene;
    exports com.oracle.javafx.scenebuilder.drivers.subscene;
    exports com.oracle.javafx.scenebuilder.drivers.gridpane;
    exports com.oracle.javafx.scenebuilder.drivers.gridpane.gesture;
    exports com.oracle.javafx.scenebuilder.drivers.gridpane.job;
    exports com.oracle.javafx.scenebuilder.drivers.splitpane;
    exports com.oracle.javafx.scenebuilder.drivers.arc;
    exports com.oracle.javafx.scenebuilder.drivers.circle;
    exports com.oracle.javafx.scenebuilder.drivers.region;
    exports com.oracle.javafx.scenebuilder.drivers.window;
    exports com.oracle.javafx.scenebuilder.drivers.common;
    exports com.oracle.javafx.scenebuilder.drivers.treetableview;
    exports com.oracle.javafx.scenebuilder.drivers.tab;
    exports com.oracle.javafx.scenebuilder.drivers.hbox;
    exports com.oracle.javafx.scenebuilder.drivers.quadcurve;
    exports com.oracle.javafx.scenebuilder.drivers.tablecolumn;
    exports com.oracle.javafx.scenebuilder.drivers.vbox;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides;
    exports com.oracle.javafx.scenebuilder.drivers.cubiccurve;
    exports com.oracle.javafx.scenebuilder.drivers.borderpane;
    exports com.oracle.javafx.scenebuilder.drivers.webview;
    exports com.oracle.javafx.scenebuilder.drivers.treetablecolumn;
    exports com.oracle.javafx.scenebuilder.drivers.toolbar;
    exports com.oracle.javafx.scenebuilder.drivers.polyline;
    exports com.oracle.javafx.scenebuilder.drivers.tableview;
    exports com.oracle.javafx.scenebuilder.drivers.polygon;
    exports com.oracle.javafx.scenebuilder.drivers.tabpane;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;
    exports com.oracle.javafx.scenebuilder.drivers.rectangle;
    exports com.oracle.javafx.scenebuilder.drivers.textflow;
    exports com.oracle.javafx.scenebuilder.drivers.node;
    exports com.oracle.javafx.scenebuilder.drivers.canvas;

    requires scenebuilder.starter;
    requires scenebuilder.core.drag.and.drop;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.graphics;
//    requires javafx.web;
    requires scenebuilder.core.jobs;
    requires scenebuilder.core.selection;
//    requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;
    requires scenebuilder.core.utils;

    provides Extension with BaseToolingExtension;
}
