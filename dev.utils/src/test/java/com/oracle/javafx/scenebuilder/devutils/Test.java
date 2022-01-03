/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.devutils;

import com.oracle.javafx.scenebuilder.devutils.tbview.TestExternalFile;
import com.oracle.javafx.scenebuilder.devutils.test.AppTester;

import javafx.beans.property.DoubleProperty;
import javafx.stage.Stage;

public class Test extends AppTester {

    public DoubleProperty xCordinate;
    public DoubleProperty yCordinate;

    @Override
    public void start(Stage stage) {
        //new TestAnim().start(stage);
        //new TestContent().start(primaryStage);
        //new TestContent2().start(primaryStage);
        //new TestContent5().start(stage);
        //new TitlePaneBinding().start(stage);
        //new TableViewBinding().start(stage);
        //new TestContent4().start(stage);
        //new TestExternalFile().start(stage, "C:\\Users\\ptreilhes\\Desktop\\tmp\\panInc2.fxml");
        //new TestExternalFile().start(stage, "C:\\Users\\ptreilhes\\Desktop\\tmp\\accordion.fxml");
        //new TestExternalFile().start(stage, "C:\\Users\\ptreilhes\\Desktop\\tmp\\accFocusTravers.fxml");
        //new TestExternalFile().start(stage, "C:\\SSDDrive\\git\\scenebuilder\\scenebuilder.core.fxom\\src\\test\\resources\\com\\oracle\\javafx\\scenebuilder\\core\\fxom\\fx\\comments.fxml");
        //new TestExternalFile().start(stage, "C:\\Users\\ptreilhes\\Desktop\\tmp\\script.fxml");
        new TestExternalFile().start(stage, "C:\\Users\\ptreilhes\\Desktop\\tmp\\one.fxml");

    }

    public static void main(String args[]) {
        launch(args);
    }
}
