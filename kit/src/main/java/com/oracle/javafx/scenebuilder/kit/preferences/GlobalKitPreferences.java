package com.oracle.javafx.scenebuilder.kit.preferences;

import com.oracle.javafx.scenebuilder.kit.preferences.global.AlignmentGuidesColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ParentRingColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerWidthPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference.BackgroundImage;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference.GluonTheme;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference.Theme;

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
    protected final GluonSwatchPreference gluonSwatch;
    protected final GluonThemePreference gluonTheme;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public GlobalKitPreferences(RootContainerHeightPreference rootContainerHeight,
			RootContainerWidthPreference rootContainerWidth, BackgroundImagePreference backgroundImage,
			AlignmentGuidesColorPreference alignmentGuidesColor, ParentRingColorPreference parentRingColor,
			ThemePreference theme, GluonSwatchPreference gluonSwatch, GluonThemePreference gluonTheme) {
		super();
		this.rootContainerHeight = rootContainerHeight;
		this.rootContainerWidth = rootContainerWidth;
		this.backgroundImage = backgroundImage;
		this.alignmentGuidesColor = alignmentGuidesColor;
		this.parentRingColor = parentRingColor;
		this.theme = theme;
		this.gluonSwatch = gluonSwatch;
		this.gluonTheme = gluonTheme;
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

    public Theme getTheme() { return theme.getValue(); }

    public void setTheme(Theme theme) { this.theme.setValue(theme); }

    public GluonSwatch getSwatch() { return gluonSwatch.getValue(); }

    public void setSwatch(GluonSwatch swatch) { this.gluonSwatch.setValue(swatch); }

    public GluonTheme getGluonTheme() { return gluonTheme.getValue(); }

    public void setGluonTheme(GluonTheme theme) { this.gluonTheme.setValue(theme); }

}
