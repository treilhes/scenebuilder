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
package com.oracle.javafx.scenebuilder.editors.popupeditors;

import java.util.List;
import java.util.stream.Collectors;

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.controller.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.controls.paintpicker.PaintPicker;
import com.oracle.javafx.scenebuilder.api.control.effect.EffectProvider;
import com.oracle.javafx.scenebuilder.editors.control.effectpicker.EffectPicker;
import com.oracle.javafx.scenebuilder.editors.control.effectpicker.Utils;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Effect;

/**
 * Popup editor for the Effect property.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class EffectPopupEditor extends PopupEditor {

    private final MessageLogger messageLogger;

    private EffectPicker effectPicker;
    private List<MenuItem> effectMenuItems;
    private List<Class<? extends Effect>> effects;

    public EffectPopupEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            MessageLogger messageLogger,
            List<EffectProvider> effectProviders
            ) {
        super(dialog, documentation, fileSystem);
        this.messageLogger = messageLogger;
        this.effects = effectProviders.stream().flatMap(p -> p.effects().stream()).collect(Collectors.toList());
    }


    private final ChangeListener<Number> effectRevisionChangeListener = (ov, t, t1) -> {
        final Effect rootEffect = effectPicker.getRootEffectProperty();
        // Need to clone the root effect of the effect picker
        // in order to commit with a new value
        final Effect rootEffectClone = Utils.clone(rootEffect);
        // If live update, do not commit the value
        if (effectPicker.isLiveUpdate() == true) {
            userUpdateTransientValueProperty(rootEffectClone);
        } else {
            commitValue(rootEffectClone);
            updateMenuButton(rootEffectClone);
        }
    };

    private final ChangeListener<Boolean> liveUpdateListener = (ov, oldValue, newValue) -> {
        if (effectPicker.isLiveUpdate() == false) {
            final Effect rootEffect = effectPicker.getRootEffectProperty();
            // Need to clone the root effect of the effect picker
            // in order to commit with a new value
            final Effect rootEffectClone = Utils.clone(rootEffect);
            commitValue(rootEffectClone);
            updateMenuButton(rootEffectClone);
        }
    };



    @Override
    public void setPopupContentValue(Object value) {
        assert value == null || value instanceof Effect;
        effectPicker.revisionProperty().removeListener(effectRevisionChangeListener);
        effectPicker.liveUpdateProperty().removeListener(liveUpdateListener);
        // We first clone the root effect and initializePopupContent the effect picker with the clone value :
        // then the clone value will be updated and passed back to the model
        final Effect rootEffectClone = Utils.clone((Effect) value);
        effectPicker.setRootEffectProperty(rootEffectClone);
        // Refresh MenuButton items if needed
        updateMenuButton(rootEffectClone);
        effectPicker.revisionProperty().addListener(effectRevisionChangeListener);
        effectPicker.liveUpdateProperty().addListener(liveUpdateListener);
    }

    @Override
    public void initializePopupContent() {
        final EffectPicker.Delegate epd = (warningKey, arguments) -> messageLogger.logWarningMessage(warningKey, arguments);
        final PaintPicker.Delegate ppd = (warningKey, arguments) -> messageLogger.logWarningMessage(warningKey, arguments);
        effectPicker = new EffectPicker(epd, ppd);
        effectMenuItems = effectPicker.getMenuItems(effects);
    }

    @Override
    public String getPreviewString(Object value) {
        if (value == null) {
            return "+"; //NOCHECK
        }
        assert value instanceof Effect;
        Effect effect = (Effect) value;
        final StringBuilder sb = new StringBuilder();
        while (effect != null) {
            sb.append(effect.getClass().getSimpleName());
            effect = Utils.getDefaultInput(effect);
            if (effect != null) {
                sb.append(", "); //NOCHECK
            }
        }
        return sb.toString();
    }

    @Override
    public Node getPopupContentNode() {
        return effectPicker;
    }

    private void updateMenuButton(Effect value) {
        if (value != null) {
            if (popupMb.getItems().contains(popupMenuItem) == false) {
                popupMb.getItems().removeAll(effectMenuItems);
                popupMb.getItems().add(popupMenuItem);
            }
        } else {
            if (popupMb.getItems().contains(popupMenuItem) == true) {
                popupMb.getItems().addAll(effectMenuItems);
                popupMb.getItems().remove(popupMenuItem);
            }
        }
    }
}
