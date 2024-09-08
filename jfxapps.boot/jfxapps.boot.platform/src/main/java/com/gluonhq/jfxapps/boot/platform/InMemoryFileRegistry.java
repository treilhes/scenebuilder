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

package com.gluonhq.jfxapps.boot.platform;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an in-memory file registry. It is used to store file
 * content associated with a session ID and a file path.
 */
@Deprecated
public class InMemoryFileRegistry {

    // The registry is a map where the key is a session ID and the value is another
    // map.
    // The inner map's key is a file path and its value is the file content.
    private static Map<String, Map<String, String>> registry = new HashMap<>();

    /**
     * Clears the session from the registry.
     *
     * @param sessionId The ID of the session to be cleared.
     */
    public static void clearSession(String sessionId) {
        registry.remove(sessionId);
    }

    /**
     * Adds a file to the registry.
     *
     * @param sessionId The ID of the session.
     * @param path      The path of the file.
     * @param content   The content of the file.
     */
    public static void addFile(String sessionId, String path, String content) {
        registry.computeIfAbsent(sessionId, (k) -> new HashMap<>()).put(path, content);
    }

    /**
     * Retrieves a file from the registry.
     *
     * @param URL The full path of the file, including the session ID.
     * @return The content of the file.
     * @throws FileNotFoundException If the file is not found in the registry.
     */
    public static String getFile(URL url) throws FileNotFoundException {

        if (url == null) {
            throw new IllegalArgumentException("url can't be null");
        }

        if (url.getPath() == null) {
            throw new FileNotFoundException(url.toString());
        }

        String sessionId = url.getHost();
        String path = url.getPath();

        Map<String, String> files = registry.get(sessionId);

        if (files == null) {
            throw new FileNotFoundException(url.toString());
        }

        String content = files.get(path);

        if (content == null) {
            throw new FileNotFoundException(url.toString());
        }

        return content;
    }
}
