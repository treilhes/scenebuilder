package com.oracle.javafx.scenebuilder.api.tooltheme;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public abstract class AbstractToolTheme implements ToolTheme {

	private final String name;
	private final @Getter String userAgentStylesheet;
	private final @Getter List<String> stylesheets = new ArrayList<>();

	public AbstractToolTheme(String userAgentStylesheet, List<String> stylesheets) {
		super();
		this.name = ToolTheme.name(this.getClass());
		this.userAgentStylesheet = userAgentStylesheet;
		if (stylesheets != null) {
			this.stylesheets.addAll(stylesheets);
		}
	}

	@Override
    public String toString() {
        return name;
    }
}
