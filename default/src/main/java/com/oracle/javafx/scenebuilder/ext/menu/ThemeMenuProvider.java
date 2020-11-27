package com.oracle.javafx.scenebuilder.ext.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ThemeMenuProvider implements MenuItemProvider {

	private final static String FIRST_SEPARATOR_ID = "firstSeparator";

	private final List<ThemeProvider> themeProviders;

	private final ThemePreference themePreference;

	private final ApplicationContext context;

	public ThemeMenuProvider(
			@Autowired ApplicationContext context,
			@Autowired @Lazy ThemePreference themePreference,
			@Autowired @Lazy List<ThemeProvider> themeProviders

			) {
		this.context = context;
		this.themeProviders = themeProviders;
		this.themePreference = themePreference;
	}

	@Override
	public List<MenuAttachment> menuItems() {
		return Arrays.asList(
				new ThemeAttachment(context, themePreference, themeProviders)
				);
	}

	public class ThemeAttachment implements MenuAttachment {

		private final ApplicationContext context;
		private final ThemePreference themePreference;

	    private final List<Class<? extends Theme>> themeClasses;

	    private Menu theme = null;


		public ThemeAttachment(ApplicationContext context, ThemePreference themePreference, List<ThemeProvider> themeProviders) {
			this.context = context;
			this.themePreference = themePreference;
			themeClasses = new ArrayList<>();
			themeProviders.forEach(tp -> themeClasses.addAll(tp.themes()));
		}

		@Override
		public String getTargetId() {
			return FIRST_SEPARATOR_ID;
		}

		@Override
		public PositionRequest getPositionRequest() {
			return PositionRequest.AsNextSibling;
		}

		@SuppressWarnings("unchecked")
		@Override
		public MenuItem getMenuItem() {

			if (theme != null) {
				return theme;
			}

			theme = new Menu(I18N.getString("menu.title.theme"));
			theme.setId("themeMenu");
			Map<String, List<Class<? extends Theme>>> groups = themeClasses.stream()
					.collect(Collectors.groupingBy(t -> Theme.group(t).getName()));

			ToggleGroup tg = new ToggleGroup();

			groups.keySet().stream().sorted().forEach(k -> {

				if (!theme.getItems().isEmpty()) {
					SeparatorMenuItem sep = new SeparatorMenuItem();
					sep.setId(k);
					theme.getItems().add(sep);
				}

				groups.get(k).stream()
					.sorted((t1,t2) -> Theme.name(t1).compareTo(Theme.name(t2)))
					.forEach(t -> {
						RadioMenuItem mi = new RadioMenuItem(Theme.name(t));
						mi.setToggleGroup(tg);
						mi.setSelected(themePreference.getValue() == t);
						mi.setUserData(t);
						mi.setOnAction((e) -> themePreference.setValue((Class<? extends Theme>) mi.getUserData()));
						theme.getItems().add(mi);
					});
			});

			themePreference.getObservableValue().addListener((ob, o, n) -> {
				context.getBean(ApplyCssContentAction.class).extend().checkAndPerform();
			});
			return theme;
		}
	}
}
