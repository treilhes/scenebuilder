package com.oracle.javafx.scenebuilder.gluon.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonThemePreference;
import com.oracle.javafx.scenebuilder.gluon.theme.GluonThemesList;

//@Component
//@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Deprecated
public class GluonThemeApplyCssContentActionExtension extends AbstractActionExtension<ApplyCssContentAction> {

	private final ThemePreference themePreference;
	private final GluonThemePreference gluonThemePreference;

	public GluonThemeApplyCssContentActionExtension(
			@Autowired @Lazy ThemePreference themePreference,
			@Autowired @Lazy GluonThemePreference gluonThemePreference
			) {
		super();
		this.themePreference = themePreference;
		this.gluonThemePreference = gluonThemePreference;
	}

	@Override
	public boolean canPerform() {
		return themePreference.getValue() == GluonThemesList.GluonMobileDark.class
				|| themePreference.getValue() == GluonThemesList.GluonMobileLight.class;
	}

	@Override
	public void prePerform() {
		getExtendedAction().getActionConfig().getStylesheets()
			.add(gluonThemePreference.getValue().getStylesheetURL());
	}



}
