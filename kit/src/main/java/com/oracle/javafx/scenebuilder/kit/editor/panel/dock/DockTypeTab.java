package com.oracle.javafx.scenebuilder.kit.editor.panel.dock;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DockType;
import com.oracle.javafx.scenebuilder.api.View;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@Component
@Lazy
public class DockTypeTab implements DockType {

	public DockTypeTab() {}

	@Override
	public String getNameKey() {
		return "%viewtype.tabbed";
	}

	@Override
	public Parent computeRoot(List<View> views) {
		Tab[] tabs = views.stream().map(v -> new Tab(v.getName(), v.getRoot())).toArray(Tab[]::new);
		return new TabPane(tabs);
	}

}
