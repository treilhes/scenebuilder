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
package com.gluonhq.jfxapps.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLUtilsTest {

    @Test
    public void equalsShouldReturnTrueForSameURLs() throws URISyntaxException, MalformedURLException {
        URL url1 = new URL("http://example.com");
        URL url2 = new URL("http://example.com");
        assertTrue(URLUtils.equals(url1, url2));
    }

    @Test
    public void equalsShouldReturnFalseForDifferentURLs() throws URISyntaxException, MalformedURLException {
        URL url1 = new URL("http://example.com");
        URL url2 = new URL("http://example.org");
        assertFalse(URLUtils.equals(url1, url2));
    }

    @Test
    public void equalsShouldReturnTrueForNullURLs() {
        URL url1 = null;
        URL url2 = null;
        assertTrue(URLUtils.equals(url1, url2));
    }

    @Test
    public void getFileShouldReturnFileForFileURI() throws URISyntaxException {
        String os = System.getProperty("os.name").toLowerCase();
        URI uri;
        String expectedPath;

        if (os.contains("win")) {
            uri = new URI("file:///C:/path/to/file");
            expectedPath = "C:\\path\\to\\file";
        } else {
            uri = new URI("file:///path/to/file");
            expectedPath = "/path/to/file";
        }

        File file = URLUtils.getFile(uri);
        assertNotNull(file);
        assertEquals(expectedPath, file.getPath());
    }

    @Test
    public void getFileShouldReturnNullForNonFileURI() throws URISyntaxException {
        URI uri = new URI("http://example.com");
        File file = URLUtils.getFile(uri);
        assertNull(file);
    }

    @Test
    public void getFileShouldReturnFileForURLString() throws URISyntaxException {
        String os = System.getProperty("os.name").toLowerCase();
        String urlString;
        String expectedPath;

        if (os.contains("win")) {
            urlString = "file:///C:/path/to/file";
            expectedPath = "C:\\path\\to\\file";
        } else {
            urlString = "file:///path/to/file";
            expectedPath = "/path/to/file";
        }

        File file = URLUtils.getFile(urlString);
        assertNotNull(file);
        assertEquals(expectedPath, file.getPath());
    }

    @Test
    public void getFileShouldThrowExceptionForInvalidURLString() {
        String urlString = "invalid url";
        assertThrows(URISyntaxException.class, () -> URLUtils.getFile(urlString));
    }

    @Test
    public void toDataURIShouldReturnDataURIForStringContent() {
        String content = "Hello, World!";
        URI dataURI = URLUtils.toDataURI(content);
        assertNotNull(dataURI);
        assertTrue(dataURI.toString().startsWith("data:base64,"));
    }
}
