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

import javax.inject.Inject;

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
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.annotation.Primary;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.loader.BootException;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.content.FileExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.ApplicationExtension;
import com.gluonhq.jfxapps.boot.loader.model.Extension;
import com.gluonhq.jfxapps.boot.loader.model.JfxApps;
import com.gluonhq.jfxapps.boot.loader.model.JfxAppsExtension;
import com.gluonhq.jfxapps.boot.main.config.BootConfig;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.platform.InternalRestClient;
import com.gluonhq.jfxapps.boot.platform.InternalRestClient.JsonBodyHandler;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.registry.RegistryManager;

/**
 * Those integration test ensure the following features are available. Impacted
 * module packages must be open to spring.core/spring.beans or module must be
 * open
 *
 * - Jpa - Validation - Aspect
 */
@ExtendWith({ MockitoExtension.class, SpringExtension.class })
@SpringBootTest(classes = { BootConfig.class,
        AvailableFeaturesTestIT.Configuration.class }, webEnvironment = WebEnvironment.DEFINED_PORT, properties = {
                "spring.mvc.servlet.path=/app",
                "server.servlet.context-path=/jfx",
                "debug=false" })

//@AutoConfigureCache
//@AutoConfigureDataJpa
//@AutoConfigureTestDatabase
//@AutoConfigureTestEntityManager
//@EnableJpaRepositories
@ActiveProfiles({ "it", "dev" })
@TestInstance(Lifecycle.PER_CLASS)
public class AvailableFeaturesTestIT {

    private static final String ROLLBACK_TRIGGERED_MARKER = "rollbackTriggered";
    private static final String CONTROLLER_ADVICE_HANDLED_EXCEPTION_MARKER = "controllerAdviceHandledException";

    private static final String RES_IT = "./src/test/resources-its/common-loader";

    private static final UUID ROOT_ID = com.gluonhq.jfxapps.boot.loader.extension.Extension.ROOT_ID;
    private static final UUID ROOT_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ROOT_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID APP1_ID = UUID.fromString("00000000-0000-0000-0001-000000000000");
    private static final UUID APP1_EXT1_ID = UUID.fromString("00000000-0000-0000-0001-000000000001");
    private static final UUID APP1_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0001-000000000011");

    @TestConfiguration
    static class Configuration {

        /*
         * This is a mock of the platform to be able to inject the root path where
         * extensions and applications are downloaded
         */
        @Bean
        @Primary
        JfxAppsPlatform jfxAppsPlatform() {
            JfxAppsPlatform jfxAppsPlatform = Mockito.mock(JfxAppsPlatform.class);
            when(jfxAppsPlatform.rootPath()).thenReturn(Paths.get("./target"));
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
            when(stateProvider.bootState()).thenReturn(testApp());
            return stateProvider;
        }

//        @Bean
//        @Primary
//        RegistryManager registryManager() {
//            RegistryManager registryManager = Mockito.mock(RegistryManager.class);
//            return registryManager;
//        }

//        @Bean
//        @Primary
//        RepositoryClient repositoryClient() {
//            RepositoryClient repositoryClient = Mockito.mock(RepositoryClient.class);
//            return repositoryClient;
//        }

        /*
         * This is the test application and extensions
         */
        private JfxApps testApp() {
            Path p_root = Path.of(RES_IT, "root/target/root-1.0.0-SNAPSHOT.jar");
            Path p_root_ext1 = Path.of(RES_IT, "root-ext1/target/root-ext1-1.0.0-SNAPSHOT.jar");
            Path p_root_ext1_ext1 = Path.of(RES_IT, "root-ext1-ext1/target/root-ext1-ext1-1.0.0-SNAPSHOT.jar");
            Path p_app1 = Path.of(RES_IT, "app1/target/app1-1.0.0-SNAPSHOT.jar");
            Path p_app1_ext1 = Path.of(RES_IT, "app1-ext1/target/app1-ext1-1.0.0-SNAPSHOT.jar");
            Path p_app1_ext1_ext1 = Path.of(RES_IT, "app1-ext1-ext1/target/app1-ext1-ext1-1.0.0-SNAPSHOT.jar");

            JfxApps apps = new JfxApps(ROOT_ID, new FileExtensionProvider(p_root));

            JfxAppsExtension ext1 = new JfxAppsExtension(ROOT_EXT1_ID, new FileExtensionProvider(p_root_ext1));
            Extension ext1_ext1 = new Extension(ROOT_EXT1_EXT1_ID, new FileExtensionProvider(p_root_ext1_ext1));

            Application app1 = new Application(APP1_ID, new FileExtensionProvider(p_app1));
            ApplicationExtension app1_ext1 = new ApplicationExtension(APP1_EXT1_ID,
                    new FileExtensionProvider(p_app1_ext1));
            Extension app1_ext1_ext1 = new Extension(APP1_EXT1_EXT1_ID, new FileExtensionProvider(p_app1_ext1_ext1));

            apps.addExtension(ext1);
            ext1.addExtension(ext1_ext1);

            apps.addApplication(app1);
            app1.addExtension(app1_ext1);
            app1_ext1.addExtension(app1_ext1_ext1);

            return apps;
        }
    }

    @Inject
    ModuleLayerManager layerManager;

    @Inject
    ContextManager contextManager;

    @Inject
    ApplicationManager appManager;

    @Inject
    ApplicationContext boot;

    @Inject
    InternalRestClient internalClient;

    @MockBean
    RegistryManager registryManager;

    @MockBean
    RepositoryClient repositoryClient;

    /**
     * This is a mock of the server properties to be able to inject the random port
     * in the server properties. RANDOM_PORT does not work as expected so we give it
     * a little help
     */
    @SpyBean
    ServerProperties serverProperties;

    /**
     * This work as expected and inject the random port successfully
     */
    //@LocalServerPort
    @Value("${local.server.port:0}")
    int port;

    @BeforeAll
    public void initLaunchApp() throws BootException {
        appManager.start();
        appManager.startApplication(APP1_ID);
    }

    @BeforeEach
    public void initEach() throws BootException {
        // This is a mock of the server properties to be able to inject the random port
        Mockito.when(serverProperties.getPort()).thenReturn(port);
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
        assertNotNull(contextManager.get(contextId));
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
        internalClient.get(contextId, "extension/id")
            .on(200, r -> assertEquals(contextId.toString(), r.body()))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();
    }

    @Test
    public void boot_rest_endpoint_must_be_created_and_accessible() throws Exception {
        internalClient.get(InternalRestClient.BOOT_CONTEXT, "version")
        .on(200, r -> assertEquals(200, r.statusCode()))
        .ifNoneMatch(r -> fail(r.toString()))
        .execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_repository_must_be_loaded_and_crud_functioning(UUID contextId) throws Exception {

        TestModel postParam = new TestModel();
        postParam.setData("SOMEDATA");
        postParam.setOther("SOMEDATA");

        TestModel postValue = internalClient.post(contextId, "models", jsonHeader, postParam)
            .on(200, JsonBodyHandler.of(TestModel.class), r -> assertTrue(r.body() != null && r.body().getId() > 0))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();

        TestModel getValue = internalClient.get(contextId, InternalRestClient.pathOf("models", postValue.getId()))
            .on(200, JsonBodyHandler.of(TestModel.class), r -> assertTrue(r.body() != null && r.body().getId() > 0))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();

        assertEquals(postValue.getId(), getValue.getId());

        internalClient.delete(contextId, "models/" + postValue.getId())
            .on(200, r -> assertEquals(200, r.statusCode()))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();

        internalClient.get(contextId, "models/" + postValue.getId())
            .on(200, JsonBodyHandler.of(TestModel.class), r -> assertNull(r.body()))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();
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
                .ifNoneMatch(r -> fail(r.toString()))
                .execute();

        internalClient.get(contextId, "models/query")
            .on(200, JsonBodyHandler.listOf(TestModel.class), r -> assertTrue(r.body() != null && r.body().size() > 0))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();

        internalClient.get(contextId, "models/derivation")
            .on(200, JsonBodyHandler.of(TestModel.class), r -> assertEquals(postParam.getOther(), r.body().getOther()))
            .ifNoneMatch(r -> fail(r.toString()))
            .execute();

    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_transaction_support_roolbacks_in_service(UUID contextId) throws Exception {
        String trRemoved = "transaction_rollback_in_service";
        TestModel posted = new TestModel();
        posted.setData(trRemoved);

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

        internalClient.post(contextId, "models/testing_validation_is_applied", jsonHeaderNew, posted)
                .on(400, r -> {
                    assertTrue(r.body().toLowerCase().contains("validation failed"));
                })
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void static_ressource_are_accessible(UUID contextId) throws Exception {
        internalClient.get(contextId, "images/test.png")
                .on(200, r -> {
                    assertTrue(r.body().length() > 0);
                })
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void mvc_is_enabled_and_return_html(UUID contextId) throws Exception {
        internalClient.get(contextId, "mvc/extension")
                .on(200, r -> {
                    assertTrue(r.body().contains("<html") && r.body().contains(contextId.toString()) && r.body().length() > 0);
                })
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void openapi_descriptor_and_test_webapp_are_enabled_and_return_html(UUID contextId) throws Exception {
        internalClient.get(contextId, "v3/api-docs")
                .on(200, r -> {
                    assertTrue(r.body().contains("openapi") && r.body().contains(contextId.toString()) && r.body().length() > 0);
                })
                .ifNoneMatch(r -> fail(r.toString())).execute();

        internalClient.get(contextId, "swagger-ui/index.html")
                .on(200, r -> {
                    assertTrue(r.body().contains("<html") && r.body().length() > 0);
                })
                .ifNoneMatch(r -> fail(r.toString())).execute();
    }
}
