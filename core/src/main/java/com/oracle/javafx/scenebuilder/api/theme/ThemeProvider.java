package com.oracle.javafx.scenebuilder.api.theme;

import java.util.List;

public interface ThemeProvider {
	List<Class<? extends Theme>> themes();
}
