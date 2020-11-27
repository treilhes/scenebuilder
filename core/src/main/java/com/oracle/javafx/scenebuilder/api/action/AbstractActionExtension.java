package com.oracle.javafx.scenebuilder.api.action;

public abstract class AbstractActionExtension<T extends Action> implements ActionExtension<T> {

	private T extendedAction;

	public AbstractActionExtension() {
	}

	@Override
	public void setExtendedAction(T action) {
		extendedAction = action;
	}

	protected T getExtendedAction() {
		return extendedAction;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public void prePerform() {}

	@Override
	public void postPerform() {}


}
