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
package com.oracle.javafx.scenebuilder.exporter.format;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
public class PngFormat implements ExportFormat {

    protected PngFormat() {}

    @Override
    public String getDescription() {
        return I18N.getString("export.png.format.description");
    }

    @Override
    public String getExtension() {
        return I18N.getString("export.png.format.extension");
    }

    @Override
    public boolean canHandleSelection() {
        return true;
    }

    @Override
    public boolean canHandleScene() {
        return true;
    }

    @Override
    public void exportSelection(Selection selection, File output) {
        String baseName = output.getName().substring(0, output.getName().lastIndexOf("."));
        String extension = output.getName().substring(output.getName().lastIndexOf(".") + 1);
        
        int targetIndex = 1;
        for (FXOMObject fxo : selection.getGroup().getItems()) {
            if (fxo.isNode()) {
                String idx = targetIndex == 1 ? "" : "_" + String.format("%04d", targetIndex);//NOCHECK
                File target = new File(output.getParentFile(), baseName + idx + "." + extension);//NOCHECK
                exportScene((Node)fxo.getSceneGraphObject(), target);
                targetIndex++;
            }
        }
    }

    @Override
    public void exportScene(Node rootNode, File output) {
        SnapshotParameters param = new SnapshotParameters();
        param.setDepthBuffer(true);
        WritableImage snapshot = rootNode.snapshot(param, null);
        BufferedImage tempImg = SwingFXUtils.fromFXImage(snapshot, null);
        
        try (FileOutputStream fos = new FileOutputStream(output)){
            ImageIO.write(tempImg, "png", fos);//NOCHECK
        } catch(Exception e) {
            // TODO do something here log or throw or message
        }
    }
}
