package com.oracle.javafx.scenebuilder.api.tooltheme;

import java.util.List;

public interface ToolThemeProvider {
	List<Class<? extends ToolTheme>> toolThemes();
}
