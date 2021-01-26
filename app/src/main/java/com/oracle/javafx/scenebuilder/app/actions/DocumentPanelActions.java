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
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.HierarchyPanel.DisplayOption;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.document.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;

public class DocumentPanelActions {

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.info",
			descriptionKey = "action.description.show.info")
	public static class ShowInfoAction extends Show {
		public ShowInfoAction(
				@Autowired Api api,
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(api, DisplayOption.INFO, documentPanelController, displayOptionPreference);
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
				@Autowired Api api,
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(api, DisplayOption.FXID, documentPanelController, displayOptionPreference);
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
		        @Autowired Api api,
				@Autowired @Lazy DocumentPanelController documentPanelController,
				@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
			super(api, DisplayOption.NODEID, documentPanelController, displayOptionPreference);
		}
	}

	public static class Show extends AbstractAction {

		private final DocumentPanelController documentPanelController;
		private final DisplayOptionPreference displayOptionPreference;
		private final DisplayOption option;

		public Show(Api api, DisplayOption option, DocumentPanelController documentPanelController, DisplayOptionPreference displayOptionPreference) {
			super(api);
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
