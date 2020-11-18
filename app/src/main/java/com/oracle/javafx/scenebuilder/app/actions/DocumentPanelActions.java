package com.oracle.javafx.scenebuilder.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.editor.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.preferences.global.DisplayOptionPreference;

public class DocumentPanelActions {

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.info",
			descriptionKey = "action.description.show.info")
	public static class ShowInfoAction extends Show {
		public ShowInfoAction(
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(DisplayOption.INFO, documentPanelController, displayOptionPreference);
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.fx.id",
			descriptionKey = "action.description.show.fx.id")
	public static class ShowFxIdAction extends Show {
		public ShowFxIdAction(
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(DisplayOption.FXID, documentPanelController, displayOptionPreference);
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.node.id",
			descriptionKey = "action.description.show.node.id")
	public static class ShowNodeIdAction extends Show {
		public ShowNodeIdAction(
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(DisplayOption.NODEID, documentPanelController, displayOptionPreference);
		}
	}
	
	public static class Show extends AbstractAction {
		
		private final DocumentPanelController documentPanelController;
		private final DisplayOptionPreference displayOptionPreference;
		private final DisplayOption option;
		
		public Show(DisplayOption option, DocumentPanelController documentPanelController, DisplayOptionPreference displayOptionPreference) {
			super();
			this.option = option;
			this.documentPanelController = documentPanelController;
			this.displayOptionPreference = displayOptionPreference;
		}

		@Override
		public boolean canPerform() {
			return documentPanelController.getHierarchyPanelController().getDisplayOption() != option;
		}

		@Override
		public void perform() {
			documentPanelController.getHierarchyPanelController().setDisplayOption(option);
	    	documentPanelController.getDocumentAccordion().setExpandedPane(
	    		documentPanelController.getDocumentAccordion().getPanes().get(0));

	    	displayOptionPreference
	    		.setValue(documentPanelController.getHierarchyPanelController().getDisplayOption())
	    		.writeToJavaPreferences();
		}
				
	}
}
