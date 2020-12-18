package com.oracle.javafx.scenebuilder.ext.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ApplyCssContentThemeExtension extends AbstractActionExtension<ApplyCssContentAction> {

	private final ThemePreference themePreference;
	private final ApplicationContext context;

	public ApplyCssContentThemeExtension(
			@Autowired ApplicationContext context,
			@Autowired @Lazy ThemePreference themePreference
			) {
		super();
		this.context = context;
		this.themePreference = themePreference;
	}

	@Override
	public boolean canPerform() {
		return themePreference.getValue() != null;
	}

	@Override
	public void prePerform() {
		Theme theme = context.getBean(themePreference.getValue());
		getExtendedAction().getActionConfig().setUserAgentStylesheet(theme.getUserAgentStylesheet());
		getExtendedAction().getActionConfig().getStylesheets().addAll(theme.getStylesheets());
	}



}
