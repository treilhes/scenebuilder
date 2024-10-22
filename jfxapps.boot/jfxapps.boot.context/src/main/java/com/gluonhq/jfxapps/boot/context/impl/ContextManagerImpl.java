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
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.ContextConfiguration;
import com.gluonhq.jfxapps.boot.api.context.ContextCustomizer;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.api.layer.Layer;

@Component
public class ContextManagerImpl implements ContextManager {

    private static final Logger logger = LoggerFactory.getLogger(ContextManagerImpl.class);

    private final Map<UUID, JfxAppContextImpl> uuidToContexts;
    private final Map<ModuleLayer, JfxAppContextImpl> layerToContexts;

    private final ApplicationContext bootContext;

    public ContextManagerImpl(ApplicationContext bootContext) {
        super();
        this.bootContext = bootContext;
        this.uuidToContexts = new HashMap<>();
        this.layerToContexts = new HashMap<>();
    }

    @Override
    public JfxAppContext get(UUID contextId) {
        return uuidToContexts.get(contextId);
    }

    @Override
    public JfxAppContext get(ModuleLayer moduleLayer) {
        return layerToContexts.get(moduleLayer);
    }

    @Override
    public boolean exists(UUID contextId) {
        return uuidToContexts.containsKey(contextId);
    }

    @Override
    public JfxAppContext create(ContextConfiguration configuration) {

        UUID parentContextId = configuration.getParentContextId();
        Layer layer = configuration.getLayer();
        Set<Class<?>> classes = configuration.getClasses();
        Set<Class<?>> deportedClasses = configuration.getDeportedClasses();
        List<Object> singletonInstances = configuration.getSingletonInstances();
        MultipleProgressListener progressListener = configuration.getProgressListener();

        JfxAppContextImpl parent = uuidToContexts.get(parentContextId);

        final UUID uuid;
        final ClassLoader loader;
        final ModuleLayer moduleLayer;

        if (layer != null) {
            uuid = layer.getId();
            loader = layer.getLoader();
            moduleLayer = layer.getModuleLayer();
        } else {
            uuid = UUID.randomUUID();
            loader = null;
            moduleLayer = null;
        }

        JfxAppContextImpl context = new JfxAppContextImpl(uuid, loader);

        uuidToContexts.put(uuid, context);

        if (moduleLayer != null) {
            layerToContexts.put(moduleLayer, context);
        }

        if (parent != null) {
            context.setParent(parent);
        } else {
            context.setParent(bootContext);
        }

        logger.info("Loading context {} with parent {} using {} classes", uuid, parentContextId, classes.size());

        if (logger.isDebugEnabled()) {
            classes.stream().sorted(Comparator.comparing(Class::getName)).forEach(c -> logger.debug("Loaded {}", c));
            deportedClasses.stream().sorted(Comparator.comparing(Class::getName)).forEach(c -> logger.debug("Deported {}", c));
        }

        if (progressListener != null) {
            context.addProgressListener(progressListener);
        }

        context.register(classes.toArray(new Class<?>[0]));
        context.deport(deportedClasses.toArray(new Class<?>[0]));

        if (singletonInstances != null) {
            singletonInstances.forEach(context::registerSingleton);
        }

        for (var cls : context.getRegisteredClasses()) {
            if (ContextCustomizer.class.isAssignableFrom(cls)) {
                logger.info("Cutomizing class {}", cls);
                try {
                    ContextCustomizer customizer = (ContextCustomizer) cls.getDeclaredConstructor().newInstance();
                    customizer.customize(classes, context);
                } catch (Exception e) {
                    logger.error("Error customizing class {}", cls, e);
                }
            }

        }

        context.refresh();
        context.start();

        logger.info("Context {} has started successfully (active: {}, running: {}, beans: {})", context.getId(),
                context.isActive(), context.isRunning(), context.getBeanDefinitionCount());

        if (logger.isDebugEnabled()) {
            Arrays.stream(context.getBeanDefinitionNames()).sorted().forEach(c -> logger.debug("Bean {}", c));
        }

        return context;
    }

    @Override
    public void clear() {
        uuidToContexts.values().forEach(JfxAppContextImpl::close);
        uuidToContexts.clear();
        layerToContexts.clear();
    }

    @Override
    public void close(UUID id) {
        JfxAppContextImpl ctx = uuidToContexts.remove(id);

        var layer = layerToContexts.entrySet().stream().filter(c -> c.getValue().getUuid().equals(id)).findFirst();
        if (layer.isPresent()) {
            layerToContexts.remove(layer.get().getKey());
        }

        if (ctx != null) {
            ctx.close();
        }
    }

}
