package com.oracle.javafx.scenebuilder.metadata.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.oracle.javafx.scenebuilder.metadata.util.Report;

public class Descriptor {

    public static final String DESCRIPTOR_LOCATION = "META-INF/sbx-meta-descriptor.properties";

    public static final String CLASS_TO_META_KEY = "classToMetaMap";

    Map<Class<?>, String> classToMetaClass = new HashMap<>();

    public Descriptor() {}

    public Descriptor(InputStream stream) throws IOException, ClassNotFoundException  {
        Properties props = new Properties();
        props.load(stream);

        for (Entry<Object, Object> entry:props.entrySet()) {
            String cls = entry.getKey().toString();
            String metaCls = entry.getValue().toString();
            classToMetaClass.put(Class.forName(cls), metaCls);
        }
    }

    public String put(Class<?> key, String metadataClassName) {
        return classToMetaClass.put(key, metadataClassName);
    }

    public String get(Class<?> key) {
        return classToMetaClass.get(key);
    }



    public Map<Class<?>, String> getClassToMetaClass() {
        return classToMetaClass;
    }

    public Map<String, Object> getInputs() {
        Map<String, Object> inputs = new HashMap<>();
        inputs.put(CLASS_TO_META_KEY, classToMetaClass);
        return inputs;
    }

    public static Descriptor load(File file) {
        try (ZipFile sourceZipFile = new ZipFile(file)) {
            ZipEntry entry = sourceZipFile.getEntry(DESCRIPTOR_LOCATION);
            return new Descriptor(sourceZipFile.getInputStream(entry));
        } catch (Exception e) {
            Report.error("Unable to load descriptor from " + file.getAbsolutePath(), e);
        }
        return null;
    }

    public static boolean hasDescriptor(File file) {
        try (ZipFile sourceZipFile = new ZipFile(file)) {
            return sourceZipFile.getEntry(DESCRIPTOR_LOCATION) != null;
        } catch (Exception e) {}
        return false;
    }
}
