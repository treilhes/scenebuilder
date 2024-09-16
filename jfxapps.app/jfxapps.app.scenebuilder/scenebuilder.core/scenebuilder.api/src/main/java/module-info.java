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
import org.scenebuilder.fxml.api.SbApiExtension;

import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;

open module scenebuilder.api {
    exports org.scenebuilder.fxml.api;
    exports org.scenebuilder.fxml.api.i18n;

    exports com.oracle.javafx.scenebuilder.api.control;
    exports com.oracle.javafx.scenebuilder.api.control.curve;
    exports com.oracle.javafx.scenebuilder.api.control.driver;
    exports com.oracle.javafx.scenebuilder.api.control.droptarget;
    exports com.oracle.javafx.scenebuilder.api.control.effect;
    exports com.oracle.javafx.scenebuilder.api.control.handles;
    exports com.oracle.javafx.scenebuilder.api.control.inlineedit;
    exports com.oracle.javafx.scenebuilder.api.control.intersect;
    exports com.oracle.javafx.scenebuilder.api.control.outline;
    exports com.oracle.javafx.scenebuilder.api.control.pickrefiner;
    exports com.oracle.javafx.scenebuilder.api.control.pring;
    exports com.oracle.javafx.scenebuilder.api.control.relocater;
    exports com.oracle.javafx.scenebuilder.api.control.resizer;
    exports com.oracle.javafx.scenebuilder.api.control.rudder;
    exports com.oracle.javafx.scenebuilder.api.control.tring;
    exports com.oracle.javafx.scenebuilder.api.controls;
    exports com.oracle.javafx.scenebuilder.api.css;
    exports com.oracle.javafx.scenebuilder.api.dnd;
    exports com.oracle.javafx.scenebuilder.api.editors;

    exports com.oracle.javafx.scenebuilder.api.job;
    exports com.oracle.javafx.scenebuilder.api.mask;
    exports com.oracle.javafx.scenebuilder.api.menu;
    exports com.oracle.javafx.scenebuilder.api.script;
    exports com.oracle.javafx.scenebuilder.api.selection;
    exports com.oracle.javafx.scenebuilder.api.theme;
    exports com.oracle.javafx.scenebuilder.api.ui;
    exports com.oracle.javafx.scenebuilder.api.util;

    requires transitive jfxapps.core.api;
    requires transitive jfxapps.core.fxom;
    //requires transitive scenebuilder.metadata.sbjavafx;
    requires transitive jfxapps.core.starter;
    requires transitive scenebuilder.metadata.customization;

    provides Extension with SbApiExtension;
}