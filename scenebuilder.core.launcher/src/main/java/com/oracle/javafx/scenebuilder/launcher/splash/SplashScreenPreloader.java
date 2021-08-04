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
package com.oracle.javafx.scenebuilder.launcher.splash;

import java.io.IOException;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreenPreloader extends Preloader {

    public static final String APP_ICON_16 = SplashScreenPreloader.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = SplashScreenPreloader.class.getResource("SceneBuilderLogo_32.png").toString();

    
    private final static int TEXT_MAX_LENGTH = 50;
    
    @FXML
    private Text text;
    
    @FXML
    private ProgressBar bar;
    
    private Stage stage;

    private Scene scene;
 
    private Scene createPreloaderScene() {
        try {
            final FXMLLoader loader = new FXMLLoader();

            loader.setController(this);
            loader.setLocation(SplashScreenPreloader.class.getResource("splash.fxml"));
            
            BorderPane bp = loader.load();
               
            SceneBuilderLoadingProgress.get().setOnProgressChange(p -> {
                if (Platform.isFxApplicationThread()) {
                    
                    new Thread(() -> bar.setProgress(p)).start();
                    
                } else {
                    SbPlatform.runLater(() -> bar.setProgress(p));
                }
                
            });
            SceneBuilderLoadingProgress.get().setOnTextChange(t -> SbPlatform.runLater(() -> {
                text.setText(t.length() > TEXT_MAX_LENGTH ? t.substring(0, TEXT_MAX_LENGTH) : t );
            }));
            SceneBuilderLoadingProgress.get().setOnLoadingDone(() -> SbPlatform.runLater(() -> {
                stage.hide();
            }));
            
            return new Scene(bp, 600, 600, Color.TRANSPARENT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.scene = createPreloaderScene();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);        
        //TODO find a way to get those icons from WindowIconSettings.class
        Image icon16 = new Image(SplashScreenPreloader.APP_ICON_16);
        Image icon32 = new Image(SplashScreenPreloader.APP_ICON_32);
        stage.getIcons().addAll(icon16, icon32);
        stage.show();
    }
    
    @Override
    public void handleProgressNotification(ProgressNotification pn) {}
 
    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        
    }    
}
