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

import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.annotation.Primary;
import com.gluonhq.jfxapps.boot.context.config.ContextConfig;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.layer.config.LayerConfig;
import com.gluonhq.jfxapps.boot.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.loader.BootException;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.config.LoaderConfig;
import com.gluonhq.jfxapps.boot.loader.content.FileExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.ApplicationExtension;
import com.gluonhq.jfxapps.boot.loader.model.Extension;
import com.gluonhq.jfxapps.boot.loader.model.JfxApps;
import com.gluonhq.jfxapps.boot.loader.model.JfxAppsExtension;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.platform.config.PlatformConfig;
import com.gluonhq.jfxapps.boot.registry.RegistryManager;

import jakarta.persistence.EntityManagerFactory;

@ExtendWith({ MockitoExtension.class, SpringExtension.class })
@SpringBootTest(classes = { SampleLoaderTestIT.Configuration.class, ContextConfig.class, LayerConfig.class, LoaderConfig.class })
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class SampleLoaderTestIT {
    private static final String RES_IT = "./src/test/resources-its/common-loader";

    private static final UUID ROOT_ID = com.gluonhq.jfxapps.boot.loader.extension.Extension.ROOT_ID;
    private static final UUID ROOT_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ROOT_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID APP1_ID = UUID.fromString("00000000-0000-0000-0001-000000000000");
    private static final UUID APP1_EXT1_ID = UUID.fromString("00000000-0000-0000-0001-000000000001");
    private static final UUID APP1_EXT1_EXT1_ID = UUID.fromString("00000000-0000-0000-0001-000000000011");

    @TestConfiguration
    static class Configuration {
        @Bean
        public JfxAppsPlatform jfxAppsPlatform() {
            JfxAppsPlatform jfxAppsPlatform = Mockito.mock(JfxAppsPlatform.class);
            when(jfxAppsPlatform.rootPath()).thenReturn(Paths.get("./target"));
            return jfxAppsPlatform;
        }

        @Bean
        @Primary
        public StateProvider stateProvider() {
            StateProvider stateProvider = Mockito.mock(StateProvider.class);
            when(stateProvider.bootState()).thenReturn(testApp());
            return stateProvider;
        }

        @Bean
        @Primary
        public RegistryManager registryManager() {
            RegistryManager registryManager = Mockito.mock(RegistryManager.class);
            return registryManager;
        }

        @Bean
        @Primary
        public RepositoryClient repositoryClient() {
            RepositoryClient repositoryClient = Mockito.mock(RepositoryClient.class);
            return repositoryClient;
        }

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
            ApplicationExtension app1_ext1 = new ApplicationExtension(APP1_EXT1_ID, new FileExtensionProvider(p_app1_ext1));
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



    @Test
    public void startTest() {



        try {
            appManager.start();
            appManager.startApplication(APP1_ID);

            var result = contextManager.get(ROOT_ID).getBeansOfType(Runnable.class);

            System.out.println(result.values().size());
            result.values().forEach(r -> r.run());
            System.out.println();
        } catch (BootException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println();
    }
}
