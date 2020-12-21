/*
 * Copyright (c) 2016, 2019, Gluon and/or its affiliates.
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

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.app.preferences.global.IgnoreVersionPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.ImportedGluonJarsPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.LastSentTrackingInfoDatePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsSizePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationEmailPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationHashPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationOptInPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.ShowUpdateDialogDatePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.WildcardImportsPreference;
import com.oracle.javafx.scenebuilder.ext.theme.global.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonThemePreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController.DISPLAY_MODE;
import com.oracle.javafx.scenebuilder.kit.preferences.GlobalKitPreferences;
import com.oracle.javafx.scenebuilder.kit.preferences.global.AlignmentGuidesColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.CssTableColumnsOrderingReversedPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.CssTableColumnsOrderingReversedPreference.CSSAnalyzerColumnsOrder;
import com.oracle.javafx.scenebuilder.sb.preferences.global.AccordionAnimationPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.DisplayModePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.DisplayOptionPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ParentRingColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerWidthPreference;

/**
 * Defines preferences global to the SB application.
 */
@Component
public class GlobalPreferences extends GlobalKitPreferences {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/

    // Default values
    static final double DEFAULT_ROOT_CONTAINER_HEIGHT = 400;
    static final double DEFAULT_ROOT_CONTAINER_WIDTH = 600;

    /***************************************************************************
     *                                                                         *
     * Instance fields                                                         *
     *                                                                         *
     **************************************************************************/

    // Global preferences
    //private final ToolThemePreference toolTheme;
    private final DisplayModePreference libraryDisplayOption;
    private final DisplayOptionPreference hierarchyDisplayOption;

    private final CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversed;
    //private final CssAnalyzerColumnsOrderPreference cssAnalyzerColumnsOrder;

    private final RecentItemsSizePreference recentItemsSize;
    private final AccordionAnimationPreference accordionAnimation;
    private final WildcardImportsPreference wildcardImports;
    private final RecentItemsPreference recentItems;

    private final ShowUpdateDialogDatePreference showUpdateDialogDate;
    private final IgnoreVersionPreference ignoreVersion;

    private final ImportedGluonJarsPreference importedGluonJars;

    private final RegistrationHashPreference registrationHash;
    private final RegistrationEmailPreference registrationEmail;
    private final RegistrationOptInPreference registrationOptIn;

    private final LastSentTrackingInfoDatePreference lastSentTrackingInfoDate;

    //final static Integer[] recentItemsSizes = {5, 10, 15, 20};

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public GlobalPreferences(RootContainerHeightPreference rootContainerHeight,
			RootContainerWidthPreference rootContainerWidth, BackgroundImagePreference backgroundImage,
			AlignmentGuidesColorPreference alignmentGuidesColor, ParentRingColorPreference parentRingColor,
			ThemePreference theme, GluonSwatchPreference gluonSwatch, GluonThemePreference gluonTheme,
			//ToolThemePreference toolTheme,
			DisplayModePreference libraryDisplayOption,
			DisplayOptionPreference hierarchyDisplayOption,

			CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversed,
			//CssAnalyzerColumnsOrderPreference cssAnalyzerColumnsOrder,

			RecentItemsSizePreference recentItemsSize, AccordionAnimationPreference accordionAnimation,
			WildcardImportsPreference wildcardImports, RecentItemsPreference recentItems,
			ShowUpdateDialogDatePreference showUpdateDialogDate, IgnoreVersionPreference ignoreVersion,
			ImportedGluonJarsPreference importedGluonJars, RegistrationHashPreference registrationHash,
			RegistrationEmailPreference registrationEmail, RegistrationOptInPreference registrationOptIn,
			LastSentTrackingInfoDatePreference lastSentTrackingInfoDate) {
		super(
				rootContainerHeight, rootContainerWidth, backgroundImage, alignmentGuidesColor, parentRingColor, theme
				//,gluonSwatch, gluonTheme
				);
		//this.toolTheme = toolTheme;
		this.libraryDisplayOption = libraryDisplayOption;
		this.hierarchyDisplayOption = hierarchyDisplayOption;

		this.cssTableColumnsOrderingReversed = cssTableColumnsOrderingReversed;
		//this.cssAnalyzerColumnsOrder = cssAnalyzerColumnsOrder;

		this.recentItemsSize = recentItemsSize;
		this.accordionAnimation = accordionAnimation;
		this.wildcardImports = wildcardImports;
		this.recentItems = recentItems;
		this.showUpdateDialogDate = showUpdateDialogDate;
		this.ignoreVersion = ignoreVersion;
		this.importedGluonJars = importedGluonJars;
		this.registrationHash = registrationHash;
		this.registrationEmail = registrationEmail;
		this.registrationOptIn = registrationOptIn;
		this.lastSentTrackingInfoDate = lastSentTrackingInfoDate;
	}

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

//    public ToolTheme getToolTheme() {
//        return toolTheme.getValue();
//    }
//
//	public void setToolTheme(ToolTheme value) {
//        toolTheme.setValue(value);
//    }

    public DISPLAY_MODE getLibraryDisplayOption() {
        return libraryDisplayOption.getValue();
    }

    public void setLibraryDisplayOption(DISPLAY_MODE value) {
        libraryDisplayOption.setValue(value);
    }

    public void updateLibraryDisplayOption(DISPLAY_MODE value) {
        libraryDisplayOption.setValue(value).writeToJavaPreferences();
    }

    public DisplayOption getHierarchyDisplayOption() {
        return hierarchyDisplayOption.getValue();
    }

    public void setHierarchyDisplayOption(DisplayOption value) {
        hierarchyDisplayOption.setValue(value);
    }

    public void updateHierarchyDisplayOption(DisplayOption value) {
        hierarchyDisplayOption.setValue(value).writeToJavaPreferences();
    }

    public CSSAnalyzerColumnsOrder getDefaultCSSAnalyzerColumnsOrder() {
        if (CssTableColumnsOrderingReversedPreference.PREFERENCE_DEFAULT_VALUE) {
            return CSSAnalyzerColumnsOrder.DEFAULTS_LAST;
        } else {
            return CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;
        }
    }

    public CSSAnalyzerColumnsOrder getCSSAnalyzerColumnsOrder() {
        if (isCssTableColumnsOrderingReversed()) {
            return CSSAnalyzerColumnsOrder.DEFAULTS_LAST;
        } else {
            return CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;
        }
    }

    public void setCSSAnalyzerColumnsOrder(CSSAnalyzerColumnsOrder value) {
        switch (value) {
            case DEFAULTS_FIRST:
                setCssTableColumnsOrderingReversed(false);
                break;
            case DEFAULTS_LAST:
                setCssTableColumnsOrderingReversed(true);
                break;
            default:
                assert false;
        }
    }

    public boolean isCssTableColumnsOrderingReversed() {
        return cssTableColumnsOrderingReversed.getValue();
    }

    public void setCssTableColumnsOrderingReversed(boolean value) {
        cssTableColumnsOrderingReversed.setValue(value);
    }

//    public CSSAnalyzerColumnsOrder getCssAnalyzerColumnsOrder() {
//		return cssAnalyzerColumnsOrder.getValue();
//	}
//
//    public void setCssAnalyzerColumnOrder(CSSAnalyzerColumnsOrder value) {
//		cssAnalyzerColumnsOrder.setValue(value);
//	}

    public List<String> getRecentItems() {
		return recentItems.getValue();
	}

    public void setRecentItems(List<String> value) {
		recentItems.setValue(value);
	}

    public int getRecentItemsSize() {
        return recentItemsSize.getValue();
    }

	public void setRecentItemsSize(int value) {
        recentItemsSize.setValue(value);
    }

    public void updateRegistrationFields(String hash, String email, Boolean optIn) {
        registrationHash.setValue(hash).writeToJavaPreferences();

        if (email != null) {
            registrationEmail.setValue(email).writeToJavaPreferences();;
        }

        if (optIn != null) {
            registrationOptIn.setValue(optIn).writeToJavaPreferences();
        }
    }

    public String getRegistrationHash() {
        return registrationHash.getValue();
    }

    public void setRegistrationHash(String registrationHash) {
        this.registrationHash.setValue(registrationHash);
    }

    public String getRegistrationEmail() {
        return registrationEmail.getValue();
    }

    public void setRegistrationEmail(String registrationEmail) {
        this.registrationEmail.setValue(registrationEmail);
    }

    public boolean isRegistrationOptIn() {
        return registrationOptIn.getValue();
    }

    public void setRegistrationOptIn(boolean registrationOptIn) {
        this.registrationOptIn.setValue(registrationOptIn);
    }

    public void setShowUpdateDialogAfter(LocalDate showUpdateDialogDate) {
        this.showUpdateDialogDate.setValue(showUpdateDialogDate).writeToJavaPreferences();
    }

    public LocalDate getShowUpdateDialogDate() {
        return showUpdateDialogDate.getValue();
    }

    public void setIgnoreVersion(String ignoreVersion) {
        this.ignoreVersion.setValue(ignoreVersion).writeToJavaPreferences();
    }

    public String getIgnoreVersion() {
        return ignoreVersion.getValue();
    }

    public void setImportedGluonJars(String[] importedJars) {
        this.importedGluonJars.setValue(importedJars).writeToJavaPreferences();
    }

    public String[] getImportedGluonJars() {
        return importedGluonJars.getValue();
    }

    public LocalDate getLastSentTrackingInfoDate() {
        return lastSentTrackingInfoDate.getValue();
    }

    public void setLastSentTrackingInfoDate(LocalDate date) {
        lastSentTrackingInfoDate.setValue(date).writeToJavaPreferences();
    }

    public boolean isAccordionAnimation() {
        return accordionAnimation.getValue();
    }

    public void setAccordionAnimation(boolean accordionAnimation) {
        this.accordionAnimation.setValue(accordionAnimation);
    }

    public boolean isWildcardImports() {
        return wildcardImports.getValue();
    }

    public void setWildcardImports(boolean wildcardImports) {
        this.wildcardImports.setValue(wildcardImports);
    }

//    private static Image getShadowImage() {
//        final URL url = PreferencesRecordGlobal.class.getResource("background-shadow.png"); //NOI18N
//        return new Image(url.toExternalForm());
//    }
}
