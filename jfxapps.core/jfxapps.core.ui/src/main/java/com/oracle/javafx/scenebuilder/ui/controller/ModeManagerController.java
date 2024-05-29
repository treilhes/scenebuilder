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
package com.oracle.javafx.scenebuilder.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.content.mode.AbstractModeController;
import com.gluonhq.jfxapps.core.api.content.mode.Mode;
import com.gluonhq.jfxapps.core.api.content.mode.ModeDescriptor;
import com.gluonhq.jfxapps.core.api.content.mode.ModeManager;
import com.gluonhq.jfxapps.core.api.content.mode.ModeProvider;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@ApplicationInstanceSingleton
public class ModeManagerController implements ModeManager {

    private Logger logger = LoggerFactory.getLogger(ModeManagerController.class);

    private Map<Object, AbstractModeController> availableModes = new HashMap<>();

    private AbstractModeController previousMode = null;
    private AbstractModeController currentMode = null;

    private final List<ModeProvider> modeProviders;
    private final JfxAppContext context;

    Class<? extends FXOMDocument> currentDocumentClass = null;

    public ModeManagerController(
            JfxAppContext context,
            DocumentManager documentManager,
            List<ModeProvider> modeProviders
            ) {
        super();
        this.modeProviders = modeProviders;
        this.context = context;
    }

    private void initialiazeForDocument(Class<? extends FXOMDocument> docClass) {

        if (currentDocumentClass == docClass) {
            return;
        }

        previousMode = null;
        if (currentMode != null) {
            currentMode.willResignActive(null);
        }
        currentMode = null;
        availableModes.clear();

        final AtomicReference<Object> defaultModeId = new AtomicReference<>();

        modeProviders.forEach(mp -> {
            for (ModeDescriptor modeDescriptor:mp.getModes()) {

                AbstractModeController instance = context.getBean(modeDescriptor.modeClass());

                if (availableModes.containsKey(instance.getModeId())) {
                    logger.error("Duplicate mode id {}, ignoring provided mode", instance.getModeId().toString());
                } else {
                    availableModes.put(instance.getModeId(), instance);

                    if (modeDescriptor.defaultMode()) {
                        defaultModeId.set(instance.getModeId());
                    }
                }
            }
        });

        Object modeId = defaultModeId.get();
        if (modeId != null) {
            enableMode(modeId);
        }
    }

    /**
     * Returns true if one mode is enabled.
     * @return true if one mode is enabled.
     */
    @Override
    public boolean hasModeEnabled() {
        return currentMode != null;
    }

    /**
     * Returns true if the mode identified by modeId is enabled.
     * @param modeId the mode id to check
     * @return true if the mode identified by modeId is enabled.
     */
    @Override
    public boolean isModeEnabled(Object modeId) {
        return currentMode != null && currentMode.getModeId() == modeId;
    }

    @Override
    public void enableMode(Object modeId) {
        assert modeId != null;
        final AbstractModeController newModeController = availableModes.get(modeId);

        if (modeId == null) {
            logger.error("Mode id can't be null!");
            return;
        }
        if (newModeController == null) {
            logger.error("Unknown mode id {}", modeId.toString());
            return;
        }
        if (currentMode == newModeController) {
            return;
        }
        changeModeController(newModeController);
    }

    public void enablePreviousMode() {
        if (previousMode == null) {
            logger.error("No previous mode available");
            return;
        }
        changeModeController(previousMode);
    }

    private void changeModeController(AbstractModeController nextModeController) {
        assert nextModeController != currentMode;
        assert nextModeController != null;

        if (currentMode != null) {
            currentMode.willResignActive(nextModeController);
        }
        previousMode = currentMode;
        currentMode = nextModeController;
        currentMode.didBecomeActive(previousMode);
    }

    @Override
    public void fxomDocumentDidChange(FXOMDocument oldDocument) {
        initialiazeForDocument(oldDocument.getClass());
        if (currentMode != null) {
            currentMode.fxomDocumentDidChange(oldDocument);
        }
    }

    @Override
    public void editorSelectionDidChange() {
        if (currentMode != null) {
            currentMode.editorSelectionDidChange();
        }
    }

    @Override
    public void dropTargetDidChange() {
        if (currentMode != null) {
            currentMode.dropTargetDidChange();
        }
    }

    @Override
    public void fxomDocumentDidRefreshSceneGraph() {
        if (currentMode != null) {
            currentMode.fxomDocumentDidRefreshSceneGraph();
        }
    }

    @Override
    public Mode getEnabledMode() {
        return currentMode;
    }
}
