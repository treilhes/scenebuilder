/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.library.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//Include Java class name ("com.myempl.ImplClass") as JSON property "class"
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
@FunctionalInterface
public interface Transform<I, O> {

    public final static ObjectMapper mapper = new ObjectMapper();

    List<O> filter(List<I> inputs);

    @SuppressWarnings("unchecked")
    public static <T extends Transform<I,O>, I, O> T read(File file) throws JsonParseException, JsonMappingException, IOException {
        return (T)mapper.readValue(file, Transform.class);
    }

    public static <T extends Transform<I, O>, I, O> void write(File targetFile, T filter)
            throws JsonGenerationException, JsonMappingException, IOException {

        Path filterFilePath = targetFile.toPath();
        Path formerFilterFilePath = filterFilePath.resolveSibling(targetFile.getName() + ".tmp"); // NOI18N
        Files.deleteIfExists(formerFilterFilePath);

        try {
            // Rename already existing filter file so that we can rollback
            if (Files.exists(filterFilePath)) {
                Files.move(filterFilePath, formerFilterFilePath, StandardCopyOption.ATOMIC_MOVE);
            }

            // Create the new filter file
            mapper.writeValue(targetFile, filter);

            // Delete the former filter file
            if (Files.exists(formerFilterFilePath)) {
                Files.delete(formerFilterFilePath);
            }
        } catch (IOException ioe) {
            // Rollback
            if (Files.exists(formerFilterFilePath)) {
                Files.move(formerFilterFilePath, filterFilePath, StandardCopyOption.ATOMIC_MOVE);
            }
            throw (ioe);
        }
    }
}
