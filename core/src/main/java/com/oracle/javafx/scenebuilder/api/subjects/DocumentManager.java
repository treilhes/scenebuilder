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
import javafx.beans.value.ChangeListener;
import lombok.Getter;

public interface DocumentManager {
    
    /**
     * The current "dirty" state has changed.
     * The document contains unsaved changes if true
     */
    SubjectItem<Boolean> dirty();
    /**
     * The current "saved" state has changed.
     * The document contains is saved if true
     */
    SubjectItem<Boolean> saved();
    /**
     * The current "closed" state has changed.
     * The document has been closed if true
     */
    SubjectItem<Boolean> closed();
    /**
     * The current stylesheet configuration has changed.
     * Because:</br>
     * - A userAgentStylesheet file has been set
     * - A stylesheet file has been added
     * - A stylesheet file has been removed
     */
    SubjectItem<StylesheetProvider> stylesheetConfig();
    /**
     * The current i18n configuration has changed.
     * Because:</br>
     * - A property file has been added
     * - A property file has been removed
     */
    SubjectItem<I18nResourceProvider> i18nResourceConfig();
    /**
     * The current fxomDocument has changed.
     * Because:</br>
     * - An empty document is loaded
     * - An FXML file is loaded 
     */
    SubjectItem<FXOMDocument> fxomDocument();
    /**
     * The currently selected objects have changed.
     */
    SubjectItem<SelectionState> selectionDidChange();
    
    /**
     * Revision is incremented each time the fxom document rebuilds the
     * scene graph.
     */
    SubjectItem<Integer> sceneGraphRevisionDidChange();
    /**
     * Revision is incremented each time the fxom document forces FX to
     * reload its stylesheets.
     */
    SubjectItem<Integer> cssRevisionDidChange();
    /**
     * The current classloader has changed.
     */
    SubjectItem<ClassLoader> classLoaderDidChange();
    /**
     * The main cycle of dependency injection loading is done
     * The document's dependencies have been loaded if true
     */
    SubjectItem<Boolean> dependenciesLoaded();
    
    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    public class DocumentManagerImpl implements InitializingBean, DocumentManager {

        private DocumentSubjects subjects;

        private final SubjectItem<Boolean> dirty;
        private final SubjectItem<Boolean> saved;
        private final SubjectItem<Boolean> closed;
        private final SubjectItem<Boolean> dependenciesLoaded;
        private final SubjectItem<StylesheetProvider> stylesheetConfig;
        private final SubjectItem<I18nResourceProvider> i18nResourceConfig;
        private final SubjectItem<FXOMDocument> fxomDocument;
        private final SubjectItem<SelectionState> selectionDidChange;
        private final SubjectItem<Integer> sceneGraphRevisionDidChange;
        private final SubjectItem<Integer> cssRevisionDidChange;
        private final SubjectItem<ClassLoader> classLoaderDidChange;
        
        private ChangeListener<? super Number> sceneGraphRevisionChangeListener = 
                (ob, o, n) -> sceneGraphRevisionDidChange().set(n.intValue());
        private ChangeListener<? super Number> cssRevisionChangeListener = 
                (ob, o, n) -> cssRevisionDidChange().set(n.intValue());

        public DocumentManagerImpl() {
            subjects = new DocumentSubjects();

            dirty = new SubjectItem<Boolean>(subjects.getDirty()).set(false);
            saved = new SubjectItem<Boolean>(subjects.getSaved()).set(false);
            closed = new SubjectItem<Boolean>(subjects.getClosed());
            dependenciesLoaded = new SubjectItem<Boolean>(subjects.getDependenciesLoaded()).set(false);
            stylesheetConfig = new SubjectItem<StylesheetProvider>(subjects.getStylesheetConfig());
            i18nResourceConfig = new SubjectItem<I18nResourceProvider>(subjects.getI18nResourceConfig());

            fxomDocument = new SubjectItem<FXOMDocument>(subjects.getFxomDocument(), 
                    (o, n) -> {
                        if (o != null) {
                            o.sceneGraphRevisionProperty().removeListener(sceneGraphRevisionChangeListener);
                            o.cssRevisionProperty().removeListener(cssRevisionChangeListener);
                        }
                        if (n != null) {
                            n.sceneGraphRevisionProperty().addListener(sceneGraphRevisionChangeListener);
                            n.cssRevisionProperty().addListener(cssRevisionChangeListener);
                        }
                    }, 
                    (s) -> {
                        return s.doOnEach(fd -> System.out.println("EACH FD" + fd.isOnNext()))
                        .doOnNext(fd -> System.out.println("NEXT FD" + fd.toString()))
                        .doAfterNext(fd -> System.out.println("AFTERNEXT FD" + fd.toString()))
                        .doOnSubscribe(fd -> System.out.println("doOnSubscribe FD" + fd.toString()));
                    });
            
            selectionDidChange = new SubjectItem<SelectionState>(subjects.getSelectionState());
            sceneGraphRevisionDidChange = new SubjectItem<Integer>(subjects.getSceneGraphRevisionDidChange());
            cssRevisionDidChange = new SubjectItem<Integer>(subjects.getCssRevisionDidChange());
            classLoaderDidChange = new SubjectItem<ClassLoader>(subjects.getClassLoaderDidChange());
        }

        @Override
        public void afterPropertiesSet() throws Exception {
        }

        @Override
        public SubjectItem<Boolean> dirty() {
            return dirty;
        }

        @Override
        public SubjectItem<Boolean> saved() {
            return saved;
        }
        
        @Override
        public SubjectItem<Boolean> closed() {
            return closed;
        }

        @Override
        public SubjectItem<StylesheetProvider> stylesheetConfig() {
            return stylesheetConfig;
        }

        @Override
        public SubjectItem<I18nResourceProvider> i18nResourceConfig() {
            return i18nResourceConfig;
        }

        @Override
        public SubjectItem<FXOMDocument> fxomDocument() {
            return fxomDocument;
        }

        @Override
        public SubjectItem<SelectionState> selectionDidChange() {
            return selectionDidChange;
        }

        @Override
        public SubjectItem<Integer> sceneGraphRevisionDidChange() {
            return sceneGraphRevisionDidChange;
        }

        @Override
        public SubjectItem<Integer> cssRevisionDidChange() {
            return cssRevisionDidChange;
        }
        
        @Override
        public SubjectItem<ClassLoader> classLoaderDidChange() {
            return classLoaderDidChange;
        }
        
        @Override
        public SubjectItem<Boolean> dependenciesLoaded() {
            return dependenciesLoaded;
        }
    }

    public class DocumentSubjects extends SubjectManager {

        private @Getter ReplaySubject<Boolean> dirty;
        private @Getter ReplaySubject<Boolean> saved;
        private @Getter ReplaySubject<Boolean> closed;
        private @Getter ReplaySubject<Boolean> dependenciesLoaded;
        private @Getter ReplaySubject<StylesheetProvider> stylesheetConfig;
        private @Getter ReplaySubject<I18nResourceProvider> i18nResourceConfig;
        private @Getter ReplaySubject<FXOMDocument> fxomDocument;
        private @Getter ReplaySubject<SelectionState> selectionState;

        private @Getter PublishSubject<Integer> sceneGraphRevisionDidChange;
        private @Getter PublishSubject<Integer> cssRevisionDidChange;
        private @Getter ReplaySubject<ClassLoader> classLoaderDidChange;

        public DocumentSubjects() {
            dirty = wrap(DocumentSubjects.class, "dirty", ReplaySubject.create(1));
            saved = wrap(DocumentSubjects.class, "saved", ReplaySubject.create(1));
            closed = wrap(DocumentSubjects.class, "closed", ReplaySubject.create(1));
            dependenciesLoaded = wrap(DocumentSubjects.class, "dependenciesLoaded", ReplaySubject.create(1));
            stylesheetConfig = wrap(DocumentSubjects.class, "stylesheetConfig", ReplaySubject.create(1));
            i18nResourceConfig = wrap(DocumentSubjects.class, "i18nResourceConfig", ReplaySubject.create(1));
            fxomDocument = wrap(DocumentSubjects.class, "fxomDocument", ReplaySubject.create(1));
            selectionState = wrap(DocumentSubjects.class, "selectionState", ReplaySubject.create(1));
            classLoaderDidChange = wrap(DocumentSubjects.class, "classLoaderDidChange", ReplaySubject.create(1));

            sceneGraphRevisionDidChange = wrap(DocumentSubjects.class, "sceneGraphRevisionDidChange",
                    PublishSubject.create());
            cssRevisionDidChange = wrap(DocumentSubjects.class, "cssRevisionDidChange", PublishSubject.create());
        }

    }
}
