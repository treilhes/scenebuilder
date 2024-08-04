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
package com.gluonhq.jfxapps.test;

import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;

import javafx.scene.Parent;

public class FxmlControllerLoader {

    private FxmlControllerLoader() {}

    public static <T extends FxmlController> Builder<T> controller(T controller) {
        return new Builder<T>(controller);
    }

    private static <T extends FxmlController> T load(T controller) {
        Parent node = loadFxml(controller);
        controller.setRoot(node);
        controller.controllerDidLoadFxml();
        return controller;
    }

    private static <T extends FxmlController> Parent loadFxml(T controller) {
        return FXMLUtils.load(controller, controller.getFxmlURL(), controller.getResources());
    }

    public static class Builder<T extends FxmlController> {
        private T controller;
        private SceneBuilderManager sbm;
        private boolean darkTheme = false;
        private boolean defaulTheme = false;

        public Builder(T controller) {
            super();
            this.controller = controller;
        }

        public Builder<T> darkTheme(SceneBuilderManager sbm) {
            this.sbm = sbm;
            darkTheme = true;
            defaulTheme = false;
            return this;
        }

        public Builder<T> defaultTheme(SceneBuilderManager sbm) {
            this.sbm = sbm;
            darkTheme = false;
            defaulTheme = true;
            return this;
        }

        public T load() {
            FxmlControllerLoader.load(controller);

            if (sbm != null && darkTheme) {
                //sbm.stylesheetConfig().onNext(new DefaultToolThemesList.Dark());
            }

            if (sbm != null && defaulTheme) {
                //sbm.stylesheetConfig().onNext(new DefaultToolThemesList.Default());
            }

            return controller;
        }
        public Parent loadFxml() {
            return FxmlControllerLoader.loadFxml(controller);
        }
    }
}
