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
package com.oracle.javafx.scenebuilder.imagelibrary.controller;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.graalvm.compiler.lir.CompositeValue.Component;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.gluonhq.jfxapps.core.api.library.LibraryItem;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibrary;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
public class ThumbnailServiceController implements DisposeWithApplication {

    private final static Logger logger = LoggerFactory.getLogger(ThumbnailServiceController.class);
    
    private final SceneBuilderManager sceneBuilderManager;
    private final ImageLibrary imageLibrary;
    private final ExecutorService thumbnailExecutor;

    public ThumbnailServiceController(
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired ImageLibrary imageLibrary
            ) {

        this.sceneBuilderManager = sceneBuilderManager;
        this.imageLibrary = imageLibrary;
        this.thumbnailExecutor = Executors.newFixedThreadPool(2);
    }

    public void newThumbnailRequest(LibraryItem item, ImageView view, int width, int height) {
        String name = item.getSection() + "_" + item.getName();
        
        URL url = imageLibrary.getStore().getThumbnail(name, width, height);
        
        if (url != null) {
            if (view.getImage() != null && url.toExternalForm().equals(view.getImage().getUrl())) {
                return;
            } else {
                SbPlatform.runOnFxThread(() -> view.setImage(new Image(url.toExternalForm())));
                return;
            }
        }
        
        thumbnailExecutor.submit(() -> {
            
            logger.info("Processing thumbnail request for {} {}x{}", name, width, height);
            
            try {
                
                
                ClassLoader clsLoader = sceneBuilderManager.classloader().get();
                FXOMDocument fxomItem = item.instantiate(clsLoader);
                
                if (fxomItem.isNode()) {
                    Node node = (Node)fxomItem.getSceneGraphRoot();
                    
                    SnapshotParameters param = new SnapshotParameters();
                    param.setDepthBuffer(true);
                    
                    final CountDownLatch latch = new CountDownLatch(1);
                    final SimpleObjectProperty<WritableImage> imageProperty = new SimpleObjectProperty<>();
                    
                    SbPlatform.runOnFxThread(() -> {
                        WritableImage snapshot = node.snapshot(param, null);
                        
                        ImageView imageView = new ImageView(snapshot);
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(width);
                        imageView.setFitHeight(height);
                        snapshot = imageView.snapshot(null, null);
                        
                        imageProperty.set(snapshot);
                        latch.countDown();
                    });
                    
                    latch.await();
                    
                    imageLibrary.getStore().saveThumbnail(name, width, height, imageProperty.get());
                    
                    URL newUrl = imageLibrary.getStore().getThumbnail(name, width, height);
                    
                    if (newUrl != null) {
                        SbPlatform.runOnFxThread(() -> view.setImage(new Image(newUrl.toExternalForm())));
                        return newUrl;
                    }
                    
                    return null;
                }
            } catch (Exception e) {
                logger.error("Error while processing thumbnail request for {} {}x{}", name, width, height, e);
            }
            
            return null;
        });
    }

    @Override
    public void dispose() {
        thumbnailExecutor.shutdownNow();
    }
}
