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
package com.gluonhq.jfxapps.core.accelerators.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.accelerators.preference.AcceleratorsMapPreference;
import com.gluonhq.jfxapps.core.accelerators.preference.FocusedAcceleratorsMapPreference;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.shortcut.Accelerator;
import com.gluonhq.jfxapps.core.api.shortcut.AcceleratorProvider;
import com.gluonhq.jfxapps.core.api.shortcut.Accelerators;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractCommonUiController;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;

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
 * {@link com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator}.
 * The initial configuration is override by preferences. A global
 * {@link Preference} with the injected {@link AcceleratorsMapPreference} and
 * focused items preference by using
 * {@link FocusedAcceleratorsMapPreference.Factory}
 */
@ApplicationInstanceSingleton
public class AcceleratorsController implements Accelerators {

    private static final Logger logger = LoggerFactory.getLogger(AcceleratorsController.class);

    private final AcceleratorsMapPreference acceleratorsMapPreference;
    private final FocusedAcceleratorsMapPreference focusedAcceleratorsMapPreference;
    private final Optional<List<AcceleratorProvider>> acceleratorProviders;

    private ApplicationInstanceEvents documentManager;
    private MainInstanceWindow documentWindow;

    private Map<Action, List<KeyCombination>> defaultGlobalAccelerators = new HashMap<>();
    private Map<Class<? extends AbstractCommonUiController>, Map<Action, List<KeyCombination>>> defaultFocusedAccelerators = new HashMap<>();

    public AcceleratorsController(
            ApplicationInstanceEvents documentManager,
            MainInstanceWindow documentWindow,
            AcceleratorsMapPreference acceleratorsMapPreference,
            FocusedAcceleratorsMapPreference focusedAcceleratorsMapPreference,
            Optional<List<AcceleratorProvider>> acceleratorProviders) {
        super();
        this.acceleratorsMapPreference = acceleratorsMapPreference;
        this.focusedAcceleratorsMapPreference = focusedAcceleratorsMapPreference;
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
        documentManager.focusedView().subscribe(this::onViewFocused);
        resetAll(null);
    }
    /**
     * @param providers
     */
    private void initializeProviders(Optional<List<AcceleratorProvider>> providers) {
        providers.ifPresent(l -> l.stream().flatMap(p -> p.accelerators().stream()).forEachOrdered(this::registerDefault));
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

            var focusedMapPreference = focusedAcceleratorsMapPreference.getValue()
                    .computeIfAbsent(accelerator.getAcceleratorTarget(), k -> FXCollections.observableHashMap());

            focusedMapPreference
                    .computeIfAbsent(accelerator.getAction().getClass(), k -> FXCollections.observableArrayList())
                    .add(keyCombination);

        } else {
            defaultGlobalAccelerators.computeIfAbsent(accelerator.getAction(), (k) -> new ArrayList<>())
                    .add(keyCombination);

            acceleratorsMapPreference.getValue().computeIfAbsent(accelerator.getAction().getClass(), k -> FXCollections.observableArrayList()).add(keyCombination);

        }
    }

    private void onViewFocused(AbstractFxmlViewController focusedView) {
        resetAll(focusedView);
    }

    // FIXME this method seems too cpu intensive for a method executed when hovering on UI elements
    // FIXME when using goto actions sometimes we get ConcurrentModificationException
    private void resetAll(AbstractFxmlViewController focusedPart) {
        final Scene scene;
        if (focusedPart != null) {
            scene = focusedPart.getRoot().getScene();
        } else {
            scene = documentWindow.getScene();
        }

        assert scene != null;

        scene.getAccelerators().clear();

        logger.debug("Accelerators cleared");
        defaultGlobalAccelerators.keySet().forEach(action -> {
            List<KeyCombination> keys = acceleratorsMapPreference.getValue().get(action.getClass());
            if (keys != null) {
                keys.forEach(key -> {
                    ActionRunner runner = new ActionRunner(action);
                    logger.debug("Global Accelerator set {} for {}", key, action.getClass());
                    scene.getAccelerators().put(key, runner);
                });
            }
        });

        if (focusedPart != null) { // some view has the focus, we override default accelerators with specific ones if any
            var hierarchy = new ArrayList<Class<? extends AbstractCommonUiController>>();
            Class<?> cls = focusedPart.getClass();
            while (cls != null && AbstractCommonUiController.class.isAssignableFrom(cls)) {
                hierarchy.add(0, (Class<? extends AbstractCommonUiController>)cls);
                cls = cls.getSuperclass();
            }

            for (var hierarchyClass:hierarchy) {
                final var focusedMapPreference = focusedAcceleratorsMapPreference.getValue().computeIfAbsent(hierarchyClass, k -> FXCollections.observableHashMap());
                final Map<Action, List<KeyCombination>> focusedAccelerators = defaultFocusedAccelerators.get(hierarchyClass);

                if (focusedMapPreference != null && focusedAccelerators != null) {
                    focusedAccelerators.keySet().forEach(action -> {
                        List<KeyCombination> keys = focusedMapPreference.get(action.getClass());
                        if (keys != null) {
                            keys.forEach(key -> {
                                ActionRunner runner = new ActionRunner(action);
                                logger.debug("Focused Accelerator set {} for {} when in {}", key, action.getClass(), focusedPart.getClass());
                                scene.getAccelerators().put(key, runner);
                            });
                        }
                    });
                }
            }
        }


    }

    private KeyCombination handleCtrlToMetaOnMacOs(KeyCombination keyCombination) {
        if (JfxAppsPlatform.IS_MAC && keyCombination.getControl() == ModifierValue.DOWN) {
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
    public void bind(Action action, MenuItem menuItem, Class<? extends AbstractCommonUiController> focusedClass) {
        if (action == null || focusedClass == null) {
            return;
        }

        Class<? extends Action> actionClass = action.getClass();
        final var focusedMapPreference = focusedAcceleratorsMapPreference.getValue().computeIfAbsent(focusedClass,
                k -> FXCollections.observableHashMap());

        final ObservableList<KeyCombination> accelerators;
        if (!focusedMapPreference.containsKey(actionClass)) {
            accelerators = FXCollections.observableArrayList();
            focusedMapPreference.put(actionClass, accelerators);

        } else {
            accelerators = focusedMapPreference.get(actionClass);
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
