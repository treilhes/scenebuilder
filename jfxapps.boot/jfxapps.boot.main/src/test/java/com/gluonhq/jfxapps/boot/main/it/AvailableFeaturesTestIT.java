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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
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
import com.gluonhq.jfxapps.boot.platform.InternalRestClient.RequestException;
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
@SpringBootTest(classes = { BootConfig.class, AvailableFeaturesTestIT.Configuration.class },
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"spring.main.allow-bean-definition-overriding=true"
            ,"spring.mvc.servlet.path=/app"
            ,"server.servlet.context-path=/jfx"
            })
//@AutoConfigureCache
//@AutoConfigureDataJpa
//@AutoConfigureTestDatabase
//@AutoConfigureTestEntityManager
//@EnableJpaRepositories
@ActiveProfiles({ "it", "dev" })
@TestInstance(Lifecycle.PER_CLASS)
public class AvailableFeaturesTestIT {

    private static final Logger logger = LoggerFactory.getLogger(AvailableFeaturesTestIT.class);

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
         * This is a mock of the platform to be able to inject the root path
         * where extensions and applications are downloaded
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
    InternalRestClient httpClient;

    @MockBean
    RegistryManager registryManager;

    @MockBean
    RepositoryClient repositoryClient;


    /**
     * This is a mock of the server properties to be able to inject the random port
     * in the server properties. RANDOM_PORT does not work as expected so we give it a little help
     */
    @SpyBean
    ServerProperties serverProperties;

    /**
     * This work as expected and inject the random port successfully
     */
    @LocalServerPort
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
        //return Stream.of(ROOT_ID, ROOT_EXT1_ID, ROOT_EXT1_EXT1_ID, APP1_ID, APP1_EXT1_ID, APP1_EXT1_EXT1_ID);
        return Stream.of(ROOT_ID, ROOT_EXT1_ID);
    }

    private final static InternalRestClient.RequestConfig jsonHeader = r -> r.header("Content-Type",
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
    public void rest_endpoint_must_be_created_and_accessible(UUID contextId) throws Exception {
        HttpResponse<String> response = httpClient.get(contextId, "extension");
        assertEquals(contextId.toString(), response.body());
    }

    @Test
    public void boot_rest_endpoint_must_be_created_and_accessible() throws Exception {
        HttpResponse<String> response = httpClient.get(InternalRestClient.BOOT_CONTEXT, "version");
        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void lookForCallableTmp(UUID contextId) throws Exception {
        var ctx = contextManager.get(contextId);
        Map<String, Callable<Boolean>> rext = ctx.getBeansOfTypeWithGeneric(Callable.class, Boolean.class);

        for (var callable : rext.values()) {
            System.out.println(callable.getClass().getName() + " will be CALLED");
            assertTrue(callable.call());
        }
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void lookForRunnableTmp(UUID contextId) throws Exception {
        var ctx = contextManager.get(contextId);
        var result = ctx.getBeansOfType(Runnable.class);

        System.out.println(result.values().size());

        result.values().forEach(r -> {
            System.out.println(r.getClass().getName() + " will RUN");
            r.run();
        });
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_repository_must_be_loaded_and_crud_functioning(UUID contextId) throws Exception {

        TestModel postParam = new TestModel();
        postParam.setData("SOMEDATA");
        postParam.setOther("SOMEDATA");

        HttpResponse<TestModel> postResponse = httpClient.post(contextId, "models", jsonHeader, postParam,
                TestModel.class);

        TestModel postValue = postResponse.body();
        assertNotNull(postValue);
        assertTrue(postValue.getId() > 0);

        var getResponse = httpClient.get(contextId, "models/" + postValue.getId(), TestModel.class);

        var getValue = getResponse.body();
        assertNotNull(getValue);
        assertEquals(postValue.getId(), getValue.getId());

        var deleteReponse = httpClient.delete(contextId, "models/" + getValue.getId());
        assertEquals(200, deleteReponse.statusCode());

        getResponse = httpClient.get(contextId, "models/" + postValue.getId(), TestModel.class);
        getValue = getResponse.body();
        assertNull(getValue);
    }

    @ParameterizedTest
    @MethodSource("allContextIds")
    public void jpa_transaction_support_roolbacks(UUID contextId) throws Exception {

        try {

            TestModel param = new TestModel();
            param.setData("SOMEDATA");
            param.setOther("SOMEDATA");

            HttpResponse<TestModel> response = httpClient.post(contextId, "models", jsonHeader, param, TestModel.class);

            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models/derivation"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models/derivation"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models/query"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logResponse(httpClient.get(contextId, "models/query"));
            logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

            TestModel transparam = new TestModel();
            transparam.setData("TRANSACTION_REMOVED");

            try {
                logResponse(httpClient.get(contextId, "models/transaction_rollback_in_service", TestModel.class));
            } catch (RequestException e) {
                logException(e);
            }
            try {
                logResponse(httpClient.get(contextId, "models/transaction_rollback_in_repository", TestModel.class));
            } catch (RequestException e) {
                logException(e);
            }

//            logResponse(httpClient.post(contextId, "models", jsonHeader, "{\"value\": \"someValue\"}"));
//            logResponse(httpClient.get(contextId, "models"));
            System.out.println();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println();
    }

    private void logResponse(HttpResponse<?> r) {
        System.out.println(String.format("%s %s", r, r.body()));
    }

    private void logResponse(Supplier<HttpResponse<?>> r) {
        try {
            var response = r.get();
            System.out.println(String.format("%s %s", response, response.body()));
        } catch (RequestException e) {
            var response = e.getResponse();
            System.out.println(String.format("%s %s", response, response.body()));
        }

    }

    private void logException(RequestException e) {
        var response = e.getResponse();
        System.out.println(String.format("%s %s", response, response.body()));
    }
}
