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
package com.gluonhq.jfxapps.core.ui.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachmentProvider;

@ApplicationInstanceSingleton
public class AnnotatedViewAttachmentProvider implements ViewAttachmentProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedViewAttachmentProvider.class);

    private final JfxAppContext context;

    private List<ViewAttachment> viewsCache;

    public AnnotatedViewAttachmentProvider(JfxAppContext context) {
        super();
        this.context = context;
    }

    @Override
    public List<ViewAttachment> views() {

        if (viewsCache != null) {
            return viewsCache;
        }

        viewsCache = context.getBeanClassesForAnnotation(com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment.class)
                .stream().map(this::makeViewAttachment).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return viewsCache;
    }

    @SuppressWarnings("unchecked")
    private List<ViewAttachment> makeViewAttachment(Class<?> cls) {

        List<ViewAttachment> result = new ArrayList<>();

        try {
            if (!AbstractFxmlViewController.class.isAssignableFrom(cls)) {
                logger.error("ViewAttachment annotation can only be used on AbstractFxmlViewController, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractFxmlViewController> viewClass = (Class<AbstractFxmlViewController>) cls;

            final com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment annotation = viewClass
                    .getAnnotation(com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment.class);

            assert annotation != null;

            ViewAttachment viewAttachment = ViewAttachment.create(
                    viewClass,
                    UUID.fromString(annotation.id()),
                    annotation.name(),
                    annotation.prefDockId().isBlank() ? null : UUID.fromString(annotation.prefDockId()),
                    annotation.openOnStart(),
                    annotation.selectOnStart(),
                    annotation.order(),
                    annotation.icon().isBlank() ? null: cls.getResource(annotation.icon()),
                    annotation.iconX2().isBlank() ? null: cls.getResource(annotation.iconX2()),
                    annotation.debug());

            result.add(viewAttachment);

        } catch (Exception e) {
            logger.error("Unable to create a view attachment for view : {}", cls, e);
        }
        return result;
    }

}
