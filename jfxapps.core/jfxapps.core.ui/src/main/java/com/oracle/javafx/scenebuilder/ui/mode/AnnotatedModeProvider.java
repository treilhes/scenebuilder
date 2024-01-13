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
package com.oracle.javafx.scenebuilder.ui.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.oracle.javafx.scenebuilder.api.content.mode.AbstractModeController;
import com.oracle.javafx.scenebuilder.api.content.mode.ModeDescriptor;
import com.oracle.javafx.scenebuilder.api.content.mode.ModeProvider;

@ApplicationInstanceSingleton
public class AnnotatedModeProvider implements ModeProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedModeProvider.class);

    private final SbContext context;

    private List<ModeDescriptor> modesCache;

    public AnnotatedModeProvider(SbContext context) {
        super();
        this.context = context;
    }

    @Override
    public List<ModeDescriptor> getModes() {
        if (modesCache != null) {
            return modesCache;
        }

        modesCache = context
                .getBeanClassesForAnnotation(
                        com.oracle.javafx.scenebuilder.api.content.mode.annotation.ModeDescriptor.class)
                .stream().map(this::makeModeDescriptor).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return modesCache;
    }

    @SuppressWarnings("unchecked")
    private List<ModeDescriptor> makeModeDescriptor(Class<?> cls) {

        List<ModeDescriptor> result = new ArrayList<>();

        try {
            if (!AbstractModeController.class.isAssignableFrom(cls)) {
                logger.error("ModeDescriptor annotation can only be used on Mode, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractModeController> modeClass = (Class<AbstractModeController>) cls;

            final com.oracle.javafx.scenebuilder.api.content.mode.annotation.ModeDescriptor[] annotations = modeClass
                    .getAnnotationsByType(
                            com.oracle.javafx.scenebuilder.api.content.mode.annotation.ModeDescriptor.class);

            assert annotations != null;

            if (annotations == null || annotations.length == 0) {
                return result;
            }

            for (com.oracle.javafx.scenebuilder.api.content.mode.annotation.ModeDescriptor annotation : annotations) {
                result.add(ModeDescriptor.create(modeClass, annotation.documentType(), annotation.defaultMode()));
            }

        } catch (Exception e) {
            logger.error("Unable to create a ModeDescriptor for mode : {}", cls, e);
        }
        return result;
    }

}
