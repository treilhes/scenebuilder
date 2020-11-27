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
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;

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
	private final ApplicationContext context;

	public GluonMenuProvider(
			@Autowired ApplicationContext context,
			@Autowired @Lazy GluonSwatchPreference gluonSwatchPreference
			) {
		this.context = context;
		this.gluonSwatchPreference = gluonSwatchPreference;
	}

	@Override
	public List<MenuAttachment> menuItems() {
		return Arrays.asList(
				new GluonThemeAttachment(context, gluonSwatchPreference)
				);
	}

	public class GluonThemeAttachment implements MenuAttachment {

	    private final GluonSwatchPreference gluonSwatchPreference;
		private final ApplicationContext context;

		private Menu gluonSwatchMenu = null;

		public GluonThemeAttachment(ApplicationContext context, GluonSwatchPreference gluonSwatchPreference) {
			this.context = context;
			this.gluonSwatchPreference = gluonSwatchPreference;
		}

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

			Arrays.stream(GluonSwatch.values()).forEach(g -> {
				RadioMenuItem mi = new RadioMenuItem(g.toString());
				mi.setToggleGroup(tg);
				mi.setUserData(g);
				mi.setGraphic(g.createGraphic());
				mi.setSelected(gluonSwatchPreference.getValue() == g);
				mi.setOnAction((e) -> gluonSwatchPreference.setValue((GluonSwatch) mi.getUserData()));
				gluonSwatchMenu.getItems().add(mi);
			});

			gluonSwatchPreference.getObservableValue().addListener((ob, o, n) -> {
				context.getBean(ApplyCssContentAction.class).extend().checkAndPerform();
			});
			return gluonSwatchMenu;
		}

	}
}
