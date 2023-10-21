package com.oracle.javafx.scenebuilder.core.loader.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Singleton;
import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.core.loader.internal.context.ContextBootstraper;
import com.oracle.javafx.scenebuilder.core.loader.internal.context.InvalidExtensionException;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.LayerNotFoundException;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.ModuleLayerManager;

@ExtendWith(MockitoExtension.class)
class ContextBootstraperTest {

    @TempDir
    Path rootDir;

    @Mock
    ModuleLayerManager layerManager;

    @Mock
    Layer parentLayer;

    @Mock
    com.oracle.javafx.scenebuilder.core.loader.model.Extension parentModelExtension;

    @Mock
    OpenExtension parentExtension;

    @Mock
    Layer extLayer;

    @Mock
    com.oracle.javafx.scenebuilder.core.loader.model.Extension childModelExtension;

    @Mock
    OpenExtension childExtension;

    UUID parentLayerId = UUID.randomUUID();

    UUID extLayerId = UUID.randomUUID();

    private void mockParentLayerAndComponents() {
        Mockito.when(layerManager.get(parentLayerId)).thenReturn(parentLayer);
        Mockito.when(parentLayer.extensions()).thenReturn(Set.of(parentExtension));
        Mockito.when(parentLayer.getId()).thenReturn(parentLayerId);
        Mockito.when(parentExtension.localContextClasses()).thenReturn(List.of(LocalParentComponent.class));
        Mockito.when(parentExtension.getId()).thenReturn(parentLayerId);
        Mockito.when(parentExtension.getParentId()).thenReturn(Extension.ROOT_ID);
        Mockito.when(parentModelExtension.getId()).thenReturn(parentLayerId);
    }

    private void mockChildLayerAndComponents() {
        Mockito.when(layerManager.get(extLayerId)).thenReturn(extLayer);
        Mockito.when(extLayer.extensions()).thenReturn(Set.of(childExtension));
        Mockito.lenient().when(extLayer.getId()).thenReturn(extLayerId);
        Mockito.lenient().when(childExtension.localContextClasses()).thenReturn(List.of(LocalChildComponent.class));
        Mockito.when(childExtension.exportedContextClasses()).thenReturn(List.of(ExportedChildComponent.class));
        Mockito.when(childExtension.getId()).thenReturn(extLayerId);
        Mockito.when(childExtension.getParentId()).thenReturn(parentLayerId);
        Mockito.when(childModelExtension.getId()).thenReturn(extLayerId);
        Mockito.when(parentModelExtension.getExtensions()).thenReturn(Set.of(childModelExtension));
    }

    @Test
    void must_throw_if_module_layer_parentLayer_does_not_exists() {
        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
            ctxBoot.create(null, parentModelExtension, null, null);
        });
    }

    @Test
    void must_throw_if_module_layer_of_extensions_does_not_exists() {
        Mockito.when(parentModelExtension.getId()).thenReturn(parentLayerId);

        Assertions.assertThrows(LayerNotFoundException.class, () -> {
            ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
            ctxBoot.create(null, parentModelExtension, null, null);
        });
    }

    @Test
    void must_create_a_parent_context_with_local_component() throws Exception {

        mockParentLayerAndComponents();
        mockChildLayerAndComponents();

        ContextBootstraper ctxBoot = new ContextBootstraper(layerManager);
        SbContext ctx = ctxBoot.create(null, parentModelExtension, null, null);

        assertNotNull(ctx);
        assertNotNull(ctx.getBean(LocalParentComponent.class));
    }

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

    @Singleton
    static class LocalParentComponent {}

    @Singleton
    static class LocalChildComponent {}

    @Singleton
    static class ExportedChildComponent {}
}
