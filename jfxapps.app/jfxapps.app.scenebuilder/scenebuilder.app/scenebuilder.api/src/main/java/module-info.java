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
import org.scenebuilder.fxml.api.FxmlApiExtension;

import com.gluonhq.jfxapps.core.extension.Extension;

open module scenebuilder.fxml.api {
    exports org.scenebuilder.fxml.api;
    exports org.scenebuilder.fxml.api.i18n;
    exports org.scenebuilder.fxml.api.subjects;

    exports com.gluonhq.jfxapps.core.api.content;
    exports com.gluonhq.jfxapps.core.api.mode;
    exports com.gluonhq.jfxapps.core.api.control;
    exports com.oracle.javafx.scenebuilder.api.control.curve;
    exports com.oracle.javafx.scenebuilder.api.control.decoration;
    exports com.oracle.javafx.scenebuilder.api.control.driver;
    exports com.oracle.javafx.scenebuilder.api.control.droptarget;
    exports com.oracle.javafx.scenebuilder.api.control.effect;
    exports com.gluonhq.jfxapps.core.api.control.handles;
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
    exports com.gluonhq.jfxapps.core.api.css;
    exports com.gluonhq.jfxapps.core.api.mask;
    exports com.oracle.javafx.scenebuilder.api.script;
    exports com.gluonhq.jfxapps.core.api.theme.theme;

    exports com.oracle.javafx.scenebuilder.fxml.api.selection;

    requires transitive jfxapps.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires transitive scenebuilder.core.fxom;
    requires transitive scenebuilder.core.metadata;
    requires transitive scenebuilder.starter;


    provides Extension with FxmlApiExtension;
}