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
package org.scenebuilder.fxml.api.subjects;

/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

import org.springframework.beans.factory.InitializingBean;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SubjectItem;
import com.gluonhq.jfxapps.core.api.subjects.SubjectManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractCommonUiController;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import javafx.beans.value.ChangeListener;

public interface FxmlDocumentManager extends DocumentManager {



    @ApplicationInstanceSingleton
    public class FxmlDocumentManagerImpl implements InitializingBean, FxmlDocumentManager {

        private final DocumentManager documentManager;

        private FxomDocumentSubjects subjects;

        private final SubjectItem<SelectionState> selectionDidChange;
        //private final SubjectItem<StylesheetProvider> stylesheetConfig;
        private final SubjectItem<I18nResourceProvider> i18nResourceConfig;
        private final SubjectItem<Integer> cssRevisionDidChange;

        private ChangeListener<? super Number> cssRevisionChangeListener = (ob, o, n) -> cssRevisionDidChange()
                .set(n.intValue());

        private final SubjectItem<FXOMDocument> fxomDocument;

        public FxmlDocumentManagerImpl(DocumentManager documentManager) {

            this.documentManager = documentManager;

            subjects = new FxomDocumentSubjects();

            //stylesheetConfig = new SubjectItem<StylesheetProvider>(subjects.getStylesheetConfig());
            i18nResourceConfig = new SubjectItem<I18nResourceProvider>(subjects.getI18nResourceConfig());
            cssRevisionDidChange = new SubjectItem<Integer>(subjects.getCssRevisionDidChange());
            selectionDidChange = new SubjectItem<SelectionState>(subjects.getSelectionState());

            fxomDocument = new SubjectItem<FXOMDocument>(subjects.getFxomDocument(), (o, n) -> {
                if (o != null) {
                    o.cssRevisionProperty().removeListener(cssRevisionChangeListener);
                }
                if (n != null) {
                    n.cssRevisionProperty().addListener(cssRevisionChangeListener);
                }
            });

            this.documentManager.fxomDocument().subscribe(doc -> {
                if (FXOMDocument.class.isInstance(doc)) {
                    fxomDocument.set(doc);
                } else {
                    fxomDocument.set(null);
                }
            });
        }

        @Override
        public void afterPropertiesSet() throws Exception {
        }

        @Override
        public SubjectItem<Boolean> dirty() {
            return documentManager.dirty();
        }

        @Override
        public SubjectItem<Boolean> saved() {
            return documentManager.saved();
        }

        @Override
        public SubjectItem<Boolean> closed() {
            return documentManager.closed();
        }

//        @Override
//        public SubjectItem<StylesheetProvider> stylesheetConfig() {
//            return stylesheetConfig;
//        }

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
            return documentManager.sceneGraphRevisionDidChange();
        }

        @Override
        public SubjectItem<Integer> cssRevisionDidChange() {
            return cssRevisionDidChange;
        }

        @Override
        public SubjectItem<Boolean> dependenciesLoaded() {
            return documentManager.dependenciesLoaded();
        }

        @Override
        public SubjectItem<AbstractCommonUiController> focused() {
            return documentManager.focused();
        }

        @Override
        public SubjectItem<AbstractFxmlViewController> focusedView() {
            return documentManager.focusedView();
        }

        @Override
        public SubjectItem<ClassLoader> classLoaderDidChange() {
            return documentManager.classLoaderDidChange();
        }

        @Override
        public SubjectItem<OMDocument> fxomDocument() {
            return documentManager.fxomDocument();
        }


    }

    public class FxomDocumentSubjects extends SubjectManager {

        private PublishSubject<SelectionState> selectionState;
        //private ReplaySubject<StylesheetProvider> stylesheetConfig;
        private ReplaySubject<I18nResourceProvider> i18nResourceConfig;
        private PublishSubject<Integer> cssRevisionDidChange;
        private ReplaySubject<FXOMDocument> fxomDocument;

        public FxomDocumentSubjects() {
            //stylesheetConfig = wrap(FxomDocumentSubjects.class, "stylesheetConfig", ReplaySubject.create(1)); // NOI18N
            i18nResourceConfig = wrap(FxomDocumentSubjects.class, "i18nResourceConfig", ReplaySubject.create(1)); // NOI18N
            cssRevisionDidChange = wrap(FxomDocumentSubjects.class, "cssRevisionDidChange", PublishSubject.create()); // NOI18N
            selectionState = wrap(FxomDocumentSubjects.class, "selectionState", PublishSubject.create()); // NOI18N
            fxomDocument = wrap(FxomDocumentSubjects.class, "fxomDocument", ReplaySubject.create(1)); // NOI18N
        }

//        public ReplaySubject<StylesheetProvider> getStylesheetConfig() {
//            return stylesheetConfig;
//        }

        public ReplaySubject<I18nResourceProvider> getI18nResourceConfig() {
            return i18nResourceConfig;
        }

        public PublishSubject<Integer> getCssRevisionDidChange() {
            return cssRevisionDidChange;
        }

        public PublishSubject<SelectionState> getSelectionState() {
            return selectionState;
        }

        public ReplaySubject<FXOMDocument> getFxomDocument() {
            return fxomDocument;
        }
    }
}
