package com.oracle.javafx.scenebuilder.ext.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import lombok.Getter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
		nameKey = "action.name.show.jar.analysis.report",
		descriptionKey = "action.description.show.jar.analysis.report",
		accelerator = "CTRL+J")
public class ApplyI18nContentAction extends AbstractAction implements InitWithDocument {

	private ApplyI18nContentConfig config;

	private final DocumentManager documentManager;

	public ApplyI18nContentAction(
			@Autowired ApplicationContext context,
			@Autowired @Lazy DocumentManager documentManager) {
		super(context);
		this.documentManager = documentManager;
	}

	public synchronized ApplyI18nContentConfig getActionConfig() {
		if (config == null) {
			config = new ApplyI18nContentConfig();
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
		documentManager.i18nResourceConfig().onNext(getActionConfig());
	}

	public static class ApplyI18nContentConfig implements I18nResourceProvider {
		private @Getter List<ResourceBundle> bundles = new ArrayList<>();
	}

	@Override
	public void init() {
		extend().checkAndPerform();
	}
}
