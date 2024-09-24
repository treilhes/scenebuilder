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
package com.gluonhq.jfxapps.registry.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.registry.mapper.impl.JsonMapper;
import com.gluonhq.jfxapps.registry.mapper.impl.XmlMapper;
import com.gluonhq.jfxapps.registry.model.Application;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.Description;
import com.gluonhq.jfxapps.registry.model.Extension;
import com.gluonhq.jfxapps.registry.model.Registry;

class MapperTest {

    private static final Registry TEST_REGISTRY = setupRegistry();

    @TempDir
    protected File testDir;

    @Test
    void should_serialize_to_xml_and_read_from_xml() throws FileNotFoundException, IOException {
        Mapper mapper = Mapper.get("xml");
        File target = new File(testDir, "target.xml");

        try (OutputStream output = new FileOutputStream(target)){
            mapper.to(TEST_REGISTRY, output);
        }

        assertTrue(target.exists());


        try (InputStream input = new FileInputStream(target)){
            Registry registry = mapper.from(input);

            assertEquals(TEST_REGISTRY, registry);
        }

    }

    @Test
    void should_serialize_to_json_and_read_from_json() throws FileNotFoundException, IOException {
        Mapper mapper = Mapper.get("json");
        File target = new File(testDir, "target.json");

        try (OutputStream output = new FileOutputStream(target)){
            mapper.to(TEST_REGISTRY, output);
        }

        assertTrue(target.exists());


        try (InputStream input = new FileInputStream(target)){
            Registry registry = mapper.from(input);

            assertEquals(TEST_REGISTRY, registry);
        }

    }

    private static Registry setupRegistry() {

        try {

            Extension innerExt1 = new Extension(
                    UUID.randomUUID(),
                    new Dependency("innerExt1.groupId", "innerExt1.artifactId", "innerExt1.version"),
                    new Description(null , "innerExt1", "innerExt1 text"),
                    Set.of());

            Extension ext1 = new Extension(
                    UUID.randomUUID(),
                    new Dependency("ext1.groupId", "ext1.artifactId", "ext1.version"),
                    new Description(null , "ext1", "ext1 text"),
                    Set.of(innerExt1));

            Application app1 = new Application(
                    UUID.randomUUID(),
                    new Dependency("app1.groupId", "app1.artifactId", "app1.version"),
                    new Description(new URI("/test"), "app1", "app1 text"),
                    Set.of(ext1));

            // single extension
            Extension singleExt1 = new Extension(
                    UUID.randomUUID(),
                    new Dependency("singleExt1.groupId", "singleExt1.artifactId", "singleExt1.version"),
                    new Description(null , "singleExt1", "singleExt1 text"),
                    Set.of());

            return new Registry(
                    UUID.randomUUID(),
                    new Dependency("registry.groupId", "registry.artifactId", "registry.version"),
                    Set.of(app1),
                    Set.of(singleExt1),
                    Set.of());

        } catch (Exception e) {
            throw new RuntimeException("Unable to setup test registry", e);
        }
    }
}
