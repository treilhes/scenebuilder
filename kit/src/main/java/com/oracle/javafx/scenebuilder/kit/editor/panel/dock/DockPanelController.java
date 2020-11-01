package com.oracle.javafx.scenebuilder.kit.editor.panel.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dock;
import com.oracle.javafx.scenebuilder.api.DockType;
import com.oracle.javafx.scenebuilder.api.View;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.scene.layout.Pane;
import lombok.Getter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DockPanelController implements Dock {

	@Autowired
	DockManager dockManager;
	
	@Autowired
	ViewManager viewManager;

	@Autowired
	List<DockType> dockTypes;

	private @Getter UUID id;
	private @Getter String label;
	private @Getter Pane content;
	private @Getter DockType dockType;

	private final List<View> views = new ArrayList<>();

	public DockPanelController(String label) {
		this.id = UUID.randomUUID();
		this.label = label;
		this.content = new Pane();

		dockManager.dockCreated().onNext(this);
		viewManager.dock().filter(dr -> dr.getTarget() == this).subscribe(dr -> viewAdded(dr.getSource()));
	}

	private void viewAdded(View view) {
		assert view != null;
		assert dockTypes != null;
		assert dockTypes.size() > 0;
		
		views.add(view);
		if (dockType == null) {
			dockType = dockTypes.get(0);
		}
		
		getContent().getChildren().clear();
		getContent().getChildren().add(dockType.computeRoot(views));
	}
}
