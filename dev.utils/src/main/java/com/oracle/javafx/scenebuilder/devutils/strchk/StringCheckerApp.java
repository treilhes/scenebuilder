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
package com.oracle.javafx.scenebuilder.devutils.strchk;

import java.io.File;
import java.util.Date;

import com.oracle.javafx.scenebuilder.devutils.strchk.controller.ResourceLocationsController;
import com.oracle.javafx.scenebuilder.devutils.strchk.loader.ProjectLoader;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.utils.Loader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StringCheckerApp extends Application {
    
    
    
    public static void main(String args[]) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(new Date());
        Project project = ProjectLoader.load(new File(".", Config.ROOT_PROJECT).getCanonicalFile());
        System.out.println(new Date());
        HBox box1 = new HBox(5);
        
        ResourceLocationsController ctrl = new ResourceLocationsController();
        AnchorPane node = Loader.load(ctrl, "ResourceLocations.fxml");
        ctrl.initialize(project);
        
        Scene scene = new Scene(node, 1024, 768, Color.BEIGE);
        primaryStage.setTitle("String Checker Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
