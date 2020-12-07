package com.oracle.javafx.scenebuilder.api.tooltheme;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;

/**
 * Theme contract supported by Scene Builder Kit.
 */
public interface ToolTheme extends StylesheetProvider2 {

	public static String name(Class<? extends ToolTheme> cls) {
		ToolThemeMeta themeMeta = cls.getAnnotation(ToolThemeMeta.class);
		if (themeMeta == null) {
			throw new RuntimeException("Class implementing ToolTheme interface must be annotated with @ToolThemeMeta");
		}
		return I18N.getString(themeMeta.name());
	}
}
