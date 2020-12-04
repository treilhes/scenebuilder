package com.oracle.javafx.scenebuilder.ext.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ApplyCssContentUserStylesheetsExtension extends AbstractActionExtension<ApplyCssContentAction> {

	private final UserStylesheetsPreference userStylesheetsPreference;

	public ApplyCssContentUserStylesheetsExtension(
			@Autowired @Lazy UserStylesheetsPreference userStylesheetsPreference
			) {
		super();
		this.userStylesheetsPreference = userStylesheetsPreference;
	}

	@Override
	public boolean canPerform() {
		return userStylesheetsPreference.getValue() != null && !userStylesheetsPreference.getValue().isEmpty();
	}

	@Override
	public void prePerform() {
		getExtendedAction().getActionConfig().getStylesheets().addAll(userStylesheetsPreference.getValue());
	}



}
