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
package com.gluonhq.jfxapps.boot.main.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.gluonhq.jfxapps.boot.context.config.ContextConfig;
import com.gluonhq.jfxapps.boot.layer.config.LayerConfig;
import com.gluonhq.jfxapps.boot.loader.config.LoaderConfig;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig;
import com.gluonhq.jfxapps.boot.platform.config.PlatformConfig;
import com.gluonhq.jfxapps.boot.registry.config.RegistryConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableWebMvc
@Import({RegistryConfig.class, ContextConfig.class, LoaderConfig.class, LayerConfig.class, PlatformConfig.class, RepositoryConfig.class})
public class BootConfig {

    @Bean
    public OpenAPI myOpenAPI() {
//      Server devServer = new Server();
//      devServer.setUrl(devUrl);
//      devServer.setDescription("Server URL in Development environment");
//
//      Server prodServer = new Server();
//      prodServer.setUrl(prodUrl);
//      prodServer.setDescription("Server URL in Production environment");

      Contact contact = new Contact();
      contact.setEmail("bezkoder@gmail.com");
      contact.setName("BezKoder");
      contact.setUrl("https://www.bezkoder.com");

      License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

      Info info = new Info()
          .title("Tutorial Management API")
          .version("1.0")
          .contact(contact)
          .description("This API exposes endpoints to manage tutorials.").termsOfService("https://www.bezkoder.com/terms")
          .license(mitLicense);

      return new OpenAPI().info(info);//.servers(List.of(devServer, prodServer));
    }
}
