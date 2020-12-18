package com.oracle.javafx.scenebuilder.ext.actions;

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
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
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
public class ApplyCssContentAction extends AbstractAction implements InitWithDocument {

	private ApplyCssContentConfig config;

	private final DocumentManager documentManager;

	public ApplyCssContentAction(
			@Autowired ApplicationContext context,
			@Autowired @Lazy DocumentManager documentManager) {
		super(context);
		this.documentManager = documentManager;
	}

	public synchronized ApplyCssContentConfig getActionConfig() {
		if (config == null) {
			config = new ApplyCssContentConfig();
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
		documentManager.stylesheetConfig().onNext(getActionConfig());
	}

	public static class ApplyCssContentConfig implements StylesheetProvider2 {
		private @Getter @Setter String userAgentStylesheet;
		private @Getter List<String> stylesheets = new ArrayList<>();
	}

	@Override
	public void init() {
		extend().checkAndPerform();
	}

}
