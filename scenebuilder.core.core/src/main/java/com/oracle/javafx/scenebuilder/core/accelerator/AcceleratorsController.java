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
package com.oracle.javafx.scenebuilder.core.accelerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerator;
import com.oracle.javafx.scenebuilder.api.shortcut.AcceleratorProvider;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerators;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractCommonUiController;
import com.oracle.javafx.scenebuilder.core.accelerator.preferences.global.AcceleratorsMapPreference;
import com.oracle.javafx.scenebuilder.core.accelerator.preferences.global.FocusedAcceleratorsMapPreference;

import io.reactivex.disposables.Disposable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;

/**
 * This class manage two level of accelerators. Global accelerators are valid
 * regardless of the scenebuilder focused panel but they can be override by
 * accelerator specific to scenebuilder focused panel
 * {@link AbstractCommonUiController}. The initial configuration is provided by
 * injection of {@link AcceleratorProvider} or class annotated by
 * {@link com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator}.
 * The initial configuration is override by preferences. A global
 * {@link Preference} with the injected {@link AcceleratorsMapPreference} and
 * focused items preference by using
 * {@link FocusedAcceleratorsMapPreference.Factory}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class AcceleratorsController implements Accelerators {

    private static final Logger logger = LoggerFactory.getLogger(AcceleratorsController.class);

    private final SceneBuilderBeanFactory sceneBuilderBeanFactory;
    private final AcceleratorsMapPreference acceleratorsMapPreference;
    private final FocusedAcceleratorsMapPreference.Factory focusedAcceleratorsMapPreferenceFactory;
    private final SceneBuilderManager scenebuilderManager;
    private final List<AcceleratorProvider> acceleratorProviders;

    private DocumentManager documentManager;
    private DocumentWindow documentWindow;
    private Disposable subscription;

    private Map<Action, List<KeyCombination>> defaultGlobalAccelerators = new HashMap<>();
    private Map<Class<? extends AbstractCommonUiController>, Map<Action, List<KeyCombination>>> defaultFocusedAccelerators = new HashMap<>();

    public AcceleratorsController(
            SceneBuilderBeanFactory sceneBuilderBeanFactory,
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            DocumentWindow documentWindow,
            AcceleratorsMapPreference acceleratorsMapPreference,
            FocusedAcceleratorsMapPreference.Factory focusedAcceleratorsMapPreferenceFactory,
            @Autowired(required = false) List<AcceleratorProvider> acceleratorProviders) {
        super();
        this.sceneBuilderBeanFactory = sceneBuilderBeanFactory;
        this.scenebuilderManager = scenebuilderManager;
        this.acceleratorsMapPreference = acceleratorsMapPreference;
        this.focusedAcceleratorsMapPreferenceFactory = focusedAcceleratorsMapPreferenceFactory;
        this.acceleratorProviders = acceleratorProviders;
        this.documentManager = documentManager;
        this.documentWindow = documentWindow;

        defaultFocusedAccelerators.put(null, defaultGlobalAccelerators);
        documentManager.dependenciesLoaded().subscribe((b) -> {
            if (b) {
                this.setup();
            }
        });

    }

    private void setup() {
        initializeProviders(acceleratorProviders);
        documentManager.focused().subscribe(this::onDocumentPartFocused);
        resetAll(null);
    }
    /**
     * @param providers
     */
    private void initializeProviders(List<AcceleratorProvider> providers) {
        if (providers == null) {
            return;
        }

        providers.stream().flatMap(p -> p.accelerators().stream()).forEachOrdered(this::registerDefault);
    }

    private void registerDefault(Accelerator accelerator) {
        if (accelerator.getKeyCombination() == null) {
            return;
        }

        KeyCombination keyCombination = handleCtrlToMetaOnMacOs(accelerator.getKeyCombination());

        if (accelerator.getAcceleratorTarget() != null) {
            defaultFocusedAccelerators
                    .computeIfAbsent(accelerator.getAcceleratorTarget(), (k) -> new HashMap<>())
                    .computeIfAbsent(accelerator.getAction(), (k) -> new ArrayList<>()).add(keyCombination);

            FocusedAcceleratorsMapPreference focusedMapPreference = focusedAcceleratorsMapPreferenceFactory.get(accelerator.getAcceleratorTarget());

            focusedMapPreference.getValue().computeIfAbsent(accelerator.getAction().getClass(), k -> FXCollections.observableArrayList()).add(keyCombination);

        } else {
            defaultGlobalAccelerators.computeIfAbsent(accelerator.getAction(), (k) -> new ArrayList<>())
                    .add(keyCombination);

            acceleratorsMapPreference.getValue().computeIfAbsent(accelerator.getAction().getClass(), k -> FXCollections.observableArrayList()).add(keyCombination);

        }
    }

    private void onDocumentPartFocused(AbstractCommonUiController focusedPart) {
        resetAll(focusedPart);
    }

    // FIXME this method seems too cpu intensive for a method executed when hovering on UI elements
    private void resetAll(AbstractCommonUiController focusedPart) {
        final Scene scene;
        final FocusedAcceleratorsMapPreference focusedMapPreference;
        final Map<Action, List<KeyCombination>> focusedAccelerators;
        if (focusedPart != null) {
            scene = focusedPart.getRoot().getScene();
            focusedMapPreference = focusedAcceleratorsMapPreferenceFactory.get(focusedPart.getClass());
            focusedAccelerators = defaultFocusedAccelerators.get(focusedPart.getClass());
        } else {
            scene = documentWindow.getScene();
            focusedMapPreference = null;
            focusedAccelerators = null;
        }

        assert scene != null;

        scene.getAccelerators().clear();

        logger.info("Accelerators cleared");
        defaultGlobalAccelerators.keySet().forEach(action -> {
            List<KeyCombination> keys = acceleratorsMapPreference.getValue().get(action.getClass());
            if (keys != null) {
                keys.forEach(key -> {
                    ActionRunner runner = new ActionRunner(action);
                    logger.info("Global Accelerator set {} for {}", key, action.getClass());
                    scene.getAccelerators().put(key, runner);
                });
            }
        });

        if (focusedMapPreference != null && focusedAccelerators != null) {
            focusedAccelerators.keySet().forEach(action -> {
                List<KeyCombination> keys = focusedMapPreference.getValue().get(action.getClass());
                if (keys != null) {
                    keys.forEach(key -> {
                        ActionRunner runner = new ActionRunner(action);
                        logger.info("Focused Accelerator set {} for {} when in {}", key, action.getClass(), focusedPart.getClass());
                        scene.getAccelerators().put(key, runner);
                    });
                }
            });
        }
    }

    private KeyCombination handleCtrlToMetaOnMacOs(KeyCombination keyCombination) {
        if (EditorPlatform.IS_MAC && keyCombination.getControl() == ModifierValue.DOWN) {
            // if shortcut contains a CTRL then change it with META
            String acceleratorStr = keyCombination.getDisplayText();
            return KeyCombination.valueOf(acceleratorStr.replace(KeyCode.CONTROL.getName(), KeyCode.META.getName()));
        }
        return keyCombination;
    }

    @Override
    public void bind(Action action, MenuItem menuItem) {
        if (action == null) {
            return;
        }

        Class<? extends Action> actionClass = action.getClass();

        final ObservableList<KeyCombination> accelerators;
        if (!acceleratorsMapPreference.getValue().containsKey(actionClass)) {
            accelerators = FXCollections.observableArrayList();
            acceleratorsMapPreference.getValue().put(actionClass, accelerators);

        } else {
            accelerators = acceleratorsMapPreference.getValue().get(actionClass);
        }
        if (accelerators != null) {
            //menuItem.setAccelerator(accelerators.get(0));
            menuItem.acceleratorProperty().bind(Bindings.valueAt(accelerators, 0));
        }

    }

    @Override
    public void bind(Action action, MenuItem menuItem, AbstractCommonUiController focused) {
        if (action == null || focused == null) {
            return;
        }

        Class<? extends Action> actionClass = action.getClass();
        FocusedAcceleratorsMapPreference focusedMapPreference = focusedAcceleratorsMapPreferenceFactory.get(focused.getClass());

        final ObservableList<KeyCombination> accelerators;
        if (!focusedMapPreference.getValue().containsKey(actionClass)) {
            accelerators = FXCollections.observableArrayList();
            focusedMapPreference.getValue().put(actionClass, accelerators);

        } else {
            accelerators = focusedMapPreference.getValue().get(actionClass);
        }
        if (accelerators != null) {
            //menuItem.setAccelerator(accelerators.get(0));
            menuItem.acceleratorProperty().bind(Bindings.valueAt(accelerators, 0));
        }
    }

    private class ActionRunner implements Runnable {
        private Action action;

        public ActionRunner(Action action) {
            super();
            this.action = action;
        }

        @Override
        public void run() {
            action.checkAndPerform();
        }
    }


}
