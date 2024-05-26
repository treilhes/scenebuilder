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
package com.gluonhq.jfxapps.metadata.java.model.tbd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.gluonhq.jfxapps.metadata.util.Report;

/**
 * The descriptor is a map of classes to their metadata class.
 */
public class Descriptor {

    /**
     * The location of the descriptor in a zip/jar file
     */
    public static final String DESCRIPTOR_LOCATION = "META-INF/sbx-meta-descriptor.properties";

    /**
     * The map of classes to their metadata class
     */
    private Map<Class<?>, String> classToMetaClass = new HashMap<>();

    /**
     * Create an empty descriptor
     */
    public Descriptor() {
    }

    /**
     * Create a descriptor from a stream The stream should contain a properties file
     * with a list of classes as keys and their metadata classes as values.
     *
     * @param stream the stream to load the descriptor from
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Descriptor(InputStream stream) throws IOException, ClassNotFoundException {
        Properties props = new Properties();
        props.load(stream);

        for (Entry<Object, Object> entry : props.entrySet()) {
            String cls = entry.getKey().toString();
            String metaCls = entry.getValue().toString();
            classToMetaClass.put(Class.forName(cls), metaCls);
        }
    }

    /**
     * Add a class to the descriptor
     * @param key the class
     * @param metadataClassName the metadata class name
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    public String put(Class<?> key, String metadataClassName) {
        return classToMetaClass.put(key, metadataClassName);
    }

    /**
     * Get the metadata class name for a given class
     * @param key the class
     * @return the metadata class name
     */
    public String get(Class<?> key) {
        return classToMetaClass.get(key);
    }

    /**
     * Get the map of classes to their metadata class
     * @return the map of classes to their metadata class
     */
    public Map<Class<?>, String> getClassToMetaClass() {
        return classToMetaClass;
    }

    /**
     * Load the descriptor from a zip/jar file
     *
     * @param file the file to load the descriptor from
     * @return the descriptor
     */
    public static Descriptor load(File file) {
        try (ZipFile sourceZipFile = new ZipFile(file)) {
            ZipEntry entry = sourceZipFile.getEntry(DESCRIPTOR_LOCATION);
            return new Descriptor(sourceZipFile.getInputStream(entry));
        } catch (Exception e) {
            Report.error("Unable to load descriptor from " + file.getAbsolutePath(), e);
        }
        return null;
    }

    /**
     * Check if the given zip/jar file has a descriptor
     *
     * @param compressedFile the file to check
     * @return true if the file has a descriptor
     */
    public static boolean hasDescriptor(File compressedFile) {
        try (ZipFile sourceZipFile = new ZipFile(compressedFile)) {
            return sourceZipFile.getEntry(DESCRIPTOR_LOCATION) != null;
        } catch (Exception e) {
        }
        return false;
    }
}
