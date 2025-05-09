/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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

import java.nio.file.Path;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractCommonUiController;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;

/**
 * This interface describe events related to the currently edited document
 *
 */
// TODO this interface must be split in 2: document events / fxml specific events
public interface ApplicationInstanceEvents {
    /**
     * The current "closed" state has changed.
     * The document has been closed if true
     */
    SubjectItem<Boolean> closed();

    /**
     * The current classloader has changed.
     */
    SubjectItem<ClassLoader> classLoaderDidChange();
    /**
     * The main cycle of dependency injection loading is done
     * The document's dependencies have been loaded if true
     */
    SubjectItem<Boolean> dependenciesLoaded();

    SubjectItem<AbstractCommonUiController> focused();
    SubjectItem<AbstractFxmlViewController> focusedView();

    @ApplicationInstanceSingleton
    public class ApplicationInstanceEventsImpl implements ApplicationInstanceEvents {

        private ApplicationInstanceSubjects subjects;

        private final SubjectItem<Boolean> closed;
        private final SubjectItem<Boolean> dependenciesLoaded;
        private final SubjectItem<ClassLoader> classLoaderDidChange;
        private final SubjectItem<AbstractCommonUiController> focused;
        private final SubjectItem<AbstractFxmlViewController> focusedView;
        

        public ApplicationInstanceEventsImpl() {
            subjects = new ApplicationInstanceSubjects();

            closed = new SubjectItem<>(subjects.getClosed());
            dependenciesLoaded = new SubjectItem<>(subjects.getDependenciesLoaded()).set(false);
            classLoaderDidChange = new SubjectItem<>(subjects.getClassLoaderDidChange());
            focused = new SubjectItem<>(subjects.getFocused());
            focusedView = new SubjectItem<>(subjects.getFocusedView());
            
        }

        @Override
        public SubjectItem<Boolean> closed() {
            return closed;
        }

        @Override
        public SubjectItem<ClassLoader> classLoaderDidChange() {
            return classLoaderDidChange;
        }

        @Override
        public SubjectItem<Boolean> dependenciesLoaded() {
            return dependenciesLoaded;
        }

        @Override
        public SubjectItem<AbstractCommonUiController> focused() {
            return focused;
        }

        @Override
        public SubjectItem<AbstractFxmlViewController> focusedView() {
            return focusedView;
        }


    }
 
    public class ApplicationInstanceSubjects extends SubjectManager {

        private ReplaySubject<Boolean> closed;
        private ReplaySubject<Boolean> dependenciesLoaded;
        private ReplaySubject<ClassLoader> classLoaderDidChange;

        private ReplaySubject<AbstractCommonUiController> focused;
        private ReplaySubject<AbstractFxmlViewController> focusedView;
        

        public ApplicationInstanceSubjects() {
            closed = wrap(ApplicationInstanceSubjects.class, "closed", ReplaySubject.create(1)); // NOI18N
            dependenciesLoaded = wrap(ApplicationInstanceSubjects.class, "dependenciesLoaded", ReplaySubject.create(1)); // NOI18N
            classLoaderDidChange = wrap(ApplicationInstanceSubjects.class, "classLoaderDidChange", ReplaySubject.create(1)); // NOI18N
            focused = wrap(ApplicationInstanceSubjects.class, "focused", ReplaySubject.create(1)); // NOI18N
            focusedView = wrap(ApplicationInstanceSubjects.class, "focusedView", ReplaySubject.create(1)); // NOI18N
            
        }

        public ReplaySubject<Boolean> getClosed() {
            return closed;
        }

        public ReplaySubject<Boolean> getDependenciesLoaded() {
            return dependenciesLoaded;
        }

        public ReplaySubject<ClassLoader> getClassLoaderDidChange() {
            return classLoaderDidChange;
        }

        public ReplaySubject<AbstractCommonUiController> getFocused() {
            return focused;
        }

        public ReplaySubject<AbstractFxmlViewController> getFocusedView() {
            return focusedView;
        }


    }
}
