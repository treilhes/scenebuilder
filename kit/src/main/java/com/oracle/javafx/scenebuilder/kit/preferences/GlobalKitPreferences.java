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
package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.ext.theme.global.ThemePreference;
//import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference;
//import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonThemePreference;
//import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;
//import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonThemePreference.GluonTheme;
import com.oracle.javafx.scenebuilder.kit.preferences.global.AlignmentGuidesColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerWidthPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference.BackgroundImage;
import com.oracle.javafx.scenebuilder.sb.preferences.global.ParentRingColorPreference;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class GlobalKitPreferences {

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    protected final RootContainerHeightPreference rootContainerHeight;
    protected final RootContainerWidthPreference rootContainerWidth;
    protected final BackgroundImagePreference backgroundImage;
    protected final AlignmentGuidesColorPreference alignmentGuidesColor;
    protected final ParentRingColorPreference parentRingColor;
    protected final ThemePreference theme;
//    protected final GluonSwatchPreference gluonSwatch;
//    protected final GluonThemePreference gluonTheme;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public GlobalKitPreferences(RootContainerHeightPreference rootContainerHeight,
			RootContainerWidthPreference rootContainerWidth, BackgroundImagePreference backgroundImage,
			AlignmentGuidesColorPreference alignmentGuidesColor, ParentRingColorPreference parentRingColor,
			ThemePreference theme
			//, GluonSwatchPreference gluonSwatch, GluonThemePreference gluonTheme
			) {
		super();
		this.rootContainerHeight = rootContainerHeight;
		this.rootContainerWidth = rootContainerWidth;
		this.backgroundImage = backgroundImage;
		this.alignmentGuidesColor = alignmentGuidesColor;
		this.parentRingColor = parentRingColor;
		this.theme = theme;
		//this.gluonSwatch = gluonSwatch;
		//this.gluonTheme = gluonTheme;
	}

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/


	public double getRootContainerHeight() {
        return rootContainerHeight.getValue();
    }

    public void setRootContainerHeight(double value) {
        rootContainerHeight.setValue(value);
    }

    public double getRootContainerWidth() {
        return rootContainerWidth.getValue();
    }

    public void setRootContainerWidth(double value) {
        rootContainerWidth.setValue(value);
    }

    public BackgroundImage getBackgroundImage() {
        return backgroundImage.getValue();
    }

    public Image getBackgroundImageImage() { return backgroundImage.getBackgroundImageImage(); }

    public void setBackgroundImage(BackgroundImage value) {
        backgroundImage.setValue(value);
    }

    public Color getAlignmentGuidesColor() {
        return alignmentGuidesColor.getValue();
    }

    public void setAlignmentGuidesColor(Color value) {
        alignmentGuidesColor.setValue(value);
    }

    public Color getParentRingColor() {
        return parentRingColor.getValue();
    }

    public void setParentRingColor(Color value) {
        parentRingColor.setValue(value);
    }

    public Class<? extends Theme> getTheme() { return theme.getValue(); }

    public void setTheme(Class<? extends Theme> theme) { this.theme.setValue(theme); }

//    public GluonSwatch getSwatch() { return gluonSwatch.getValue(); }
//
//    public void setSwatch(GluonSwatch swatch) { this.gluonSwatch.setValue(swatch); }
//
//    public GluonTheme getGluonTheme() { return gluonTheme.getValue(); }
//
//    public void setGluonTheme(GluonTheme theme) { this.gluonTheme.setValue(theme); }

}
