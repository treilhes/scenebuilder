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
package com.gluonhq.jfxapps.boot.loader.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.LayerNotFoundException;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.loader.extension.SealedExtension;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper.ServiceLoader;

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

    @Mock
    ContextManager contextManager;

    /** The parent layer id. */
    UUID parentId = UUID.randomUUID();

    /** The parent model extension. */
    @Mock
    com.gluonhq.jfxapps.boot.loader.model.Extension parentExtensionModel;

    /** The parent layer. */
    @Mock
    Layer parentLayer;

    /** The parent extension. */
    @Mock
    OpenExtension parentExtensionLoaded;

    /** The ext layer id. */
    UUID childId = UUID.randomUUID();

    /** The child model extension. */
    @Mock
    com.gluonhq.jfxapps.boot.loader.model.Extension childExtensionModel;

    /** The ext layer. */
    @Mock
    Layer childExtensionLayer;

    /** The child extension. */
    @Mock
    OpenExtension childExtensionLoaded;

    @Mock
    SealedExtension sealedChildExtensionLoaded;

    @Mock
    JfxAppContext ctx;

    @Mock
    ServiceLoader loader;

    @Captor
    ArgumentCaptor<Set<Class<?>>> contextClassesCaptor;

    /**
     * Must throw if module layer parent layer does not exists.
     */
    @Test
    void must_throw_if_module_layer_parentLayer_does_not_exists() {
        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);
            ctxBoot.create(null, parentExtensionModel, null, null, loader);
        });
    }

    /**
     * Must throw if module layer of extensions does not exists.
     */
    @Test
    void must_throw_if_module_layer_of_extensions_does_not_exists() {
        when(parentExtensionModel.getId()).thenReturn(parentId);

        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);
            ctxBoot.create(null, parentExtensionModel, null, null, loader);
        });
    }

    /**
     * Must create a parent context with local component.
     *
     * @throws Exception the exception
     */
    @Test
    void must_create_a_context_with_declared_local_component() throws Exception {

        // the parent layer exists and is accessible
        when(layerManager.get(parentId)).thenReturn(parentLayer);

        // the parent layer contains one extension
        when(loader.loadService(parentLayer, Extension.class)).thenReturn(Set.of(parentExtensionLoaded));

        // the extension loaded expose local classes to load
        when(parentExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalParentComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(parentExtensionModel.getId()).thenReturn(parentId);
        when(parentLayer.getId()).thenReturn(parentId);
        when(parentExtensionLoaded.getId()).thenReturn(parentId);

        // the extension loaded is a top level one
        when(parentExtensionLoaded.getParentId()).thenReturn(Extension.ROOT_ID);

        // test

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.create(null, parentExtensionModel, null, null, loader);

        verify(contextManager).create(any(), any(), any(), contextClassesCaptor.capture(), any(), any(), any());

        assertThat(contextClassesCaptor.getValue()).contains(LocalParentComponent.class,
                parentExtensionLoaded.getClass());
    }

    /**
     * Must create a child context with local component.
     *
     * @throws Exception the exception
     */
    @Test
    void must_create_a_child_context_with_local_component() throws Exception {

        // the child layer exists and is accessible
        when(layerManager.get(childId)).thenReturn(childExtensionLayer);

        // the child layer contains one extension
        when(loader.loadService(childExtensionLayer, Extension.class)).thenReturn(Set.of(childExtensionLoaded));

        // the extension loaded expose local classes to load
        when(childExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));

        // the extension loaded expose exportable classes to load
        when(childExtensionLoaded.exportedContextClasses()).thenReturn(List.of(ExportedChildComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(childExtensionLayer.getId()).thenReturn(childId);
        when(childExtensionLoaded.getId()).thenReturn(childId);
        when(childExtensionModel.getId()).thenReturn(childId);

        // the extension loaded is a child of parent
        when(childExtensionLoaded.getParentId()).thenReturn(parentId);

        when(ctx.getUuid()).thenReturn(parentId);

        // test

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.create(ctx, childExtensionModel, null, null, loader);

        verify(contextManager).create(any(), any(), any(), contextClassesCaptor.capture(), any(), any(), any());

        assertThat(contextClassesCaptor.getValue()).contains(LocalChildComponent.class,
                childExtensionLoaded.getClass());
    }

    @Test
    void must_create_a_child_context_without_parent_window_and_editorsingleton_components_for_sealed_extensions()
            throws Exception {

        // the child layer exists and is accessible
        when(layerManager.get(childId)).thenReturn(childExtensionLayer);

        // the child layer contains one extension
        when(loader.loadService(childExtensionLayer, Extension.class)).thenReturn(Set.of(childExtensionLoaded));

        // the extension loaded expose local classes to load
        when(childExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));

        // the extension loaded expose exportable classes to load
        when(childExtensionLoaded.exportedContextClasses()).thenReturn(List.of(ExportedChildComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(childExtensionLayer.getId()).thenReturn(childId);
        when(childExtensionLoaded.getId()).thenReturn(childId);
        when(childExtensionModel.getId()).thenReturn(childId);

        // the extension loaded is a child of parent
        when(childExtensionLoaded.getParentId()).thenReturn(parentId);

        when(ctx.getUuid()).thenReturn(parentId);

        // test

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.create(ctx, childExtensionModel, null, null, loader);

        verify(contextManager).create(any(), any(), any(), contextClassesCaptor.capture(), any(), any(), any());

        assertThat(contextClassesCaptor.getValue()).contains(LocalChildComponent.class,
                childExtensionLoaded.getClass());
    }

    @Test
    void must_create_a_child_context_with_parent_window_and_editorsingleton_components_for_sealed_extensions()
            throws Exception {

        // the child layer exists and is accessible
        when(layerManager.get(childId)).thenReturn(childExtensionLayer);

        // the child layer contains one extension
        when(loader.loadService(childExtensionLayer, Extension.class)).thenReturn(Set.of(sealedChildExtensionLoaded));

        // the extension loaded expose local classes to load
        when(sealedChildExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(childExtensionLayer.getId()).thenReturn(childId);
        when(sealedChildExtensionLoaded.getId()).thenReturn(childId);
        when(childExtensionModel.getId()).thenReturn(childId);

        // the extension loaded is a child of parent
        when(sealedChildExtensionLoaded.getParentId()).thenReturn(parentId);

        when(ctx.getUuid()).thenReturn(parentId);
        //when(ctx.getRegisteredClasses()).thenReturn(Set.of(LocalParentComponent.class));
        when(ctx.getDeportedClasses()).thenReturn(Set.of(
                WindowParentComponent.class, EditorSingletonParentComponent.class));

        // test

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.create(ctx, childExtensionModel, null, null, loader);

        verify(contextManager).create(any(), any(), any(), contextClassesCaptor.capture(), any(), any(), any());

        assertThat(contextClassesCaptor.getValue()).contains(LocalChildComponent.class,
                WindowParentComponent.class, EditorSingletonParentComponent.class,
                sealedChildExtensionLoaded.getClass());
    }

    /**
     * Only exported classes from child must be loaded in context.
     *
     * @throws Exception the exception
     */
    @Test
    void only_exported_classes_from_child_must_be_loaded_in_context() throws Exception {

        // the parent layer exists and is accessible
        when(layerManager.get(parentId)).thenReturn(parentLayer);

        // the parent layer contains one extension
        when(loader.loadService(parentLayer, Extension.class)).thenReturn(Set.of(parentExtensionLoaded));

        // the extension loaded expose local classes to load
        when(parentExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalParentComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(parentExtensionModel.getId()).thenReturn(parentId);
        when(parentLayer.getId()).thenReturn(parentId);
        when(parentExtensionLoaded.getId()).thenReturn(parentId);

        // the extension loaded is a top level one
        when(parentExtensionLoaded.getParentId()).thenReturn(Extension.ROOT_ID);

        // the child layer exists and is accessible
        when(layerManager.get(childId)).thenReturn(childExtensionLayer);

        // the child layer contains one extension
        when(loader.loadService(childExtensionLayer, Extension.class)).thenReturn(Set.of(childExtensionLoaded));

        // the extension loaded expose local classes to load
        when(childExtensionLoaded.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));

        // the extension loaded expose exportable classes to load
        when(childExtensionLoaded.exportedContextClasses()).thenReturn(List.of(ExportedChildComponent.class));

        // the extension descriptor, the layer and the extension loaded share the same
        // id
        when(childExtensionLoaded.getId()).thenReturn(childId);
        when(childExtensionModel.getId()).thenReturn(childId);

        // the extension loaded is a child of parent
        when(childExtensionLoaded.getParentId()).thenReturn(parentId);

        // the parent extension model has a child
        when(parentExtensionModel.getExtensions()).thenReturn(Set.of(childExtensionModel));

        // test

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.create(null, parentExtensionModel, null, null, loader);

        verify(contextManager).create(any(), any(), any(), contextClassesCaptor.capture(), any(), any(), any());

        assertThat(contextClassesCaptor.getValue()).contains(LocalParentComponent.class,
                ExportedChildComponent.class);

        assertThat(contextClassesCaptor.getValue()).doesNotContain(LocalChildComponent.class);
    }

    /**
     * Already created contexts must be accessible.
     *
     * @throws Exception the exception
     */
    @Test
    void exists_call_must_check_context_exist() throws Exception {

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.exists(parentExtensionModel);

        verify(contextManager).exists(parentExtensionModel.getId());
    }

    /**
     * Already created contexts must be accessible.
     *
     * @throws Exception the exception
     */
    @Test
    void get_call_must_get_context() throws Exception {

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.get(parentExtensionModel);

        verify(contextManager).get(parentExtensionModel.getId());

    }

    /**
     * Already created contexts must be cleared and closed.
     *
     * @throws Exception the exception
     */
    @Test
    void clear_call_must_clear_context() throws Exception {

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.clear();

        verify(contextManager).clear();

    }

    /**
     * Already created contexts must be closed.
     *
     * @throws Exception the exception
     */
    @Test
    void close_call_must_close_context() throws Exception {

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager, contextManager);

        ctxBoot.close(parentExtensionModel);

        verify(contextManager).close(parentExtensionModel.getId());

    }

    /**
     * The Class LocalParentComponent.
     */
    @Singleton
    static class LocalParentComponent {
    }

    /**
     * The Class LocalParentComponent.
     */
    @ApplicationInstanceSingleton
    static class WindowParentComponent {
    }

    /**
     * The Class LocalParentComponent.
     */
    @ApplicationSingleton
    static class EditorSingletonParentComponent {
    }

    /**
     * The Class LocalChildComponent.
     */
    @Singleton
    static class LocalChildComponent {
    }

    /**
     * The Class ExportedChildComponent.
     */
    @Singleton
    static class ExportedChildComponent {
    }
}
