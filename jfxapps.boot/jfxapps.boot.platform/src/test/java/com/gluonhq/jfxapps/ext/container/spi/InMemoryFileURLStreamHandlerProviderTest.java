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
package com.gluonhq.jfxapps.ext.container.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryFileURLStreamHandlerProviderTest {

    @BeforeEach
    void setUp() {
        InMemoryFileRegistry.clearSession("session1");
        InMemoryFileRegistry.addFile("session1", "/path/to/file", "file content");
    }

    @Test
    void createURLStreamHandler_withValidProtocol_returnsHandler() throws IOException {
        InMemoryFileURLStreamHandlerProvider provider = new InMemoryFileURLStreamHandlerProvider();
        URLStreamHandler handler = provider.createURLStreamHandler(InMemoryFileURLStreamHandlerProvider.PROTOCOL_NAME);
        assertNotNull(handler);
    }

    @Test
    void createURLStreamHandler_withInvalidProtocol_returnsNull() {
        InMemoryFileURLStreamHandlerProvider provider = new InMemoryFileURLStreamHandlerProvider();
        URLStreamHandler handler = provider.createURLStreamHandler("invalid");
        assertNull(handler);
    }

    @Test
    void url_withNonExistingFile_throwsIOException() throws MalformedURLException, URISyntaxException {
        URI uri = new URI(InMemoryFileURLStreamHandlerProvider.PROTOCOL_NAME, "session1", "/session1/non/existing/file", null);
        URL url = uri.toURL();

        assertThrows(IOException.class, () -> url.openConnection());
    }

    @Test
    void url_withValidProtocol_returnsFileContent() throws IOException, URISyntaxException {
        URI uri = new URI(InMemoryFileURLStreamHandlerProvider.PROTOCOL_NAME, "session1", "/path/to/file", null);
        URL url = uri.toURL();
        URLConnection connection = url.openConnection();
        assertNotNull(connection);
        assertEquals("file content", new String(connection.getInputStream().readAllBytes()));
    }
}
