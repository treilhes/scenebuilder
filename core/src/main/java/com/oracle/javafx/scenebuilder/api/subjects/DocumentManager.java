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
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import javafx.beans.value.ChangeListener;
import lombok.Getter;

public interface DocumentManager {
    Subject<Boolean> dirty();

    Subject<Boolean> saved();
    
    Subject<Boolean> closed();

    Subject<StylesheetProvider> stylesheetConfig();

    Subject<I18nResourceProvider> i18nResourceConfig();

    Subject<FXOMDocument> fxomDocument();

    Subject<SelectionState> selectionDidChange();

    Subject<Integer> sceneGraphRevisionDidChange();

    Subject<Integer> cssRevisionDidChange();

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    public class DocumentManagerImpl implements InitializingBean, DocumentManager {

        private DocumentSubjects subjects;

        private FXOMDocument lastFXOMDocument = null;
        private ChangeListener<? super Number> sceneGraphRevisionChangeListener = (ob, o,
                n) -> sceneGraphRevisionDidChange().onNext(n.intValue());
        private ChangeListener<? super Number> cssRevisionChangeListener = (ob, o, n) -> cssRevisionDidChange()
                .onNext(n.intValue());

        public DocumentManagerImpl() {
            subjects = new DocumentSubjects();

            fxomDocument().subscribe(fd -> {
                if (lastFXOMDocument != null) {
                    lastFXOMDocument.sceneGraphRevisionProperty().removeListener(sceneGraphRevisionChangeListener);
                    lastFXOMDocument.cssRevisionProperty().removeListener(cssRevisionChangeListener);
                }
                if (fd != null) {
                    fd.sceneGraphRevisionProperty().addListener(sceneGraphRevisionChangeListener);
                    fd.cssRevisionProperty().addListener(cssRevisionChangeListener);
                    lastFXOMDocument = fd;
                }
            });
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
        public Subject<Boolean> closed() {
            return subjects.getClosed();
        }

        @Override
        public Subject<StylesheetProvider> stylesheetConfig() {
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

        @Override
        public Subject<SelectionState> selectionDidChange() {
            return subjects.getSelectionState();
        }

        @Override
        public Subject<Integer> sceneGraphRevisionDidChange() {
            return subjects.getSceneGraphRevisionDidChange();
        }

        @Override
        public Subject<Integer> cssRevisionDidChange() {
            return subjects.getCssRevisionDidChange();
        }
    }

    public class DocumentSubjects extends SubjectManager {

        private @Getter ReplaySubject<Boolean> dirty;
        private @Getter ReplaySubject<Boolean> saved;
        private @Getter ReplaySubject<Boolean> closed;
        private @Getter ReplaySubject<StylesheetProvider> stylesheetConfig;
        private @Getter ReplaySubject<I18nResourceProvider> i18nResourceConfig;
        private @Getter ReplaySubject<FXOMDocument> fxomDocument;
        private @Getter ReplaySubject<SelectionState> selectionState;

        private @Getter PublishSubject<Integer> sceneGraphRevisionDidChange;
        private @Getter PublishSubject<Integer> cssRevisionDidChange;

        public DocumentSubjects() {
            dirty = wrap(DocumentSubjects.class, "dirty", ReplaySubject.create(1));
            saved = wrap(DocumentSubjects.class, "saved", ReplaySubject.create(1));
            closed = wrap(DocumentSubjects.class, "closed", ReplaySubject.create(1));
            stylesheetConfig = wrap(DocumentSubjects.class, "stylesheetConfig", ReplaySubject.create(1));
            i18nResourceConfig = wrap(DocumentSubjects.class, "i18nResourceConfig", ReplaySubject.create(1));
            fxomDocument = wrap(DocumentSubjects.class, "fxomDocument", ReplaySubject.create(1));
            selectionState = wrap(DocumentSubjects.class, "selectionState", ReplaySubject.create(1));

            sceneGraphRevisionDidChange = wrap(DocumentSubjects.class, "sceneGraphRevisionDidChange",
                    PublishSubject.create());
            cssRevisionDidChange = wrap(DocumentSubjects.class, "cssRevisionDidChange", PublishSubject.create());
        }

    }
}
