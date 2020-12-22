/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
