package com.oracle.javafx.scenebuilder.gluon.theme;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.api.theme.AbstractTheme;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeMeta;
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.DefaultThemesList;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
@Component
public class GluonThemesList implements ThemeProvider {

	private GluonThemesList() {}

	@Override
	public List<Class<? extends Theme>> themes() {
		return Arrays.asList(
				GluonMobileLight.class,
				GluonMobileDark.class
				);
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
	@ThemeMeta(name = "title.theme.gluon_mobile_light", group = GluonGroup.class)
	public static class GluonMobileLight extends AbstractTheme {
		public GluonMobileLight(@Autowired GluonSwatchPreference gluonSwatchPreference) {
			super(new DefaultThemesList.Modena().getUserAgentStylesheet(),
					Arrays.asList(
							GlistenStyleClasses.impl_loadResource("glisten.gls"),
							"com/oracle/javafx/scenebuilder/gluon/css/GluonDocument.css",
							gluonSwatchPreference.getValue().getStylesheetURL(),
							GlistenStyleClasses.impl_loadResource("theme_light.gls")
							)
					);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
	@ThemeMeta(name = "title.theme.gluon_mobile_dark", group = GluonGroup.class)
	public static class GluonMobileDark extends AbstractTheme {
		public GluonMobileDark(@Autowired GluonSwatchPreference gluonSwatchPreference) {
			super(new DefaultThemesList.Modena().getUserAgentStylesheet(),
					Arrays.asList(
							GlistenStyleClasses.impl_loadResource("glisten.gls"),
							"com/oracle/javafx/scenebuilder/app/css/GluonDocument.css",
							gluonSwatchPreference.getValue().getStylesheetURL(),
							GlistenStyleClasses.impl_loadResource("theme_dark.gls")
							)
					);
		}
	}
}
