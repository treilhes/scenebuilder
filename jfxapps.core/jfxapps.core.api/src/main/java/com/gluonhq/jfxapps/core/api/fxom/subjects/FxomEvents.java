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
package com.gluonhq.jfxapps.core.api.fxom.subjects;

import java.nio.file.Path;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fxom.css.StylesheetProvider;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.subjects.SubjectItem;
import com.gluonhq.jfxapps.core.api.subjects.SubjectManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.value.ChangeListener;

/**
 * This interface describe events related to the currently edited document
 *
 */
// TODO this interface must be split in 2: document events / fxml specific events
public interface FxomEvents {

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
     * Because:<br/>
     * - A userAgentStylesheet file has been set
     * - A stylesheet file has been added
     * - A stylesheet file has been removed
     */
    SubjectItem<StylesheetProvider> stylesheetConfig();
    /**
     * The current i18n configuration has changed.
     * Because:<br/>
     * - A property file has been added
     * - A property file has been removed
     */
    SubjectItem<I18nResourceProvider> i18nResourceConfig();

    SubjectItem<SelectionState> selectionDidChange();
    //SubjectItem<SelectionState> selectionDidChange();

    /**
     * The current fxomDocument has changed.
     * Because:<br/>
     * - An empty document is loaded
     * - An FXML file is loaded
     */
    SubjectItem<FXOMDocument> fxomDocument();

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
     * When a watched resource is updated outside of the editor
     * @return
     */
    SubjectItem<Map<Path, String>> filesystemUpdate();

    @ApplicationInstanceSingleton
    public class FxomEventsImpl implements FxomEvents {

        private FxomSubjects subjects;

        private final SubjectItem<Boolean> dirty;
        private final SubjectItem<Boolean> saved;
        private final SubjectItem<Boolean> closed;
        private final SubjectItem<StylesheetProvider> stylesheetConfig;
        private final SubjectItem<I18nResourceProvider> i18nResourceConfig;
        private final SubjectItem<FXOMDocument> omDocument;
        private final SubjectItem<SelectionState> selectionDidChange;
        private final SubjectItem<Integer> sceneGraphRevisionDidChange;
        private final SubjectItem<Integer> cssRevisionDidChange;
        private final SubjectItem<Map<Path, String>> filesystemUpdate;

        private ChangeListener<? super Number> sceneGraphRevisionChangeListener =
                (ob, o, n) -> sceneGraphRevisionDidChange().set(n.intValue());
        private ChangeListener<? super Number> cssRevisionChangeListener =
                (ob, o, n) -> cssRevisionDidChange().set(n.intValue());

        public FxomEventsImpl() {
            subjects = new FxomSubjects();

            dirty = new SubjectItem<>(subjects.getDirty()).set(false);
            saved = new SubjectItem<>(subjects.getSaved()).set(false);
            closed = new SubjectItem<>(subjects.getClosed());
            stylesheetConfig = new SubjectItem<>(subjects.getStylesheetConfig());
            i18nResourceConfig = new SubjectItem<>(subjects.getI18nResourceConfig());

            omDocument = new SubjectItem<>(subjects.getFxomDocument(),
                    (o, n) -> {
                        if (o != null) {
                            o.sceneGraphRevisionProperty().removeListener(sceneGraphRevisionChangeListener);
                            o.cssRevisionProperty().removeListener(cssRevisionChangeListener);
                        }
                        if (n != null) {
                            n.sceneGraphRevisionProperty().addListener(sceneGraphRevisionChangeListener);
                            n.cssRevisionProperty().addListener(cssRevisionChangeListener);
                        }
                    });

            selectionDidChange = new SubjectItem<>(subjects.getSelectionDidChange());
            sceneGraphRevisionDidChange = new SubjectItem<>(subjects.getSceneGraphRevisionDidChange());
            cssRevisionDidChange = new SubjectItem<>(subjects.getCssRevisionDidChange());
            filesystemUpdate = new SubjectItem<>(subjects.getFilesystemUpdate());
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
            return omDocument;
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
        public SubjectItem<Map<Path, String>> filesystemUpdate() {
            return filesystemUpdate;
        }
    }

    public class FxomSubjects extends SubjectManager {

        private ReplaySubject<Boolean> dirty;
        private ReplaySubject<Boolean> saved;
        private ReplaySubject<Boolean> closed;
        private ReplaySubject<StylesheetProvider> stylesheetConfig;
        private ReplaySubject<I18nResourceProvider> i18nResourceConfig;
        private ReplaySubject<FXOMDocument> fxomDocument;
        private ReplaySubject<SelectionState> selectionDidChange;

        private PublishSubject<Integer> sceneGraphRevisionDidChange;
        private PublishSubject<Integer> cssRevisionDidChange;
        private Subject<Map<Path, String>> filesystemUpdate;

        public FxomSubjects() {
            dirty = wrap(FxomSubjects.class, "dirty", ReplaySubject.create(1)); // NOI18N
            saved = wrap(FxomSubjects.class, "saved", ReplaySubject.create(1)); // NOI18N
            closed = wrap(FxomSubjects.class, "closed", ReplaySubject.create(1)); // NOI18N
            stylesheetConfig = wrap(FxomSubjects.class, "stylesheetConfig", ReplaySubject.create(1)); // NOI18N
            i18nResourceConfig = wrap(FxomSubjects.class, "i18nResourceConfig", ReplaySubject.create(1)); // NOI18N
            fxomDocument = wrap(FxomSubjects.class, "fxomDocument", ReplaySubject.create(1)); // NOI18N
            selectionDidChange = wrap(FxomSubjects.class, "selectionDidChange", ReplaySubject.create(1)); // NOI18N

            sceneGraphRevisionDidChange = wrap(FxomSubjects.class, "sceneGraphRevisionDidChange", // NOI18N
                    PublishSubject.create());
            cssRevisionDidChange = wrap(FxomSubjects.class, "cssRevisionDidChange", PublishSubject.create()); // NOI18N
            filesystemUpdate = wrap(FxomSubjects.class, "filesystemUpdate", PublishSubject.create()); // NOI18N
        }

        public ReplaySubject<Boolean> getDirty() {
            return dirty;
        }

        public ReplaySubject<Boolean> getSaved() {
            return saved;
        }

        public ReplaySubject<Boolean> getClosed() {
            return closed;
        }

        public ReplaySubject<StylesheetProvider> getStylesheetConfig() {
            return stylesheetConfig;
        }

        public ReplaySubject<I18nResourceProvider> getI18nResourceConfig() {
            return i18nResourceConfig;
        }

        public ReplaySubject<FXOMDocument> getFxomDocument() {
            return fxomDocument;
        }

        public ReplaySubject<SelectionState> getSelectionDidChange() {
            return selectionDidChange;
        }

        public PublishSubject<Integer> getSceneGraphRevisionDidChange() {
            return sceneGraphRevisionDidChange;
        }

        public PublishSubject<Integer> getCssRevisionDidChange() {
            return cssRevisionDidChange;
        }

        public Subject<Map<Path, String>> getFilesystemUpdate() {
            return filesystemUpdate;
        }
    }
}
