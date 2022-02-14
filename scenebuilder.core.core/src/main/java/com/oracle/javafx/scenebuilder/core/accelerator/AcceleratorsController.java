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

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerators;
import com.oracle.javafx.scenebuilder.core.accelerator.preferences.global.AcceleratorsMapPreference;

import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;

/**
 * @author ptreilhes
 *
 */
@Component
public class AcceleratorsController implements Accelerators {

    private final Main main;
    private final AcceleratorsMapPreference acceleratorsMapPreference;

    public AcceleratorsController(
            Main main,
            AcceleratorsMapPreference acceleratorsMapPreference) {
        super();
        this.main = main;
        this.acceleratorsMapPreference = acceleratorsMapPreference;
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
    public void bind(Action action) {
        if (action == null) {
            return;
        }

        Class<? extends Action> actionClass = action.getClass();

        KeyCombination accelerator = handleCtrlToMetaOnMacOs(action.getWishedAccelerator());

        if (!acceleratorsMapPreference.getValue().containsKey(actionClass)) {
            acceleratorsMapPreference.getValue().put(actionClass, accelerator);
        }

//        main.
//        menuItem.setAccelerator(acceleratorsMapPreference.getValue().get(actionClass));
//        menuItem.acceleratorProperty().bind(Bindings.valueAt(acceleratorsMapPreference.getValue(), actionClass));
    }

    @Override
    public void bind(Action action, MenuItem menuItem) {
        if (action == null) {
            return;
        }

        Class<? extends Action> actionClass = action.getClass();

        KeyCombination accelerator = handleCtrlToMetaOnMacOs(action.getWishedAccelerator());

        if (!acceleratorsMapPreference.getValue().containsKey(actionClass)) {
            acceleratorsMapPreference.getValue().put(actionClass, accelerator);
        }

        menuItem.setAccelerator(acceleratorsMapPreference.getValue().get(actionClass));
        menuItem.acceleratorProperty().bind(Bindings.valueAt(acceleratorsMapPreference.getValue(), actionClass));
    }
}
