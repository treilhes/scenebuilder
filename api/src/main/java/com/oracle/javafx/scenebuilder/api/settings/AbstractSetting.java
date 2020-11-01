package com.oracle.javafx.scenebuilder.api.settings;

public class AbstractSetting implements Setting {

	public AbstractSetting() {}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
