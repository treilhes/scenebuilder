package com.oracle.javafx.scenebuilder.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController.ShowMode;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController.ViewMode;

public class InspectorPanelActions {

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.all",
			descriptionKey = "action.description.show.all")
	public static class ShowAllAction extends Show {
		public ShowAllAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy InspectorPanelController inspectorPanelController) {
			super(context,ShowMode.ALL, inspectorPanelController);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.edited",
			descriptionKey = "action.description.show.edited")
	public static class ShowEditedAction extends Show {
		public ShowEditedAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy InspectorPanelController inspectorPanelController) {
			super(context,ShowMode.EDITED, inspectorPanelController);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.view.by.section",
			descriptionKey = "action.description.view.by.section")
	public static class ViewBySectionsAction extends View {
		public ViewBySectionsAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy InspectorPanelController inspectorPanelController) {
			super(context,ViewMode.SECTION, inspectorPanelController);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.view.by.property.name",
			descriptionKey = "action.description.view.by.property.name")
	public static class ViewByPropertyNameAction extends View {
		public ViewByPropertyNameAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy InspectorPanelController inspectorPanelController) {
			super(context,ViewMode.PROPERTY_NAME, inspectorPanelController);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.view.by.property.type",
			descriptionKey = "action.description.view.by.property.type")
	public static class ViewByPropertyTypeAction extends View {
		public ViewByPropertyTypeAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy InspectorPanelController inspectorPanelController) {
			super(context,ViewMode.PROPERTY_TYPE, inspectorPanelController);
		}
	}



	public static class Show extends AbstractAction {

		private final InspectorPanelController inspectorPanelController;
		private final ShowMode option;

		public Show(ApplicationContext context, ShowMode option, InspectorPanelController inspectorPanelController) {
			super(context);
			this.option = option;
			this.inspectorPanelController = inspectorPanelController;
		}

		@Override
		public boolean canPerform() {
			//return inspectorPanelController.getShowMode() != option;
			return true;
		}

		@Override
		public void perform() {
			inspectorPanelController.setShowMode(option);
		}

	}

	public static class View extends AbstractAction {

		private final InspectorPanelController inspectorPanelController;
		private final ViewMode option;

		public View(ApplicationContext context, ViewMode option, InspectorPanelController inspectorPanelController) {
			super(context);
			this.option = option;
			this.inspectorPanelController = inspectorPanelController;
		}

		@Override
		public boolean canPerform() {
			//return inspectorPanelController.getViewMode() != option;
			return true;
		}

		@Override
		public void perform() {
			inspectorPanelController.setViewMode(option);
		}

	}
}
