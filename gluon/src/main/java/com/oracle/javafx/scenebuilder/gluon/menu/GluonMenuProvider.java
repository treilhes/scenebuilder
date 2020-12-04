package com.oracle.javafx.scenebuilder.gluon.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.gluon.theme.GluonThemesList;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonMenuProvider implements MenuItemProvider {

	private final static String THEME_MENU_ID = "themeMenu";
	private final GluonSwatchPreference gluonSwatchPreference;
	private final ThemePreference themePreference;
	private final ApplicationContext context;

	public GluonMenuProvider(
			@Autowired ApplicationContext context,
			@Autowired @Lazy ThemePreference themePreference,
			@Autowired @Lazy GluonSwatchPreference gluonSwatchPreference
			) {
		this.context = context;
		this.gluonSwatchPreference = gluonSwatchPreference;
		this.themePreference = themePreference;
	}

	@Override
	public List<MenuAttachment> menuItems() {
		return Arrays.asList(
				new GluonThemeAttachment()
				);
	}

	public class GluonThemeAttachment implements MenuAttachment {

		private Menu gluonSwatchMenu = null;

		public GluonThemeAttachment() {}

		@Override
		public String getTargetId() {
			return THEME_MENU_ID;
		}

		@Override
		public PositionRequest getPositionRequest() {
			return PositionRequest.AsNextSibling;
		}

		@Override
		public MenuItem getMenuItem() {

			if (gluonSwatchMenu != null) {
				return gluonSwatchMenu;
			}

			gluonSwatchMenu = new Menu(I18N.getString("menu.title.gluon.swatch"));

			ToggleGroup tg = new ToggleGroup();

			boolean disabled =  isMenuDisabled();

			Arrays.stream(GluonSwatch.values()).forEach(g -> {
				RadioMenuItem mi = new RadioMenuItem(g.toString());
				mi.setToggleGroup(tg);
				mi.setUserData(g);
				mi.setGraphic(g.createGraphic());
				mi.setDisable(disabled);
				mi.setSelected(gluonSwatchPreference.getValue() == g);
				mi.setOnAction((e) -> gluonSwatchPreference.setValue((GluonSwatch) mi.getUserData()));
				gluonSwatchMenu.getItems().add(mi);
			});

			gluonSwatchPreference.getObservableValue().addListener((ob, o, n) -> {
				updateMenu();
				context.getBean(ApplyCssContentAction.class).extend().checkAndPerform();
			});
			themePreference.getObservableValue().addListener((ob, o, n) -> {
				updateMenu();
			});
			return gluonSwatchMenu;
		}

		private void updateMenu() {
			boolean disable =  isMenuDisabled();
			gluonSwatchMenu.getItems().stream()
				.filter(mi -> mi.getClass().isAssignableFrom(RadioMenuItem.class))
				.forEach(mi -> {
					RadioMenuItem rmi = (RadioMenuItem)mi;
					rmi.setDisable(disable);
					rmi.setSelected(gluonSwatchPreference.getValue() == mi.getUserData());
				});
		}
	}

	public boolean isMenuDisabled() {
		Class<? extends Theme> theme = themePreference.getValue();
		return theme != GluonThemesList.GluonMobileLight.class
				&& theme != GluonThemesList.GluonMobileDark.class;
	}
}
