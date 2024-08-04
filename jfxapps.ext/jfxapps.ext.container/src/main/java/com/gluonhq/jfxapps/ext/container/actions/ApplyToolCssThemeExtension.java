/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.ext.container.actions;

import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractActionExtension;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolTheme;
import com.gluonhq.jfxapps.ext.container.preferences.global.ToolThemePreference;

@Prototype
public class ApplyToolCssThemeExtension extends AbstractActionExtension<ApplyToolCssAction> implements InitWithDocument {

	private final ToolThemePreference toolThemePreference;
	private final JfxAppContext context;
	private final ActionFactory actionFactory;

	public ApplyToolCssThemeExtension(
			JfxAppContext context,
			ActionFactory actionFactory,
			@Lazy ToolThemePreference toolThemePreference
			) {
		super();
		this.context = context;
		this.actionFactory = actionFactory;
		this.toolThemePreference = toolThemePreference;
	}

	@Override
	public boolean canPerform() {
		return toolThemePreference.getValue() != null;
	}

	@Override
	public void prePerform() {
		ToolTheme toolTheme = context.getBean(toolThemePreference.getValue());
		getExtendedAction().getActionConfig().setUserAgentStylesheet(toolTheme.getUserAgentStylesheet());
		getExtendedAction().getActionConfig().getStylesheets().addAll(toolTheme.getStylesheets());
	}

    @Override
    public void initWithDocument() {
        toolThemePreference.getObservableValue().addListener(
                (ob, o, n) -> actionFactory.create(ApplyToolCssAction.class).perform());
    }



}
