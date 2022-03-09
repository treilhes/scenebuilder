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
package com.oracle.javafx.scenebuilder.core.shortcut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerator;
import com.oracle.javafx.scenebuilder.api.shortcut.AcceleratorProvider;
import com.oracle.javafx.scenebuilder.api.ui.AbstractCommonUiController;

import javafx.scene.input.KeyCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class AnnotatedActionAcceleratorProvider implements AcceleratorProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedActionAcceleratorProvider.class);

    private final SceneBuilderBeanFactory context;
    private final ActionFactory actionFactory;

    private List<Accelerator> acceleratorsCache;

    public AnnotatedActionAcceleratorProvider(
            SceneBuilderBeanFactory context,
            ActionFactory actionFactory) {
        super();
        this.context = context;
        this.actionFactory = actionFactory;
    }

    @Override
    public List<Accelerator> accelerators() {
        if (acceleratorsCache != null) {
            return acceleratorsCache;
        }

        acceleratorsCache = context.getBeanClassesForAnnotation(com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator.class)
                .stream().map(this::makeAccelerator).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return acceleratorsCache;
    }

    @SuppressWarnings("unchecked")
    private List<Accelerator> makeAccelerator(Class<?> cls) {

        List<Accelerator> result = new ArrayList<>();

        try {
            if (!AbstractAction.class.isAssignableFrom(cls)) {
                logger.error("Accelerator annotation can only be used on Action, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractAction> actionClass = (Class<AbstractAction>) cls;

            final com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator[] annotations = actionClass
                    .getAnnotationsByType(com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator.class);

            assert annotations != null;

            if (annotations == null || annotations.length == 0) {
                return result;
            }

            final Action action = actionFactory.create(actionClass);

            for (com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator annotation : annotations) {

                final String rawAccelerator;
                if (EditorPlatform.IS_MAC && !annotation.macosAccelerator().isBlank()) {
                    rawAccelerator = annotation.macosAccelerator();
                } else {
                    rawAccelerator = annotation.accelerator().isBlank() ? null : annotation.accelerator();
                }

                if (rawAccelerator.isBlank()) {
                    continue;
                }

                final KeyCombination keyCombination = KeyCombination.valueOf(rawAccelerator);
                Accelerator accelerator = new Accelerator() {

                    @Override
                    public KeyCombination getKeyCombination() {
                        return keyCombination;
                    }

                    @Override
                    public Class<? extends AbstractCommonUiController> getAcceleratorTarget() {
                        return annotation.whenFocusing().equals(AbstractCommonUiController.class) ? null
                                : annotation.whenFocusing();
                    }

                    @Override
                    public Action getAction() {
                        return action;
                    }
                };

                result.add(accelerator);
            }


        } catch (Exception e) {
            logger.error("Unable to create an accelerator for action : {}", cls, e);
        }
        return result;
    }

}