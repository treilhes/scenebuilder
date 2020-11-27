package com.oracle.javafx.scenebuilder.api.theme;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

/**
 * Theme contract supported by Scene Builder Kit.
 */
public interface Theme extends StylesheetProvider2 {

	Class<? extends AbstractGroup> getThemeGroupClass();

	public static String name(Class<? extends Theme> cls) {
		ThemeMeta themeMeta = cls.getAnnotation(ThemeMeta.class);
		if (themeMeta == null) {
			throw new RuntimeException("Class implementing Theme interface must be annotated with @ThemeMeta");
		}
		return I18N.getString(themeMeta.name());
	}

	public static Class<? extends AbstractGroup> group(Class<? extends Theme> cls) {
		ThemeMeta themeMeta = cls.getAnnotation(ThemeMeta.class);
		if (themeMeta == null) {
			throw new RuntimeException("Class implementing Theme interface must be annotated with @ThemeMeta");
		}
		return themeMeta.group();
	}


}
