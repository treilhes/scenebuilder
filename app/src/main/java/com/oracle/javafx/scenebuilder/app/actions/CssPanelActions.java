package com.oracle.javafx.scenebuilder.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.app.CssPanelMenuController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.css.CssPanelController;

public class CssPanelActions {

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.view.rules",
			descriptionKey = "action.description.css.view.rules")
	public static class ViewRulesAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;
		private final CssPanelController cssPanelController;

		public ViewRulesAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
				@Autowired @Lazy CssPanelController cssPanelController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
			this.cssPanelController = cssPanelController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.viewRules();
	        cssPanelController.getDefaultsSplit().setDisable(true);
	        cssPanelController.getHideDefaultValues().setDisable(true);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.view.table",
			descriptionKey = "action.description.css.view.table")
	public static class ViewTableAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;
		private final CssPanelController cssPanelController;

		public ViewTableAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
				@Autowired @Lazy CssPanelController cssPanelController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
			this.cssPanelController = cssPanelController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.viewTable();
	        cssPanelController.getDefaultsSplit().setDisable(false);
	        cssPanelController.getHideDefaultValues().setDisable(false);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.view.text",
			descriptionKey = "action.description.css.view.text")
	public static class ViewTextAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;
		private final CssPanelController cssPanelController;

		public ViewTextAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
				@Autowired @Lazy CssPanelController cssPanelController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
			this.cssPanelController = cssPanelController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.viewText();
	        cssPanelController.getDefaultsSplit().setDisable(true);
	        cssPanelController.getHideDefaultValues().setDisable(true);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.copy.styleable.path",
			descriptionKey = "action.description.css.copy.styleable.path")
	public static class CopyStyleablePathAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;

		public CopyStyleablePathAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.copyStyleablePath();
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.split.defaults",
			descriptionKey = "action.description.css.split.defaults")
	public static class SplitDefaultsAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;
		private final CssPanelController cssPanelController;

		public SplitDefaultsAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
				@Autowired @Lazy CssPanelController cssPanelController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
			this.cssPanelController = cssPanelController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.splitDefaultsAction(cssPanelController.getDefaultsSplit());
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.css.show.styled.only",
			descriptionKey = "action.description.css.show.styled.only")
	public static class ShowStyledOnlyAction extends AbstractAction {

		private final CssPanelMenuController cssPanelMenuController;
		private final CssPanelController cssPanelController;

		public ShowStyledOnlyAction(
				@Autowired ApplicationContext context,
				@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
				@Autowired @Lazy CssPanelController cssPanelController) {
			super(context);
			this.cssPanelMenuController = cssPanelMenuController;
			this.cssPanelController = cssPanelController;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			cssPanelMenuController.showStyledOnly(cssPanelController.getHideDefaultValues());
		}
	}
}
