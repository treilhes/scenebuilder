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
package com.oracle.javafx.scenebuilder.boot.main.command;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.boot.main.Main;
import com.oracle.javafx.scenebuilder.core.loader.ApplicationManager;
import com.oracle.javafx.scenebuilder.core.loader.OpenCommandEvent;
import com.oracle.javafx.scenebuilder.core.loader.dev.DevelopmentMode;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "run")
public class RunFxmlCommand implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RunFxmlCommand.class);

    final static String RUNNER_APPLICATION = "/runner.json";

    @Option(names = {"--dev", "-d"}, description = "Enable development mode, project extensions sources are resolved localy using classes folder")
    public boolean devMode;

    @Option(names = {"--root", "-r"}, defaultValue = "./target/runner", description = "Runner Extensions download folder")
    public Path root;

    @Option(names = {"--fxml", "-f"}, required = true, description = "Fxml file to run")
    public File fxmlFile;

    @Override
    public void run() {
        if (devMode) {
            DevelopmentMode.setActive(true);
            DevelopmentMode.addMavenProjectDirectory(Path.of(".").resolve("../.."));
        }

        logger.info("running fxml file : {}", fxmlFile.getAbsolutePath());

        ApplicationManager appManager =  ApplicationManager.get(root);

        try (InputStream bootStream = Main.class.getResourceAsStream(RUNNER_APPLICATION)){
            appManager.loadApplication(bootStream);
            appManager.load();
            appManager.start();
            appManager.send(new OpenCommandEvent(null, fxmlFile));
        } catch (Exception e) {
            logger.error("Unable to load the default bootable application definition", e);
        }
    }
}