/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMNormalizer;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMRefresher;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FileLoader;
import com.oracle.javafx.scenebuilder.core.fxom.ext.LoaderCapabilitiesManager;
import com.oracle.javafx.scenebuilder.core.fxom.ext.TransientStateBackup;
import com.oracle.javafx.scenebuilder.core.fxom.ext.WeakProperty;

open module scenebuilder.core.fxom {

    exports com.oracle.javafx.scenebuilder.core.fxom;
    exports com.oracle.javafx.scenebuilder.core.fxom.collector;
    exports com.oracle.javafx.scenebuilder.core.fxom.glue;
    exports com.oracle.javafx.scenebuilder.core.fxom.sampledata;
    exports com.oracle.javafx.scenebuilder.core.fxom.ext;
    exports com.oracle.javafx.scenebuilder.core.fxom.util;


//    requires javafx.fxml;
//    requires transitive javafx.graphics;
//    requires javafx.controls;
//    requires java.xml;
//    requires javafx.media;
//    requires java.desktop;
    requires org.slf4j;

    requires jfxapps.core.utils;
    requires java.scripting;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.xml;
    requires java.desktop;

    uses FXOMNormalizer;
    uses FXOMRefresher;
    uses TransientStateBackup;
    uses WeakProperty;
    uses FileLoader;
    uses LoaderCapabilitiesManager;
}