package com.oracle.javafx.scenebuilder.api.theme;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

/**
 * Theme group contract supported by Scene Builder Kit.
 */
public interface ThemeGroup {
    String getName();

    public static String name(Class<? extends AbstractGroup> cls) {
    	GroupMeta groupMeta = cls.getAnnotation(GroupMeta.class);
		if (groupMeta == null) {
			throw new RuntimeException("Class inheriting AbstractGroup class must be annotated with @ThemeMeta");
		}
		return I18N.getString(groupMeta.value());
	}


}
