package com.oracle.javafx.scenebuilder.api.theme;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public abstract class AbstractTheme implements Theme {

	private final String name;
	private final @Getter Class<? extends AbstractGroup> themeGroupClass;
	private final @Getter String userAgentStylesheet;
	private final @Getter List<String> stylesheets = new ArrayList<>();

	public AbstractTheme(String userAgentStylesheet, List<String> stylesheets) {
		super();
		this.name = Theme.name(this.getClass());
		this.themeGroupClass = Theme.group(this.getClass());
		this.userAgentStylesheet = userAgentStylesheet == null || userAgentStylesheet.isEmpty() ? null : userAgentStylesheet;

		if (stylesheets != null) {
			this.stylesheets.addAll(stylesheets);
		}
	}

	@Override
    public String toString() {
        return name;
    }
}
