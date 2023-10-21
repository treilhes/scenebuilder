package com.oracle.javafx.scenebuilder.sb.spi;

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
                    return new StringURLConnection(url, InMemoryFileRegistry.getFile(url.getPath()));
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
