package com.oracle.javafx.scenebuilder.kit.editor.panel.dock;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DockType;
import com.oracle.javafx.scenebuilder.api.View;

import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

@Component
@Lazy
public class DockTypeAccordion implements DockType {

	public DockTypeAccordion() {}

	@Override
	public String getNameKey() {
		return "%viewtype.accordion";
	}

	@Override
	public Parent computeRoot(List<View> views) {
		TitledPane[] panes = views.stream().map(v -> new TitledPane(v.getName(), v.getPanelRoot())).toArray(TitledPane[]::new);
		return new Accordion(panes);
	}

}
