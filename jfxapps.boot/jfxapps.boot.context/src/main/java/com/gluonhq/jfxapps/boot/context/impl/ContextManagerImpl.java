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
package com.gluonhq.jfxapps.boot.context.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.Comparators;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.context.SbContext;

@Component
public class ContextManagerImpl implements ContextManager {

    private static final Logger logger = LoggerFactory.getLogger(ContextManagerImpl.class);

    private final Map<UUID, SbContextImpl> contexts;

    public ContextManagerImpl() {
        super();
        this.contexts = new HashMap<>();
    }

    @Override
    public SbContext get(UUID contextId) {
        return contexts.get(contextId);
    }

    @Override
    public boolean exists(UUID contextId) {
        return contexts.containsKey(contextId);
    }

    @Override
    public SbContext create(UUID parentContextId, UUID contextId, Class<?>[] classes, List<Object> singletonInstances, MultipleProgressListener progressListener) {
        return create(parentContextId, contextId, null, classes, singletonInstances, progressListener);
    }

    @Override
    public SbContext create(UUID parentContextId, UUID contextId, ClassLoader loader, Class<?>[] classes, List<Object> singletonInstances, MultipleProgressListener progressListener) {

        SbContextImpl parent = contexts.get(parentContextId);

        SbContextImpl context = new SbContextImpl(contextId, loader);

        if (parent != null) {
            context.setParent(parent);
        }

        logger.info("Loading context {} with parent {} using {} classes", contextId, parentContextId, classes.length);

        if (logger.isDebugEnabled()) {
            Arrays.stream(classes).sorted(Comparator.comparing(Class::getName)).forEach(c -> logger.debug("Loaded {}", c));
        }

        if (progressListener != null) {
            context.addProgressListener(progressListener);
        }

        context.register(classes);

        if (singletonInstances != null) {
            singletonInstances.forEach(context::registerSingleton);
        }

        context.refresh();
        context.start();

        logger.info("Context {} has started successfully (active: {}, running: {}, beans: {})", context.getId(),
                context.isActive(), context.isRunning(), context.getBeanDefinitionCount());

        contexts.put(contextId, context);

        return context;
    }

    @Override
    public void clear() {
        contexts.values().forEach(SbContextImpl::close);
        contexts.clear();
    }

    @Override
    public void close(UUID id) {
        SbContextImpl ctx = contexts.remove(id);

        if (ctx != null) {
            ctx.close();
        }
    }

}
