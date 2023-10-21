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
package com.oracle.javafx.scenebuilder.core.loader.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.oracle.javafx.scenebuilder.core.loader.BootException;
import com.oracle.javafx.scenebuilder.core.loader.model.Application;

class ApplicationManagerIT {

    @TempDir
    Path rootDir;

    @Test
    void bootstrap_load_root_app() throws IOException, BootException {
        ApplicationManagerImpl appManager = new ApplicationManagerImpl(rootDir);

        try (InputStream json = this.getClass().getResourceAsStream("root-app.json")){
            appManager.loadApplication(json);
            appManager.load();
            appManager.start(null);
        }
        appManager.logState();
        appManager.stop();
        appManager.unload();
    }

    @Test
    void bootstrap_load_root_app2() throws IOException {
        ApplicationManagerImpl appManager = new ApplicationManagerImpl(rootDir);

        try (InputStream json = this.getClass().getResourceAsStream("root-app.json")){
            appManager.loadApplication(json);
            appManager.load(System.out::println);
            appManager.start(System.out::println);
        } catch(Throwable e) {
            e.printStackTrace();
        }
        appManager.logState();

        appManager.stop();
        System.out.println();
    }

    @Test
    void should_load_root_app_and_ext() throws IOException, BootException {
        ApplicationManagerImpl appManager = new ApplicationManagerImpl(rootDir);

        try (InputStream json = this.getClass().getResourceAsStream("root-app-and-ext.json")){
            appManager.loadApplication(json);
            appManager.load();
            appManager.start(null);
        }

        appManager.logState();

        appManager.stop();
        appManager.unload();
        System.out.println();

    }

    @Test
    void should_remove_ext_context_and_beans_from_root_app() throws IOException {
        try {
            ApplicationManagerImpl appManager = new ApplicationManagerImpl(rootDir);

            try (InputStream json = this.getClass().getResourceAsStream("root-app-and-ext.json")){
                appManager.loadApplication(json);
                appManager.load();
                appManager.start(null);
            }
            Application app = appManager.getApplication();

            appManager.logState();
            appManager.stop();
            appManager.remove(app.getExtensions().iterator().next().getId());
            appManager.logState();

            appManager.stop();
            System.out.println();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
