package com.oracle.javafx.scenebuilder.sb.actions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
		nameKey = "action.name.show.jar.analysis.report",
		descriptionKey = "action.description.show.jar.analysis.report",
		accelerator = "CTRL+J")
public class ApplyToolCssAction extends AbstractAction implements InitWithDocument {

	private ApplyToolCssConfig config;

	private final SceneBuilderManager sceneBuilderManager;

	public ApplyToolCssAction(
			@Autowired ApplicationContext context,
			@Autowired @Lazy SceneBuilderManager sceneBuilderManager) {
		super(context);
		this.sceneBuilderManager = sceneBuilderManager;
	}

	public synchronized ApplyToolCssConfig getActionConfig() {
		if (config == null) {
			config = new ApplyToolCssConfig();
		}
		return config;
	}

	public synchronized void resetActionConfig() {
		config = null;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public void perform() {
		assert getActionConfig() != null;
		sceneBuilderManager.stylesheetConfig().onNext(getActionConfig());
	}

	public static class ApplyToolCssConfig implements StylesheetProvider2 {
		private @Getter @Setter String userAgentStylesheet;
		private @Getter List<String> stylesheets = new ArrayList<>();
	}

	@Override
	public void init() {
		extend().checkAndPerform();
	}

}
