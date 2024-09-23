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
package app.ext1;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.api.loader.extension.SealedExtension;

import _test.TestConfig;
import app.ext1.aspect.JfxAppsAspect;
import app.ext1.controller.ExtensionController;
import app.ext1.internal.JfxAppsLocalService;
import app.ext1.model.JfxAppsModel;
import app.ext1.repository.JfxAppsRepository;
import app.ext1.rest.JfxAppsRestController;
import app.ext1.rest.RestExceptionHandler;
import app.ext1.service.JfxAppsDataService;
import app.ext1.service.JfxAppsRootExportedService;
import app.ext1.test.JfxAppsAspectTest;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
@EntityScan(basePackageClasses = {JfxAppsModel.class})
@EnableJpaRepositories(basePackageClasses = {JfxAppsRepository.class})
public class JfxAppsExtension implements OpenExtension {

    private static final UUID PARENT_ID = UUID.fromString(TestConfig.PARENT_ID);

    public static final UUID ID = UUID.fromString(TestConfig.ID);

    @Override
    public UUID getParentId() {
        return PARENT_ID;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(
                JfxAppsLocalService.class,
                JfxAppsAspect.class,
                JfxAppsAspectTest.class,
                JfxAppsRestController.class,
                JfxAppsDataService.class,
                RestExceptionHandler.class,
                ExtensionController.class);
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(
                JfxAppsRootExportedService.class
                );
    }

    @Bean
    OpenAPI myOpenAPI() {
      Contact contact = new Contact();
      contact.setEmail("xxx@xxxx.com");
      contact.setName("Xxxxxx");
      contact.setUrl("https://www.xxxx.com");

      License bds3License = new License().name("BSD 3 clause").url("https://opensource.org/license/bsd-3-clause");

      Info info = new Info()
          .title("Management API " + ID)
          .version("1.0")
          .contact(contact)
          .description("This API exposes endpoints.")
          .license(bds3License);

      return new OpenAPI().info(info);
    }

}
