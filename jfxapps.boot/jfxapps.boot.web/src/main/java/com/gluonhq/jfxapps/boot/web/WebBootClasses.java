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
package com.gluonhq.jfxapps.boot.web;

import java.util.List;

import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;

import com.gluonhq.jfxapps.boot.api.loader.BootContextConfigClasses;
import com.gluonhq.jfxapps.boot.web.client.InternalRestClientImpl;
import com.gluonhq.jfxapps.boot.web.controller.boot.BootConfig;
import com.gluonhq.jfxapps.boot.web.controller.boot.BootRestController;
import com.gluonhq.jfxapps.boot.web.controller.boot.DispatcherRestController;

public class WebBootClasses implements BootContextConfigClasses {

    @Override
    public List<Class<?>> classes() {
        return List.of(

                //tomcatTomcatServletWebServerFactory
                TomcatServletWebServerAutoConfiguration.class,
                org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration.class,
                //org.springframework.boot.webmvc.autoconfigure.ServletWebServerFactoryAutoConfiguration.class,
                org.springframework.boot.web.server.autoconfigure.servlet.ServletWebServerConfiguration.class,
                org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration.class,
                org.springframework.boot.servlet.autoconfigure.HttpEncodingAutoConfiguration.class,
                org.springframework.boot.servlet.autoconfigure.MultipartAutoConfiguration.class,
                org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration.class,

                //thymeleaf
                org.springframework.boot.thymeleaf.autoconfigure.ThymeleafAutoConfiguration.class,

                //springdoc
                org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
                org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration.class,
                org.springdoc.core.configuration.SpringDocConfiguration.class,
                org.springdoc.core.properties.SpringDocConfigProperties.class,
                //org.springdoc.core.configuration.SpringDocJavadocConfiguration.class,
                //org.springdoc.core.configuration.SpringDocGroovyConfiguration.class,
                //org.springdoc.core.configuration.SpringDocSecurityConfiguration.class,
                //org.springdoc.core.configuration.SpringDocFunctionCatalogConfiguration.class,
                //org.springdoc.core.configuration.SpringDocHateoasConfiguration.class,
                org.springdoc.core.configuration.SpringDocPageableConfiguration.class,
                org.springdoc.core.configuration.SpringDocSortConfiguration.class,
                org.springdoc.core.configuration.SpringDocSpecPropertiesConfiguration.class,
                //org.springdoc.core.configuration.SpringDocDataRestConfiguration.class,
                //org.springdoc.core.configuration.SpringDocKotlinConfiguration.class,
                //org.springdoc.core.configuration.SpringDocKotlinxConfiguration.class,
                //org.springdoc.core.configuration.SpringDocJacksonKotlinModuleConfiguration.class,
                org.springdoc.webmvc.ui.SwaggerConfig.class,
                org.springdoc.core.properties.SwaggerUiConfigProperties.class,
                org.springdoc.core.properties.SwaggerUiOAuthProperties.class,
                org.springdoc.core.configuration.SpringDocUIConfiguration.class,

                //tmp
                org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration.class,
                org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration.class,
                //end tmp

                //jfxapps
                BootConfig.class,
                BootRestController.class,
                DispatcherRestController.class,
                InternalRestClientImpl.class
                );
    }

}
