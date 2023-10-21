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
        XmlMapper mapper = new XmlMapper();
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
        JsonMapper mapper = new JsonMapper();
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
                    Set.of(singleExt1));

        } catch (Exception e) {
            throw new RuntimeException("Unable to setup test registry", e);
        }
    }
}
