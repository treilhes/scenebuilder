package com.oracle.javafx.scenebuilder.sb.tooltheme;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.tooltheme.AbstractToolTheme;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolTheme;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolThemeMeta;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolThemeProvider;

@Component
@Qualifier("default")
public class DefaultToolThemesList implements ToolThemeProvider {

	public DefaultToolThemesList() {}

	@Override
	public List<Class<? extends ToolTheme>> toolThemes() {
		return Arrays.asList(
				Default.class,
				Dark.class
				);
	}

	@Component
	@Lazy
	@ToolThemeMeta(name = "prefs.tool.theme.default")
	public static class Default extends AbstractToolTheme {
		public Default() {
			super(null, Arrays.asList("com/oracle/javafx/scenebuilder/kit/css/ThemeDefault.css"));
		}
	}

    @Component
    @Lazy
    @ToolThemeMeta(name = "prefs.tool.theme.dark")
    public static class Dark extends AbstractToolTheme {
        public Dark() {
            super(null, Arrays.asList("com/oracle/javafx/scenebuilder/kit/css/ThemeDark.css"));
        }
    }

}
