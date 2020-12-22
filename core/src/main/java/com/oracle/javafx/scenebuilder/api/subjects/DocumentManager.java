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
package com.oracle.javafx.scenebuilder.api.subjects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface DocumentManager {
	Subject<Boolean> dirty();
	Subject<Boolean> saved();
	Subject<StylesheetProvider2> stylesheetConfig();
	Subject<I18nResourceProvider> i18nResourceConfig();
	Subject<FXOMDocument> fxomDocument();

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	public class DocumentManagerImpl implements InitializingBean, DocumentManager {

		private DocumentSubjects subjects;

		public DocumentManagerImpl() {
			subjects = new DocumentSubjects();
		}

		@Override
		public void afterPropertiesSet() throws Exception {
		}

		@Override
		public Subject<Boolean> dirty() {
			return subjects.getDirty();
		}

		@Override
		public Subject<Boolean> saved() {
			return subjects.getSaved();
		}

		@Override
		public Subject<StylesheetProvider2> stylesheetConfig() {
			return subjects.getStylesheetConfig();
		}

		@Override
		public Subject<I18nResourceProvider> i18nResourceConfig() {
			return subjects.getI18nResourceConfig();
		}

		@Override
		public Subject<FXOMDocument> fxomDocument() {
			return subjects.getFxomDocument();
		}

	}

	public class DocumentSubjects extends SubjectManager {

		private @Getter ReplaySubject<Boolean> dirty;
		private @Getter ReplaySubject<Boolean> saved;
		private @Getter ReplaySubject<StylesheetProvider2> stylesheetConfig;
		private @Getter ReplaySubject<I18nResourceProvider> i18nResourceConfig;
		private @Getter ReplaySubject<FXOMDocument> fxomDocument;


		public DocumentSubjects() {
			dirty = wrap(DocumentSubjects.class, "dirty", ReplaySubject.create(1));
			saved = wrap(DocumentSubjects.class, "saved", ReplaySubject.create(1));
			stylesheetConfig = wrap(DocumentSubjects.class, "stylesheetConfig", ReplaySubject.create(1));
			i18nResourceConfig = wrap(DocumentSubjects.class, "i18nResourceConfig", ReplaySubject.create(1));
			fxomDocument = wrap(DocumentSubjects.class, "fxomDocument", ReplaySubject.create(1));
		}

	}
}
