/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.app.preferences;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.BottomVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.DocumentVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.I18NResourcePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftDividerVPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LibraryVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.PathPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.RightVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.StageHeightPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.StageWidthPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.XPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.YPosPreference;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonThemePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonThemePreference.GluonTheme;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController.SectionId;
import com.oracle.javafx.scenebuilder.kit.preferences.document.InspectorSectionIdPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.document.SceneStyleSheetsPreference;

/**
 * Defines preferences specific to a document.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentPreferences {

    // Document preferences
    private final XPosPreference xPos;
    private final YPosPreference yPos;
    private final StageHeightPreference stageHeight;
    private final StageWidthPreference stageWidth;
    private final BottomVisiblePreference bottomVisible;
    private final LeftVisiblePreference leftVisible;
    private final RightVisiblePreference rightVisible;
    private final LibraryVisiblePreference libraryVisible;
    private final DocumentVisiblePreference documentVisible;
    private final InspectorSectionIdPreference inspectorSectionId;
    private final LeftDividerHPosPreference leftDividerHPos;
    private final RightDividerHPosPreference rightDividerHPos;
    private final BottomDividerVPosPreference bottomDividerVPos;
    private final LeftDividerVPosPreference leftDividerVPos;

    private final SceneStyleSheetsPreference sceneStyleSheets;
    private final I18NResourcePreference I18NResource;
    private final ThemePreference theme;
    private final GluonSwatchPreference gluonSwatch;
    private final GluonThemePreference gluonTheme;
    private final PathPreference path;

    private final DocumentWindowController documentWindowController;


    public DocumentPreferences(
    		DocumentWindowController dwc,
    		@Autowired PathPreference path,
    		@Autowired XPosPreference xPos,
    	    @Autowired YPosPreference yPos,
    	    @Autowired StageHeightPreference stageHeight,
    	    @Autowired StageWidthPreference stageWidth,
    	    @Autowired BottomVisiblePreference bottomVisible,
    	    @Autowired LeftVisiblePreference leftVisible,
    	    @Autowired RightVisiblePreference rightVisible,
    	    @Autowired LibraryVisiblePreference libraryVisible,
    	    @Autowired DocumentVisiblePreference documentVisible,
    	    @Autowired InspectorSectionIdPreference inspectorSectionId,
    	    @Autowired LeftDividerHPosPreference leftDividerHPos,
    	    @Autowired RightDividerHPosPreference rightDividerHPos,
    	    @Autowired BottomDividerVPosPreference bottomDividerVPos,
    	    @Autowired LeftDividerVPosPreference leftDividerVPos,
    	    @Autowired SceneStyleSheetsPreference sceneStyleSheets,
    	    @Autowired I18NResourcePreference I18NResource,
    	    @Autowired ThemePreference theme,
    	    @Autowired GluonSwatchPreference gluonSwatch,
    	    @Autowired GluonThemePreference gluonTheme
    		) {
        this.documentWindowController = dwc;
        this.path = path;
        this.xPos = xPos;
        this.yPos = yPos;
        this.stageHeight = stageHeight;
        this.stageWidth = stageWidth;
        this.bottomVisible = bottomVisible;
        this.leftVisible = leftVisible;
        this.rightVisible = rightVisible;
        this.libraryVisible = libraryVisible;
        this.documentVisible = documentVisible;
        this.inspectorSectionId = inspectorSectionId;
        this.leftDividerHPos = leftDividerHPos;
        this.rightDividerHPos = rightDividerHPos;
        this.bottomDividerVPos = bottomDividerVPos;
        this.leftDividerVPos = leftDividerVPos;
        this.sceneStyleSheets = sceneStyleSheets;
        this.I18NResource = I18NResource;
        this.theme = theme;
        this.gluonSwatch = gluonSwatch;
        this.gluonTheme = gluonTheme;

    }

    public String getPath() {
		return path.getValue();
	}

    public void setPath(String value) {
		path.setValue(value);
	}

	public double getXPos() {
        return xPos.getValue();
    }

    public void setXPos(double value) {
        xPos.setValue(value);
    }

    public double getYPos() {
        return yPos.getValue();
    }

    public void setYPos(double value) {
        yPos.setValue(value);
    }

    public double getStageHeight() {
        return stageHeight.getValue();
    }

    public void setStageHeight(double value) {
        stageHeight.setValue(value);
    }

    public double getStageWidth() {
        return stageWidth.getValue();
    }

    public void setStageWidth(double value) {
        stageWidth.setValue(value);
    }

    public boolean getBottomVisible() {
        return bottomVisible.getValue();
    }

    public void setBottomVisible(boolean value) {
        bottomVisible.setValue(value);
    }

    public boolean getLeftVisible() {
        return leftVisible.getValue();
    }

    public void setLeftVisible(boolean value) {
        leftVisible.setValue(value);
    }

    public boolean getRightVisible() {
        return rightVisible.getValue();
    }

    public void setRightVisible(boolean value) {
        rightVisible.setValue(value);
    }

    public boolean getLibraryVisible() {
        return libraryVisible.getValue();
    }

    public void setLibraryVisible(boolean value) {
        libraryVisible.setValue(value);
    }

    public boolean getDocumentVisible() {
        return documentVisible.getValue();
    }

    public void setDocumentVisible(boolean value) {
        documentVisible.setValue(value);
    }

    public void setInspectorSectionId(SectionId value) {
        inspectorSectionId.setValue(value);
    }

    public double getLeftDividerHPos() {
        return leftDividerHPos.getValue();
    }

    public void setLeftDividerHPos(double value) {
        leftDividerHPos.setValue(value);
    }

    public double getRightDividerHPos() {
        return rightDividerHPos.getValue();
    }

    public void setRightDividerHPos(double value) {
        rightDividerHPos.setValue(value);
    }

    public double getBottomDividerVPos() {
        return bottomDividerVPos.getValue();
    }

    public void setBottomDividerVPos(double value) {
        bottomDividerVPos.setValue(value);
    }

    public double getLeftividerVPos() {
        return leftDividerVPos.getValue();
    }

    public void setLeftDividerVPos(double value) {
        leftDividerVPos.setValue(value);
    }

    public List<String> getSceneStyleSheets() {
        return sceneStyleSheets.getValue();
    }

    public String getI18NResource() {
        return I18NResource.getValue();
    }

    public void setI18NResource(String value) {
        I18NResource.setValue(value);
    }

    public void setI18NResourceFile(File file) {
        if (file != null) {
            I18NResource.setValue(file.getPath());
        }
    }

//    public void setGluonSwatch(GluonSwatch gluonSwatch) {
//        this.gluonSwatch.setValue(gluonSwatch);
//    }
//
//    public GluonSwatch getGluonSwatch() {
//        if (gluonSwatch.getValue() == null) {
//            return documentWindowController.getEditorController().getGluonSwatch();
//        }
//        return gluonSwatch.getValue();
//    }
//
//    public void setGluonTheme(GluonTheme gluonTheme) {
//        this.gluonTheme.setValue(gluonTheme);
//    }
//
//    public GluonTheme getGluonTheme() {
//        if (gluonTheme.getValue() == null) {
//            return documentWindowController.getEditorController().getGluonTheme();
//        }
//        return gluonTheme.getValue();
//    }

}
