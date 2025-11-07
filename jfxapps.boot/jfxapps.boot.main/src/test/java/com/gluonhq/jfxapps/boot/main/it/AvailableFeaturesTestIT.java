/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.main.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.annotation.Primary;
import com.gluonhq.jfxapps.boot.api.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.api.loader.BootException;
import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifactManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.web.client.InternalRestClient;
import com.gluonhq.jfxapps.boot.api.web.client.InternalRestClient.JsonBodyHandler;
import com.gluonhq.jfxapps.boot.context.boot.BootContextFactory;
import com.gluonhq.jfxapps.boot.context.boot.BootContextInitializer;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.content.FileExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.internal.jpa.model.Extension;
import com.gluonhq.jfxapps.boot.loader.model.LoadableContent;

import jakarta.inject.Inject;

/**
 * Those integration test ensure the following features are available. Impacted
 * module packages must be open to spring.core/spring.beans or module must be
 * open
 *
 * - Jpa - Validation - Aspect
 */
@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@SpringBootTest(classes = { AvailableFeaturesTestIT.Configuration.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
                "spring.mvc.servlet.path=/app", "server.servlet.context-path=/jfx",
                "jfxapps.repository.directory=./target/it", "debug=true" })
@ContextConfiguration(loader = AvailableFeaturesTestIT.TestContextLoader.class)
@ActiveProfiles({ "it", "dev" })
@TestInstance(Lifecycle.PER_CLASS)
public class AvailableFeaturesTestIT {

    private static final String ROLLBACK_TRIGGERED_MARKER = "rollbackTriggered";
    private static final String CONTROLLER_ADVICE_HANDLED_EXCEPTION_MARKER = "controllerAdviceHandledException";

    private static final String RES_IT = "./src/test/resources-its/common-loader";

    private static final UUID ROOT_ID = OpenExtension.ROOT_ID;
    private static final UUID ROOT_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID ROOT_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000111");

    private static final UUID APP1_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID APP1_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private static final UUID APP1_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000112");

    public static class TestContextLoader extends SpringBootContextLoader {

        @Override
        protected SpringApplication getSpringApplication() {

            var application = super.getSpringApplication();
            application.setApplicationContextFactory(new BootContextFactory());

            var filteredClasses = List.of(JfxAppsPlatform.class, StateProvider.class,
            		Service.class, RegistryManager.class, RegistryArtifactManager.class);
            application.addInitializers(new BootContextInitializer(List.of(), filteredClasses));

            return application;
        }

        @Override
        protected ApplicationContextFactory getApplicationContextFactory(MergedContextConfiguration mergedConfig) {
            return new BootContextFactory();
            //return super.getApplicationContextFactory(mergedConfig);
        }


    }

    @TestConfiguration
    @SpringBootConfiguration
    static class Configuration {

        /*
         * This is a mock of the platform to be able to inject the root path where
         * extensions and applications are downloaded
         */
        @Bean
        @Primary
        JfxAppsPlatform jfxAppsPlatform() {
            JfxAppsPlatform jfxAppsPlatform = Mockito.mock(JfxAppsPlatform.class);
            when(jfxAppsPlatform.rootPath()).thenReturn(Paths.get("./target/it"));
            when(jfxAppsPlatform.getAvailableProcessors()).thenReturn(4);
            return jfxAppsPlatform;
        }

        /*
         * This is a mock of the state provider to be able to inject the test
         * application and extensions
         */
        @Bean
        @Primary
        StateProvider stateProvider() {
            StateProvider stateProvider = Mockito.mock(StateProvider.class);
            when(stateProvider.applicationState(Mockito.eq(ROOT_ID) , Mockito.any())).thenReturn(rootApp());
            when(stateProvider.applicationState(Mockito.eq(APP1_ID) , Mockito.any())).thenReturn(testApp());
            return stateProvider;
        }

        /*
         * This is the test application and extensions
         */
        private LoadableContent rootApp() {
            Path p_root = Path.of(RES_IT, "root/target/root-1.0.0-SNAPSHOT.jar");
            Path p_root_ext1 = Path.of(RES_IT, "root-ext1/target/root-ext1-1.0.0-SNAPSHOT.jar");
            Path p_root_ext1_ext1 = Path.of(RES_IT, "root-ext1-ext1/target/root-ext1-ext1-1.0.0-SNAPSHOT.jar");

            LoadableContent root = new LoadableContent();
            Extension rootExt = new Extension();
            rootExt.setId(ROOT_ID);
            root.setExtension(rootExt);
            root.setContentProvider(new FileExtensionProvider(p_root));

            LoadableContent root_ext1 = new LoadableContent();
            Extension rootExt_ext1 = new Extension();
            rootExt_ext1.setId(ROOT_EXT1_ID);
            root_ext1.setExtension(rootExt_ext1);
            root_ext1.setContentProvider(new FileExtensionProvider(p_root_ext1));

            rootExt.getExtensions().add(rootExt_ext1);
            root.addExtension(root_ext1);
            rootExt_ext1.setParentExtension(rootExt);

            LoadableContent root_ext1_ext1 = new LoadableContent();
            Extension rootExt_ext1_ext1 = new Extension();
            rootExt_ext1_ext1.setId(ROOT_EXT1_EXT1_ID);
            root_ext1_ext1.setExtension(rootExt_ext1_ext1);
            root_ext1_ext1.setContentProvider(new FileExtensionProvider(p_root_ext1_ext1));

            rootExt_ext1.getExtensions().add(rootExt_ext1_ext1);
            root_ext1.addExtension(root_ext1_ext1);
            rootExt_ext1_ext1.setParentExtension(rootExt_ext1);

            return root;
        }

        /*
         * This is the test application and extensions
         */
        private LoadableContent testApp() {
            Path p_app1 = Path.of(RES_IT, "app1/target/app1-1.0.0-SNAPSHOT.jar");
            Path p_app1_ext1 = Path.of(RES_IT, "app1-ext1/target/app1-ext1-1.0.0-SNAPSHOT.jar");
            Path p_app1_ext1_ext1 = Path.of(RES_IT, "app1-ext1-ext1/target/app1-ext1-ext1-1.0.0-SNAPSHOT.jar");

            LoadableContent app1 = new LoadableContent();
            Extension app1Ext = new Extension();
            app1Ext.setId(APP1_ID);
            app1.setExtension(app1Ext);
            app1.setContentProvider(new FileExtensionProvider(p_app1));

            LoadableContent app1_ext1 = new LoadableContent();
            Extension app1Ext_ext1 = new Extension();
            app1Ext_ext1.setId(APP1_EXT1_ID);
            app1_ext1.setExtension(app1Ext_ext1);
            app1_ext1.setContentProvider(new FileExtensionProvider(p_app1_ext1));

            app1Ext.getExtensions().add(app1Ext_ext1);
            app1.addExtension(app1_ext1);
            app1Ext_ext1.setParentExtension(app1Ext);

            LoadableContent app1_ext1_ext1 = new LoadableContent();
            Extension app1Ext_ext1_ext1 = new Extension();
            app1Ext_ext1_ext1.setId(APP1_EXT1_EXT1_ID);
            app1_ext1_ext1.setExtension(app1Ext_ext1_ext1);
            app1_ext1_ext1.setContentProvider(new FileExtensionProvider(p_app1_ext1_ext1));

            app1Ext_ext1.getExtensions().add(app1Ext_ext1_ext1);
            app1_ext1.addExtension(app1_ext1_ext1);
            app1Ext_ext1_ext1.setParentExtension(app1Ext_ext1);

            return app1;
        }
    }

    @Inject
    ApplicationContext boot;

    InternalRestClient internalClient;

    @MockitoBean
    RegistryManager registryManager;

    @MockitoBean
    RepositoryClient repositoryClient;

    /**
     * This is a mock of the server properties to be able to inject the random port
     * in the server properties. RANDOM_PORT does not work as expected so we give it
     * a little help
     */
    @MockitoSpyBean
    ServerProperties serverProperties;

    /**
     * This work as expected and inject the random port successfully
     */
    // @LocalServerPort
    @Value("${local.server.port:0}")
    int port;

    @BeforeAll
    public void initLaunchApp() throws BootException {
        var appManager = boot.getBean(ApplicationManager.class);
        appManager.start();
        appManager.startApplication(APP1_ID);


    }

    @BeforeEach
    public void initEach() throws BootException {
        // This is a mock of the server properties to be able to inject the random port
        Mockito.when(serverProperties.getPort()).thenReturn(port);

        internalClient = boot.getBean(InternalRestClient.class);
    }

    private static Stream<UUID> allContextIds() {
        return Stream.of(ROOT_ID, ROOT_EXT1_ID, ROOT_EXT1_EXT1_ID, APP1_ID, APP1_EXT1_ID, APP1_EXT1_EXT1_ID);

    }

    private static Stream<Arguments> allContextIdsAndParents() {
        return Stream.of(Arguments.of(ROOT_ID, List.of()), Arguments.of(ROOT_EXT1_ID, List.of(ROOT_ID)),
                Arguments.of(ROOT_EXT1_EXT1_ID, List.of(ROOT_ID, ROOT_EXT1_ID)),
                Arguments.of(APP1_ID, List.of(ROOT_ID)), Arguments.of(APP1_EXT1_ID, List.of(ROOT_ID, APP1_ID)),
                Arguments.of(APP1_EXT1_EXT1_ID, List.of(ROOT_ID, APP1_ID, APP1_EXT1_ID)));
    }

    private final static InternalRestClient.RequestConfig jsonHeader = r -> r.header("Content-Type",
            "application/json");
    private final static InternalRestClient.RequestConfig jsonHeaderNew = r -> r.header("Content-Type",
            "application/json");

    /**
     * This test ensure that the context are created and accessible
     *
     * @param contextId
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("allContextIds")
    public void context_must_be_created_and_accessible(UUID contextId) throws Exception {
        assertNotNull(boot.getBean(ContextManager.class).get(contextId));
    }

    /**
     * This test ensure that the rest endpoint are created and accessible
     *
     * @param contextId
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("allContextIds")
    public void extension_rest_endpoint_must_be_created_and_accessible(UUID contextId) throws Exception {
        internalClient.get(contextId, "extension/id").on(200, r -> assertEquals(contextId.toString(), r.body()))
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @Test
    public void boot_rest_endpoint_must_be_created_and_accessible() throws Exception {
        internalClient.get(InternalRestClient.BOOT_CONTEXT, "version").on(200, r -> assertEquals(200, r.statusCode()))
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_repository_must_be_loaded_and_crud_functioning(UUID contextId) throws Exception {

        TestModel postParam = new TestModel();
        postParam.setData("SOMEDATA");
        postParam.setOther("SOMEDATA");

        TestModel postValue = internalClient.post(contextId, "models", jsonHeader, postParam)
                .on(200, JsonBodyHandler.of(TestModel.class), r -> assertTrue(r.body() != null && r.body().getId() > 0))
                .ifNoneMatch(r -> fail(r.toString())).execute();

        TestModel getValue = internalClient.get(contextId, InternalRestClient.pathOf("models", postValue.getId()))
                .on(200, JsonBodyHandler.of(TestModel.class), r -> assertTrue(r.body() != null && r.body().getId() > 0))
                .ifNoneMatch(r -> fail(r.toString())).execute();

        assertEquals(postValue.getId(), getValue.getId());

        internalClient.delete(contextId, "models/" + postValue.getId()).on(200, r -> assertEquals(200, r.statusCode()))
                .ifNoneMatch(r -> fail(r.toString())).execute();

        internalClient.get(contextId, "models/" + postValue.getId())
                .on(200, JsonBodyHandler.of(TestModel.class), r -> assertNull(r.body()))
                .ifNoneMatch(r -> fail(r.toString())).execute();
        ;
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_query_and_query_by_derivation_support(UUID contextId) throws Exception {

        TestModel postParam = new TestModel();
        postParam.setData("SOMEDATA");
        postParam.setOther("SOMEDATA");

        internalClient.post(contextId, "models", jsonHeader, postParam)
                .on(200, JsonBodyHandler.of(TestModel.class), r -> assertTrue(r.body() != null && r.body().getId() > 0))
                .ifNoneMatch(r -> fail(r.toString())).execute();

        internalClient.get(contextId, "models/query")
                .on(200, JsonBodyHandler.listOf(TestModel.class),
                        r -> assertTrue(r.body() != null && r.body().size() > 0))
                .ifNoneMatch(r -> fail(r.toString())).execute();

        internalClient.get(contextId, "models/derivation")
                .on(200, JsonBodyHandler.of(TestModel.class),
                        r -> assertEquals(postParam.getOther(), r.body().getOther()))
                .ifNoneMatch(r -> fail(r.toString())).execute();

    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_transaction_support_roolbacks_in_service(UUID contextId) throws Exception {
        String trRemoved = "transaction_rollback_in_service";
        TestModel posted = new TestModel();
        posted.setData(trRemoved);
        posted.setOther("objectIsValid");
        internalClient.post(contextId, "models/transaction_rollback_in_service", jsonHeaderNew, posted)
                .on(500, JsonBodyHandler.of(Error.class),
                        r -> assertEquals(ROLLBACK_TRIGGERED_MARKER, r.body().getMessage()))
                .ifNoneMatch(r -> fail("500 should have matched," + r.toString())).execute();

        internalClient.get(contextId, "models").ifNoneMatch(JsonBodyHandler.listOf(TestModel.class), r -> {
            List<TestModel> models = r.body();
            boolean found = models.stream().filter(m -> trRemoved.equals(m.getData())).findAny().isPresent();
            assertFalse(found);
        }).execute();

    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_transaction_support_roolbacks_in_repository(UUID contextId) throws Exception {
        String trRemoved = "transaction_rollback_in_repository";
        TestModel posted = new TestModel();
        posted.setData(trRemoved);
        posted.setOther("objectIsValid");
        internalClient.post(contextId, "models/transaction_rollback_in_repository", jsonHeaderNew, posted)
                .on(500, JsonBodyHandler.of(Error.class),
                        r -> assertEquals(ROLLBACK_TRIGGERED_MARKER, r.body().getMessage()))
                .ifNoneMatch(r -> fail("500 should have matched, " + r.toString())).execute();

        internalClient.get(contextId, "models").ifNoneMatch(JsonBodyHandler.listOf(TestModel.class), r -> {
            List<TestModel> models = r.body();
            boolean found = models.stream().filter(m -> trRemoved.equals(m.getData())).findAny().isPresent();
            assertFalse(found);
        }).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void controller_advice_handle_exceptions(UUID contextId) throws Exception {
        internalClient.get(contextId, "models/throw_controler_advice_handled_exception")
                .on(500, JsonBodyHandler.of(Error.class),
                        r -> assertEquals(CONTROLLER_ADVICE_HANDLED_EXCEPTION_MARKER, r.body().getMessage()))
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIdsAndParents")
    public void all_aspects_from_root_to_extensions_must_applied(UUID contextId, List<UUID> parents) throws Exception {
        internalClient.get(contextId, "models/testing_aspects_are_applied").on(200, r -> {
            assertTrue(r.body().contains("_" + contextId + "_"));
            for (UUID parent : parents) {
                assertTrue(r.body().contains("_" + parent + "_"));
            }
        }).ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void testing_validation_is_applied(UUID contextId) throws Exception {
        TestModel posted = new TestModel(); // "other" is null, so 400 bad request is expected

        internalClient.post(contextId, "models/testing_validation_is_applied", jsonHeaderNew, posted).on(400, r -> {
            assertTrue(r.body().toLowerCase().contains("validation failed"));
        }).ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void static_ressource_are_accessible(UUID contextId) throws Exception {
        internalClient.get(contextId, "images/test.png").on(200, r -> {
            assertTrue(r.body().length() > 0);
        }).ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void mvc_is_enabled_and_return_html(UUID contextId) throws Exception {
        internalClient.get(contextId, "mvc/extension").on(200, r -> {
            assertTrue(r.body().contains("<html") && r.body().contains(contextId.toString()) && r.body().length() > 0);
        }).ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void openapi_descriptor_and_test_webapp_are_enabled_and_return_html(UUID contextId) throws Exception {
        internalClient.get(contextId, "v3/api-docs").on(200, r -> {
            assertTrue(
                    r.body().contains("openapi") && r.body().contains(contextId.toString()) && r.body().length() > 0);
        }).ifNoneMatch(r -> fail(r.toString())).execute();

        internalClient.get(contextId, "swagger-ui/index.html").on(200, r -> {
            assertTrue(r.body().contains("<html") && r.body().length() > 0);
        }).ifNoneMatch(r -> fail(r.toString())).execute();
    }

    /**
     * This test ensure that the application scoped bean JfxAppsRootExportedService
     * loaded in the application context APP1 did successfully resolve the
     * dependency to the local service from its source extension ROOT_EXT1_ID
     * context due to the annotation {@literal @}LayerContext <br/>
     *
     * @ApplicationSingleton<br/>
     *                            public class JfxAppsRootExportedService implements
     *                            RootExportedService {<br/>
     *                            public JfxAppsRootExportedService(@LayerContext
     *                            JfxAppsService local) {}}<br/>
     */
    @Test
    public void root_service_load_bean_in_source_context() throws Exception {
        internalClient.get(APP1_ID, "RootExportedService/list").on(200, r -> {
            assertTrue(r.body().contains("app.ext1.internal.JfxAppsLocalService"));
        }).ifNoneMatch(r -> fail(r.toString())).execute();

    }

}
