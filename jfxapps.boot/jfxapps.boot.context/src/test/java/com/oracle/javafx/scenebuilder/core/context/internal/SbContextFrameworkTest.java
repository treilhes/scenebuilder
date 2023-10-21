package com.oracle.javafx.scenebuilder.core.context.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Lazy;
import com.oracle.javafx.scenebuilder.core.context.annotation.PreferedConstructor;
import com.oracle.javafx.scenebuilder.core.context.annotation.Primary;
import com.oracle.javafx.scenebuilder.core.context.annotation.Singleton;
import com.oracle.javafx.scenebuilder.core.context.impl.ContextManager;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Provider;


/**
 * In case of switching the dependency injection framework, those tests exhibit some of the needed features of the new framework
 * when using jakarta.inject
 */
class SbContextFrameworkTest {

    @Test
    void test_injection_feature_optional() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { OptionalFeature.class };
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(OptionalFeature.class));
    }

    @Test
    void test_injection_feature_generic() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { Component1.class, Component2.class, Component3.class, GenericFeature.class};
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(GenericFeature.class));
    }

    @Test
    void test_injection_feature_list() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { Component1.class, Component2.class, Component3.class, ListFeature.class};
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(ListFeature.class));
    }

    @Test
    void test_injection_feature_lazy_injection() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { Component1.class, Component2.class, Component3.class, LazyInjectionFeature.class };
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(LazyInjectionFeature.class));
    }

    @Test
    void test_injection_feature_multiple_constructor() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { Component1.class, MultiConstructorFeature.class };
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(MultiConstructorFeature.class));
    }

    @Test
    void test_injection_feature_primary() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { PrimaryComponent.class, SecondaryComponent.class, PrimaryInjectionFeature.class };
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        assertNotNull(ctx.getBean(PrimaryInjectionFeature.class));
    }

    @Test
    void test_injection_feature_lazy_initialization() {
        ContextManager mng = new ContextManager();
        Class<?>[] classes = { LazyInitComponent.class, LazyInitFeature.class };
        SbContext ctx = mng.create(null, UUID.randomUUID(), classes, null, null);
        LazyInitFeature lif = ctx.getBean(LazyInitFeature.class);

        assertEquals(0, LazyInitComponent.instanciated);
        LazyInitComponent cmp = lif.lazy.get();
        assertEquals(1, LazyInitComponent.instanciated);
        assertNotNull(cmp);;
    }

    static interface SomeService<T> {}
    static interface UnknownService {}
    static interface OtherService {}
    static interface Generic1 {}
    static interface Generic2 {}

    @Singleton
    static class Component1 implements SomeService<Generic1> {}

    @Singleton
    static class Component2 implements SomeService<Generic2> {}

    @Singleton
    static class Component3 implements SomeService<Generic2> {}

    @Singleton
    @Primary
    static class PrimaryComponent implements OtherService {}

    @Singleton
    static class SecondaryComponent implements OtherService {}

    @Singleton
    @Lazy
    static class LazyInitComponent {
        public static int instanciated = 0;
        public LazyInitComponent() {
            super();
            instanciated++;
        }
    }

    @Singleton
    static class MultiConstructorFeature {
        private Component1 test;
        public MultiConstructorFeature() {}
        @PreferedConstructor
        public MultiConstructorFeature(Component1 test) {
            this.test = test;
        }
        @PostConstruct
        public void then() {
            assertNotNull(test);
        }
    }

    @Singleton
    static class OptionalFeature {
        public OptionalFeature(Optional<UnknownService> optional) {
            assertTrue(optional.isEmpty());
        }
    }

    @Singleton
    static class GenericFeature {
        public GenericFeature(SomeService<Generic1> service) {
            assertNotNull(service);
        }
    }

    @Singleton
    static class ListFeature {
        public ListFeature(
                List<SomeService<Generic2>> services,
                Optional<List<SomeService<?>>> optionalList) {
            assertEquals(2, services.size());
            optionalList.orElseThrow();
            assertEquals(3, optionalList.get().size());
        }
    }

    @Singleton
    static class LazyInjectionFeature {
        public LazyInjectionFeature(Provider<SomeService<Generic1>> service) {
            assertNotNull(service.get());
        }
    }

    @Singleton
    static class PrimaryInjectionFeature {
        public PrimaryInjectionFeature(OtherService service) {
            assertNotNull(service);
        }
    }

    @Singleton
    static class LazyInitFeature {
        public Provider<LazyInitComponent> lazy;
        public LazyInitFeature(Provider<LazyInitComponent> lazy) {
            this.lazy = lazy;
        }
    }
}
