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
package com.gluonhq.jfxapps.boot.loader.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonh.jfxapps.boot.layer.Layer;
import com.gluonh.jfxapps.boot.layer.LayerNotFoundException;
import com.gluonh.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextBootstraperTest.
 */
@ExtendWith(MockitoExtension.class)
class ContextBootstraperTest {

    /** The root dir. */
    @TempDir
    Path rootDir;

    /** The layer manager. */
    @Mock
    ModuleLayerManager layerManager;

    /** The parent layer. */
    @Mock
    Layer parentLayer;

    /** The parent model extension. */
    @Mock
    com.gluonhq.jfxapps.boot.loader.model.Extension parentModelExtension;

    /** The parent extension. */
    @Mock
    OpenExtension parentExtension;

    /** The ext layer. */
    @Mock
    Layer extLayer;

    /** The child model extension. */
    @Mock
    com.gluonhq.jfxapps.boot.loader.model.Extension childModelExtension;

    /** The child extension. */
    @Mock
    OpenExtension childExtension;

    /** The parent layer id. */
    UUID parentLayerId = UUID.randomUUID();

    /** The ext layer id. */
    UUID extLayerId = UUID.randomUUID();

    /**
     * Mock parent layer and components.
     */
    private void mockParentLayerAndComponents() {
        Mockito.when(layerManager.get(parentLayerId)).thenReturn(parentLayer);
        Mockito.when(parentLayer.loadService(Extension.class)).thenReturn(Set.of(parentExtension));
        Mockito.when(parentLayer.getId()).thenReturn(parentLayerId);
        Mockito.when(parentExtension.localContextClasses()).thenReturn(List.of(LocalParentComponent.class));
        Mockito.when(parentExtension.getId()).thenReturn(parentLayerId);
        Mockito.when(parentExtension.getParentId()).thenReturn(Extension.ROOT_ID);
        Mockito.when(parentModelExtension.getId()).thenReturn(parentLayerId);
    }

    /**
     * Mock child layer and components.
     */
    private void mockChildLayerAndComponents() {
        Mockito.when(layerManager.get(extLayerId)).thenReturn(extLayer);
        Mockito.when(extLayer.loadService(Extension.class)).thenReturn(Set.of(childExtension));
        Mockito.lenient().when(extLayer.getId()).thenReturn(extLayerId);
        Mockito.lenient().when(childExtension.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));
        Mockito.when(childExtension.exportedContextClasses()).thenReturn(List.of(ExportedChildComponent.class));
        Mockito.when(childExtension.getId()).thenReturn(extLayerId);
        Mockito.when(childExtension.getParentId()).thenReturn(parentLayerId);
        Mockito.when(childModelExtension.getId()).thenReturn(extLayerId);
        Mockito.when(parentModelExtension.getExtensions()).thenReturn(Set.of(childModelExtension));
    }

    /**
     * Must throw if module layer parent layer does not exists.
     */
    @Test
    void must_throw_if_module_layer_parentLayer_does_not_exists() {
        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
            ctxBoot.create(null, parentModelExtension, null, null);
        });
    }

    /**
     * Must throw if module layer of extensions does not exists.
     */
    @Test
    void must_throw_if_module_layer_of_extensions_does_not_exists() {
        Mockito.when(parentModelExtension.getId()).thenReturn(parentLayerId);

        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
            ctxBoot.create(null, parentModelExtension, null, null);
        });
    }

    /**
     * Must create a parent context with local component.
     *
     * @throws Exception the exception
     */
    @Test
    void must_create_a_parent_context_with_local_component() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);

        assertNotNull(ctx);
        assertNotNull(ctx.getBean(LocalParentComponent.class));
    }

    /**
     * Must create a child context with local component.
     *
     * @throws Exception the exception
     */
    @Test
    void must_create_a_child_context_with_local_component() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);
        SbContext ctxChild = ctxBoot.create(ctx, childModelExtension, null, null);

        assertNotNull(ctx);
        assertNotNull(ctxChild);
        assertNotNull(ctxChild.getBean(LocalChildComponent.class));
        assertEquals(ctx, ctxChild.getParent());
    }

    /**
     * Only exported classes from child must be loaded in context.
     *
     * @throws Exception the exception
     */
    @Test
    void only_exported_classes_from_child_must_be_loaded_in_context() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);


        assertNotNull(ctx);

        assertNotNull(ctx.getBean(LocalParentComponent.class));
        assertNotNull(ctx.getBean(ExportedChildComponent.class));
        // LocalChildComponent is local to child so must not be accessible in parent
        //Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(LocalChildComponent.class));
        Assertions.assertThrows(Exception.class, () -> ctx.getBean(LocalChildComponent.class));
    }

    /**
     * Already created contexts must be accessible.
     *
     * @throws Exception the exception
     */
    @Test
    void already_created_contexts_must_be_accessible() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);
        SbContext ctxChild = ctxBoot.create(ctx, childModelExtension, null, null);

        assertTrue(ctxBoot.exists(parentModelExtension));
        assertTrue(ctxBoot.exists(childModelExtension));
        assertEquals(ctx, ctxBoot.get(parentModelExtension));
        assertEquals(ctxChild, ctxBoot.get(childModelExtension));
    }

    /**
     * Already created contexts must be cleared and closed.
     *
     * @throws Exception the exception
     */
    @Test
    void already_created_contexts_must_be_cleared_and_closed() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);
        SbContext ctxChild = ctxBoot.create(ctx, childModelExtension, null, null);

        ctxBoot.clear();

        assertFalse(ctxBoot.exists(parentModelExtension));
        assertFalse(ctxBoot.exists(childModelExtension));
        assertNull(ctxBoot.get(parentModelExtension));
        assertNull(ctxBoot.get(childModelExtension));

        assertFalse(ctx.isActive());
        assertFalse(ctx.isRunning());
        assertFalse(ctxChild.isActive());
        assertFalse(ctxChild.isRunning());
    }

    /**
     * Already created contexts must be closed.
     *
     * @throws Exception the exception
     */
    @Test
    void already_created_contexts_must_be_closed() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);
        SbContext ctxChild = ctxBoot.create(ctx, childModelExtension, null, null);

        ctxBoot.close(parentModelExtension);
        ctxBoot.close(childModelExtension);

        assertFalse(ctxBoot.exists(parentModelExtension));
        assertFalse(ctxBoot.exists(childModelExtension));
        assertNull(ctxBoot.get(parentModelExtension));
        assertNull(ctxBoot.get(childModelExtension));

        assertFalse(ctx.isActive());
        assertFalse(ctx.isRunning());
        assertFalse(ctxChild.isActive());
        assertFalse(ctxChild.isRunning());
    }

    /**
     * The Class LocalParentComponent.
     */
    @Singleton
    static class LocalParentComponent {}

    /**
     * The Class LocalChildComponent.
     */
    @Singleton
    static class LocalChildComponent {}

    /**
     * The Class ExportedChildComponent.
     */
    @Singleton
    static class ExportedChildComponent {}
}
