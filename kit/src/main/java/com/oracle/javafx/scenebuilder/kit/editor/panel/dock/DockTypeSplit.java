package com.oracle.javafx.scenebuilder.kit.editor.panel.dock;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DockType;
import com.oracle.javafx.scenebuilder.api.View;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

@Component
@Lazy
public class DockTypeSplit implements DockType {

	public DockTypeSplit() {}

	@Override
	public String getNameKey() {
		return "%viewtype.split";
	}

	@Override
	public Parent computeRoot(List<View> views) {
		Node[] nodes = views.stream().map(v -> v.getPanelRoot()).toArray(Node[]::new);
		return new SplitPane(nodes);
	}

}
