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
package com.oracle.javafx.scenebuilder.ext.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import lombok.Getter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
		nameKey = "action.name.show.jar.analysis.report",
		descriptionKey = "action.description.show.jar.analysis.report",
		accelerator = "CTRL+J")
public class ApplyI18nContentAction extends AbstractAction implements InitWithDocument {

	private ApplyI18nContentConfig config;

	private final DocumentManager documentManager;

	public ApplyI18nContentAction(
			@Autowired ApplicationContext context,
			@Autowired @Lazy DocumentManager documentManager) {
		super(context);
		this.documentManager = documentManager;
	}

	public synchronized ApplyI18nContentConfig getActionConfig() {
		if (config == null) {
			config = new ApplyI18nContentConfig();
		}
		return config;
	}

	public synchronized void resetActionConfig() {
		config = null;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public void perform() {
		assert getActionConfig() != null;
		documentManager.i18nResourceConfig().onNext(getActionConfig());
	}

	public static class ApplyI18nContentConfig implements I18nResourceProvider {
		private @Getter List<ResourceBundle> bundles = new ArrayList<>();
	}

	@Override
	public void init() {
		extend().checkAndPerform();
	}
}
