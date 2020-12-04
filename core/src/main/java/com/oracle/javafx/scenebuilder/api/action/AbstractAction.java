package com.oracle.javafx.scenebuilder.api.action;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.scene.input.KeyCombination;

public abstract class AbstractAction implements Action {

	private final ApplicationContext context;
	private final String nameI18nKey;
	private final String descriptionI18nKey;
	private final String rawAccelerator;
	private ExtendedAction<?> extendedAction;

	public AbstractAction(ApplicationContext context) {
		this.context = context;

		ActionMeta actionMeta = this.getClass().getAnnotation(ActionMeta.class);

		if (actionMeta == null) {
			throw new RuntimeException("Class inheriting AbstractAction class must be annotated with @ActionMeta");
		}

		nameI18nKey = actionMeta.nameKey();
		descriptionI18nKey = actionMeta.descriptionKey();
		rawAccelerator = actionMeta.accelerator();
	}

	@Override
	public String getUniqueId() {
		return this.getClass().getName();
	}

	@Override
	public String getName() {
		return nameI18nKey == null ? null : I18N.getString(nameI18nKey);
	}

	@Override
	public String getDescription() {
		return descriptionI18nKey == null ? null : I18N.getString(descriptionI18nKey);
	}

	@Override
	public KeyCombination getWishedAccelerator() {
		if (rawAccelerator == null) {
			return null;
		}
		return KeyCombination.valueOf(rawAccelerator);
	}

	@Override
	public void checkAndPerform() {
		if (canPerform()) {
			perform();
		}
	}

	@Override
	public ExtendedAction<?> extend() {
		if (this.getClass().isAssignableFrom(ExtendedAction.class)) {
			return (ExtendedAction<?>) this;
		}
		if (extendedAction == null) {
			extendedAction = getContext().getBean(ExtendedAction.class, this);
		}
		return extendedAction;
	}

	public ApplicationContext getContext() {
		return context;
	}
}
