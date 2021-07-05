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

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SubjectManager;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;

public interface SceneBuilderManager {

    SubjectItem<Boolean> debugMode();

    Subject<Boolean> closed();

    Subject<DocumentWindow> documentOpened();

    Subject<DocumentWindow> documentClosed();

    Subject<StylesheetProvider> stylesheetConfig();
    
    SubjectItem<ClassLoader> classloader();

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public class SceneBuilderManagerImpl implements InitializingBean, SceneBuilderManager {

        private final SceneBuilderSubjects subjects;
        private final SubjectItem<Boolean> debugMode;
        private final SubjectItem<ClassLoader> classloader;

        public SceneBuilderManagerImpl() {
            subjects = new SceneBuilderSubjects();
            debugMode = new SubjectItem<Boolean>(subjects.getDebugMode()).set(false);
            classloader = new SubjectItem<ClassLoader>(subjects.getClassloader()).set(this.getClass().getClassLoader());
        }

        @Override
        public void afterPropertiesSet() throws Exception {
        }

        @Override
        public Subject<StylesheetProvider> stylesheetConfig() {
            return subjects.getStylesheetConfig();
        }

        @Override
        public Subject<Boolean> closed() {
            return subjects.getClosed();
        }

        @Override
        public Subject<DocumentWindow> documentOpened() {
            return subjects.getDocumentOpened();
        }

        @Override
        public Subject<DocumentWindow> documentClosed() {
            return subjects.getDocumentClosed();
        }

        @Override
        public SubjectItem<Boolean> debugMode() {
            return debugMode;
        }
        
        @Override
        public SubjectItem<ClassLoader> classloader() {
            return classloader;
        }
    }

    public class SceneBuilderSubjects extends SubjectManager {

        private @Getter ReplaySubject<StylesheetProvider> stylesheetConfig;
        private @Getter PublishSubject<Boolean> closed;
        private @Getter PublishSubject<DocumentWindow> documentOpened;
        private @Getter PublishSubject<DocumentWindow> documentClosed;
        private @Getter ReplaySubject<Boolean> debugMode;
        private @Getter ReplaySubject<ClassLoader> classloader;

        public SceneBuilderSubjects() {
            closed = wrap(SceneBuilderSubjects.class, "closed", PublishSubject.create()); // NOI18N
            debugMode = wrap(SceneBuilderSubjects.class, "debugMode", ReplaySubject.create(1)); // NOI18N
            stylesheetConfig = wrap(SceneBuilderSubjects.class, "stylesheetConfig", ReplaySubject.create(1)); // NOI18N
            documentOpened = wrap(SceneBuilderSubjects.class, "documentOpened", PublishSubject.create()); // NOI18N
            documentClosed = wrap(SceneBuilderSubjects.class, "documentClosed", PublishSubject.create()); // NOI18N
            classloader = wrap(SceneBuilderSubjects.class, "classloader", ReplaySubject.create(1)); // NOI18N
        }

    }
}
