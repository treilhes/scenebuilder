package com.oracle.javafx.scenebuilder.api.theme;

public abstract class AbstractGroup implements ThemeGroup {

	public String name;

	public AbstractGroup() {
		super();
		this.name = ThemeGroup.name(this.getClass());
	}

	@Override
	public String getName() {
		return name;
	}

}
