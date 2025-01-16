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

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.WebApplicationType;

import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.api.web.client.InternalRestClient;
import com.gluonhq.jfxapps.boot.context.boot.BootContext;
import com.gluonhq.jfxapps.boot.context.impl.ExtensionContext;
import com.gluonhq.jfxapps.boot.web.controller.extension.WebExtensionConfig;

class WebTestIT {

    @Test
    void must_load_root_context_controller() throws URISyntaxException, IOException {
        var bootClasses = new ArrayList<>(new WebBootClasses().classes());

        var bootContext = BootContext.create(bootClasses, WebApplicationType.SERVLET, new String[] {
                "--server.port=8080"
        }, (c) -> c.registerBean(ContextManager.class, () -> Mockito.mock(ContextManager.class)));


        var internalClient = bootContext.getBean(InternalRestClient.class);

        internalClient.get(null, "version")
            .on(200, r -> assertTrue(!r.body().isBlank()))
            .ifNoneMatch(r -> fail(r.toString())).execute();

        bootContext.stop();
    }

    @Test
    void must_load_extension_context_controller() throws URISyntaxException, IOException {
        var contextManager = Mockito.mock(ContextManager.class);
        var extension = Mockito.mock(OpenExtension.class);

        var bootClasses = new ArrayList<>(new WebBootClasses().classes());

        var bootContext = BootContext.create(bootClasses, WebApplicationType.SERVLET, new String[] {
                "--server.port=8080"
        }, (c) -> c.registerBean(ContextManager.class, () -> contextManager));

        var extensionId = UUID.randomUUID();
        var loader = WebTestIT.class.getClassLoader();

        var extensionClasses = new ArrayList<Class<?>>();
        extensionClasses.add(WebExtensionConfig.class);

        var extensionContext = ExtensionContext.create(bootContext, extensionId, extensionClasses, loader);
        extensionContext.registerBean(Extension.class, () -> extension);
        extensionContext.refresh();

        Mockito.when(extension.getId()).thenReturn(extensionId);
        Mockito.when(contextManager.get(extensionId)).thenReturn(extensionContext);

        var internalClient = bootContext.getBean(InternalRestClient.class);

        internalClient.get(extensionId, "extension/id")
            .on(200, r -> assertEquals("extension id must match", r.body(), extensionId.toString()))
            .ifNoneMatch(r -> fail(r.toString())).execute();

        bootContext.stop();
    }
}
