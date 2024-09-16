/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.subjects;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;

public interface ApplicationEvents {

    SubjectItem<Boolean> debugMode();

    Subject<Boolean> closed();

    Subject<ApplicationInstance> documentOpened();

    Subject<ApplicationInstance> documentClosed();

    Subject<ApplicationInstance> documentScoped();

    Subject<ToolStylesheetProvider> stylesheetConfig();

    SubjectItem<ClassLoader> classloader();

    @ApplicationSingleton
    public class ApplicationEventsImpl implements ApplicationEvents {

        private final ApplicationSubjects subjects;
        private final SubjectItem<Boolean> debugMode;
        private final SubjectItem<ClassLoader> classloader;


        public ApplicationEventsImpl() {
            subjects = new ApplicationSubjects();
            debugMode = new SubjectItem<Boolean>(subjects.getDebugMode()).set(false);
            classloader = new SubjectItem<ClassLoader>(subjects.getClassloader()).set(this.getClass().getClassLoader());
        }

        @Override
        public Subject<ToolStylesheetProvider> stylesheetConfig() {
            return subjects.getStylesheetConfig();
        }

        @Override
        public Subject<Boolean> closed() {
            return subjects.getClosed();
        }

        @Override
        public Subject<ApplicationInstance> documentOpened() {
            return subjects.getDocumentOpened();
        }

        @Override
        public Subject<ApplicationInstance> documentClosed() {
            return subjects.getDocumentClosed();
        }

        @Override
        public Subject<ApplicationInstance> documentScoped() {
            return subjects.getDocumentScoped();
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

    public class ApplicationSubjects extends SubjectManager {

        private ReplaySubject<ToolStylesheetProvider> stylesheetConfig;
        private PublishSubject<Boolean> closed;
        private PublishSubject<ApplicationInstance> documentOpened;
        private PublishSubject<ApplicationInstance> documentClosed;
        private ReplaySubject<ApplicationInstance> documentScoped;
        private ReplaySubject<Boolean> debugMode;
        private ReplaySubject<ClassLoader> classloader;

        public ApplicationSubjects() {
            closed = wrap(ApplicationSubjects.class, "closed", PublishSubject.create()); // NOI18N
            debugMode = wrap(ApplicationSubjects.class, "debugMode", ReplaySubject.create(1)); // NOI18N
            stylesheetConfig = wrap(ApplicationSubjects.class, "stylesheetConfig", ReplaySubject.create(1)); // NOI18N
            documentOpened = wrap(ApplicationSubjects.class, "documentOpened", PublishSubject.create()); // NOI18N
            documentClosed = wrap(ApplicationSubjects.class, "documentClosed", PublishSubject.create()); // NOI18N
            documentScoped = wrap(ApplicationSubjects.class, "documentScoped", ReplaySubject.create(1)); // NOI18N
            classloader = wrap(ApplicationSubjects.class, "classloader", ReplaySubject.create(1)); // NOI18N
        }

        public ReplaySubject<ToolStylesheetProvider> getStylesheetConfig() {
            return stylesheetConfig;
        }

        public PublishSubject<Boolean> getClosed() {
            return closed;
        }

        public PublishSubject<ApplicationInstance> getDocumentOpened() {
            return documentOpened;
        }

        public PublishSubject<ApplicationInstance> getDocumentClosed() {
            return documentClosed;
        }

        public ReplaySubject<ApplicationInstance> getDocumentScoped() {
            return documentScoped;
        }

        public ReplaySubject<Boolean> getDebugMode() {
            return debugMode;
        }

        public ReplaySubject<ClassLoader> getClassloader() {
            return classloader;
        }

    }
}
