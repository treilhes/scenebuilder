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
package com.oracle.javafx.scenebuilder.gluon.theme;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.api.theme.AbstractTheme;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeMeta;
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.DefaultThemesList;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
@Component
public class GluonThemesList implements ThemeProvider {

	private GluonThemesList() {}

	@Override
	public List<Class<? extends Theme>> themes() {
		return Arrays.asList(
				GluonMobileLight.class,
				GluonMobileDark.class
				);
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
	@ThemeMeta(name = "title.theme.gluon_mobile_light", group = GluonGroup.class)
	public static class GluonMobileLight extends AbstractTheme {
		public GluonMobileLight(@Autowired GluonSwatchPreference gluonSwatchPreference) {
			super(new DefaultThemesList.Modena().getUserAgentStylesheet(),
					Arrays.asList(
							GlistenStyleClasses.impl_loadResource("glisten.gls"),
							"com/oracle/javafx/scenebuilder/gluon/css/GluonDocument.css",
							gluonSwatchPreference.getValue().getStylesheetURL(),
							GlistenStyleClasses.impl_loadResource("theme_light.gls")
							)
					);
		}
	}

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
	@ThemeMeta(name = "title.theme.gluon_mobile_dark", group = GluonGroup.class)
	public static class GluonMobileDark extends AbstractTheme {
		public GluonMobileDark(@Autowired GluonSwatchPreference gluonSwatchPreference) {
			super(new DefaultThemesList.Modena().getUserAgentStylesheet(),
					Arrays.asList(
							GlistenStyleClasses.impl_loadResource("glisten.gls"),
							"com/oracle/javafx/scenebuilder/app/css/GluonDocument.css",
							gluonSwatchPreference.getValue().getStylesheetURL(),
							GlistenStyleClasses.impl_loadResource("theme_dark.gls")
							)
					);
		}
	}
}
