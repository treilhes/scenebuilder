package com.oracle.javafx.scenebuilder.metadata.util;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

/**
 * A utility class which makes it nicer to interact with ResourceBundles. This
 * particular implementation handles looking up resources in the "primary"
 * delegate bundle and then the "secondary" delegate bundle if it didn't find
 * there resource in the primary.
 */
public final class Resources {

    private static final Map<Predicate<String>, Function<String, String>> rawTransforms = new HashMap<>();

    public static void clearRawTransforms() {
        rawTransforms.clear();
    }

    public static void addRawTransform(Predicate<String> predicate, Function<String, String> transform) {
        rawTransforms.put(predicate, transform);
    }

    private static final String ROOT_KEY = "ROOT";

    private static final Map<Object, PropertiesConfiguration> cache = new HashMap<>();

    public static void save(Set<Class<?>> set, File outputFolder) {
        cache.entrySet().forEach(e -> {
            Object key = e.getKey();
            PropertiesConfiguration p = e.getValue();
            File target = null;

            if (String.class.isAssignableFrom(key.getClass())) {
                if (ROOT_KEY.equals(key)) {
                    target = new File(outputFolder, "root.properties");
                }
            } else {
                Class<?> clsKey = (Class<?>) key;

                if (!set.contains(clsKey)) {
                    return;
                }
                File packageFolder = new File(outputFolder, toPath(clsKey));
                target = new File(packageFolder, ((Class<?>) key).getSimpleName() + ".properties");
            }

            if ((p == null || p.isEmpty()) && target.exists()) {
                target.delete();
            }
            if (p != null) {
                if (!target.getParentFile().exists()) {
                    target.getParentFile().mkdirs();
                }
                try (FileWriter fw = new FileWriter(target)) {
                    PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(p.getLayout()) {
                        @Override
                        public Set<String> getKeys() {
                            return super.getKeys().stream().sorted()
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                        }
                    };
                    layout.save(p, fw);
                } catch (Exception e1) {
                    System.out.println("Unable to save properties to " + target.getAbsolutePath());
                }

            }
        });
    }

    private final List<PropertiesConfiguration> properties = new ArrayList<>();
    private final Class<?> ownerClass;

    public Resources(final Class<?> beanClass) {
        ownerClass = beanClass;
        Class<?> current = beanClass;
        while (current != null) {
            String packageFolderName = toPath(current);
            String className = current.getSimpleName();
            String resourceName = String.format("%s/%s.properties", packageFolderName, className);

            properties.add(cache.computeIfAbsent(current, k -> loadProperties(resourceName)));

            current = current.getSuperclass();
        }

        properties.add(cache.computeIfAbsent(ROOT_KEY, k -> loadProperties("root.properties")));

    }

    private static String toPath(Class<?> cls) {
        return cls.getName().replace('$', '/').replace('.', '/').toLowerCase();
    }

    private PropertiesConfiguration loadProperties(String resourceName) {
        try (InputStreamReader isr = new InputStreamReader(
                Resources.class.getClassLoader().getResourceAsStream(resourceName))) {
            PropertiesConfiguration p = newOrderedProperties();
            p.read(isr);
            return p;
        } catch (Exception e) {
            return newOrderedProperties();
        }
    }

    // @SuppressWarnings("serial")
    private static PropertiesConfiguration newOrderedProperties() {
        return new PropertiesConfiguration();
    }

    public String get(Class<?> beanClass, String key, String defaultValue) {

        Class<?> cls = beanClass;
        while (cls != null && cls != ownerClass) {
            String packageFolderName = toPath(beanClass);
            String className = cls.getSimpleName();
            String resourceName = String.format("%s/%s.properties", packageFolderName, className);

            PropertiesConfiguration p = cache.computeIfAbsent(cls, k -> loadProperties(resourceName));

            if (p.containsKey(key)) {
                return checkTransform(key, p.getProperty(key));
            }

            cls = cls.getSuperclass();
        }

        for (PropertiesConfiguration p : properties) {
            if (p.containsKey(key)) {
                return checkTransform(key, p.getProperty(key));
            }
        }
        return defaultValue;
    }

    public void set(Class<?> beanClass, String key, String value) {
        properties.get(0).addProperty(key, value);
    }

    private static String checkTransform(String key, Object value) {
        if (value == null) {
            value = "";
        }
        if (!rawTransforms.isEmpty()) {
            for (Entry<Predicate<String>, Function<String, String>> entry : rawTransforms.entrySet()) {
                if (entry.getKey().test(key)) {
                    return entry.getValue().apply(value.toString());
                }
            }
        }
        return value.toString();
    }
}
