package com.oracle.javafx.scenebuilder.sb.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolTheme;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.sb.preferences.global.ToolThemePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ApplyToolCssThemeExtension extends AbstractActionExtension<ApplyToolCssAction> implements InitWithDocument {

	private final ToolThemePreference toolThemePreference;
	private final ApplicationContext context;

	public ApplyToolCssThemeExtension(
			@Autowired ApplicationContext context,
			@Autowired @Lazy ToolThemePreference toolThemePreference
			) {
		super();
		this.context = context;
		this.toolThemePreference = toolThemePreference;
	}

	@Override
	public boolean canPerform() {
		return toolThemePreference.getValue() != null;
	}

	@Override
	public void prePerform() {
		ToolTheme toolTheme = context.getBean(toolThemePreference.getValue());
		getExtendedAction().getActionConfig().setUserAgentStylesheet(toolTheme.getUserAgentStylesheet());
		getExtendedAction().getActionConfig().getStylesheets().addAll(toolTheme.getStylesheets());
	}

    @Override
    public void init() {
        toolThemePreference.getObservableValue().addListener(
                (ob, o, n) -> context.getBean(ApplyToolCssAction.class).extend().perform());
    }



}
