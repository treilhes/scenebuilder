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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;
import java.nio.charset.Charset;

/**
 * Cheap replacement for datauri (only available in Java17)
 * Url of the form : inmemoryfile:sessionid/some/path/to/file will be looked into
 * {@link InMemoryFileRegistry}
 * @author ptreilhes
 *
 */
public class InMemoryFileURLStreamHandlerProvider extends URLStreamHandlerProvider {

    public static final String PROTOCOL_NAME = "inmemoryfile";

    @Override public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL_NAME.equals(protocol)) {
            return new URLStreamHandler(){
                @Override protected URLConnection openConnection(URL url) throws IOException {
                    return new StringURLConnection(url, InMemoryFileRegistry.getFile(url));
                }
            };
        }
        return null;
    }

    private static class StringURLConnection extends URLConnection {

        private final String content;

        public StringURLConnection(URL url, String content) {
            super(url);
            this.content = content;
        }

        @Override
        public void connect() throws IOException {}

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF8")));
        }

        @Override
        public boolean getAllowUserInteraction() {
            return false;
        }

        @Override
        public boolean getUseCaches() {
            return false;
        }

        @Override
        public boolean getDefaultUseCaches() {
            return false;
        }

        @Override
        public long getExpiration() {
            return System.currentTimeMillis() - 1000000;
        }

        @Override
        public long getDate() {
            return System.currentTimeMillis();
        }

        @Override
        public long getLastModified() {
            return System.currentTimeMillis();
        }

    }
}
