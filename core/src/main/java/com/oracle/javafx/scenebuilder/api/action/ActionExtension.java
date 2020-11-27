package com.oracle.javafx.scenebuilder.api.action;

public interface ActionExtension<T extends Action> {

	void setExtendedAction(T action);

	public boolean canPerform();

	public void prePerform();

	public void postPerform();

}
