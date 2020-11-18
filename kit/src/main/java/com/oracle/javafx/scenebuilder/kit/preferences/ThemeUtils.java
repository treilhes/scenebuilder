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
package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference.GluonTheme;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference.Theme;

/**
 * This class contains static methods that depends on theme.
 *
 * @treatAsPrivate
 */
public class ThemeUtils {
    
    /**
     * Gluon Glisten package
     */
    public static final String GLUON_PACKAGE = "com.gluonhq.charm.glisten";

    /**
     * This URL is where you go when the user takes Scene Builder Help action (shortcut F1)
     */
    public static final String DOCUMENTATION_URL = "https://docs.oracle.com/javafx/index.html"; //NOI18N

    /**
     * Javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String JAVADOC_HOME = "https://openjfx.io/javadoc/11/"; //NOI18N

    /**
     * Gluon javadoc home (for Inspector and CSS Analyzer properties)
     */
    public final static String GLUON_JAVADOC_HOME = "http://docs.gluonhq.com/charm/javadoc/" + "latest" +"/"; //NOI18N

    /**
     * scene builder specific tweaks to Gluon theme
     */
    public static final String GLUON_DOCUMENT_STYLESHEET = "com/oracle/javafx/scenebuilder/app/css/GluonDocument.css";

    /**
     * Default theme
     */
    public static final Theme DEFAULT_THEME = Theme.MODENA;

    /**
     * Default Gluon Swatch
     */
    public static final GluonSwatch DEFAULT_SWATCH = GluonSwatch.BLUE;

    /**
     * Default Gluon Theme
     */
    public static final GluonTheme DEFAULT_GLUON_THEME = GluonTheme.LIGHT;

    public static String getPlatformThemeStylesheetURL() {
        // Return USER_AGENT css, which is Modena for fx 8.0
        return Theme.MODENA.getStylesheetURL();
    }

    public static String getGluonDocumentStylesheetURL() {
        return GLUON_DOCUMENT_STYLESHEET;
    }

    public static boolean isModena(Theme theme) {
        return theme.toString().startsWith("MODENA");
    }
    
    public static boolean isModenaBlackonwhite(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("BLACK_ON_WHITE");
    }
    
    public static boolean isModenaWhiteonblack(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("WHITE_ON_BLACK");
    }
    
    public static boolean isModenaYellowonblack(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("YELLOW_ON_BLACK");
    }
    
    public static boolean isModenaHighContrast(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("HIGH_CONTRAST");
    }
    
    public static boolean isModenaTouch(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("TOUCH");
    }
    
    public static boolean isModenaTouchHighContrast(Theme theme) {
        return isModena(theme)
                && theme.toString().contains("HIGH_CONTRAST")
                && theme.toString().contains("TOUCH");
    }
    
    public static boolean isCaspian(Theme theme) {
        return theme.toString().startsWith("CASPIAN");
    }

    public static boolean isGluonMobileLight(Theme theme) { return theme == Theme.GLUON_MOBILE_LIGHT; }

    public static boolean isGluonMobileDark(Theme theme) {
        return theme == Theme.GLUON_MOBILE_DARK;
    }



}
