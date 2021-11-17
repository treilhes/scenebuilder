/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.oracle.javafx.scenebuilder.metadata.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.javafx.scenebuilder.metadata.bean.PropertyMetaData.Type;
import com.oracle.javafx.scenebuilder.metadata.util.ReflectionUtils;
import com.oracle.javafx.scenebuilder.metadata.util.Report;
import com.oracle.javafx.scenebuilder.metadata.util.Resources;

//import javafx.beans.DefaultProperty;
//import javafx.scene.image.Image;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * Represents the meta-data for a JavaFX Bean. A BeanMetaData is created by
 * invoking one of the constructors. These constructors reflect on a Class
 * representing a JavaFX Bean and extracts and/or generates the meta-data for
 * that class. Some information, such as the actual name of the bean, are
 * determined reflectively and cannot be customized. Most information however
 * can be customized either through the use of the {@link Bean} annotation, or
 * through the use of one or more {@link java.util.ResourceBundle}s.
 * <p>
 * During the reflective process, this class will look first for a resource
 * bundle named after the bean, and then for a generic "resources"
 * ResourceBundle located in the same package as the bean.
 * <p>
 * For example, suppose I had a bean named MyWidget. In the same package as
 * MyWidget there may be both a resources.properties, and a
 * MyWidgetResources.properties. When looking up the displayName of MyWidget,
 * BeanMetaData will first:
 * <ol>
 * <li>Check for a Bean annotation with a specified displayName.
 * <ol>
 * <li>If the displayName does not start with a % character and is not
 * {@link Bean.COMPUTE}, then take the value of displayName as a literal
 * value.</li>
 * <li>If the displayName starts with a % character, then take the displayName
 * as the key for a corresponding value in the resource bundles. First check the
 * MyWidgetResources bundle, and if there is no entry then check the resources
 * bundle.</li>
 * <li>If the displayName is {@link Bean.COMPUTE}, then look in the resource
 * bundles (first in MyWidgetResources and then in resources) for an entry
 * MyWidget-displayName. If it is not in either location, then attempt to
 * synthesize a displayName based on the class name.</li>
 * </ol>
 * </li>
 * <li>If there is no annotation, then look for MyWidget-displayName first in
 * MyWidgetResources, and then in resources. If it is not in either location,
 * then attempt to synthesize a displayName based on the class name.</li>
 * </ol>
 * <p>
 * Likewise, for other attributes, we follow the same lookup scheme but instead
 * of MyWidget-displayName we look for MyWidget-shortDescription, and so on.
 * <p>
 * The BeanMetaData also provides access to the PropertyMetaData and
 * EventMetaData and CallbackMetaData for the JavaFX Bean. If you are not
 * interested in the full BeanMetaData but only really want a specific
 * PropertyMetaData, then you can create a PropertyMetaData directly.
 * <p>
 * For a larger discussion on the JavaFX Beans design pattern, see the package
 * documentation in javafx.beans.
 *
 * TODO need to write this larger documentation for JavaFX Beans.
 *
 * @author Richard
 */
public final class BeanMetaData<T> extends AbstractMetaData {

    /**
     * This is used in the implementation to find the images of the specified sizes
     * on disk, and in storing them and retrieving them as needed.
     */
    private enum ImageSize {
        Size_16(16), Size_32(32), Size_64(64), Size_128(128), Size_256(256), Size_512(512),
        Size_Full(Integer.MAX_VALUE);

        private final int size;

        ImageSize(int size) {
            this.size = size;
        }

        public final String getExtension() {
            return size == Integer.MAX_VALUE ? "" : size + "x" + size;
        }

        public final int getSize() {
            return size;
        }
    }

    /**
     * The list of meta-data for properties.
     */
    private Supplier<T> instanceSupplier = null;

    /**
     * The list of meta-data for properties.
     */
    private List<PropertyMetaData> properties;

//    /**
//     * The list of meta-data for static properties.
//     */
//    private List<PropertyMetaData> staticProperties;

    /**
     * A map containing the images that were discovered for this bean.
     */
    private Map<ImageSize, URL> images = new HashMap<>();

    /**
     * The class of the Builder, if any, which is associated with this JavaFX Bean.
     * If there is a builder, you should use it when constructing an instance of
     * this bean, rather than using the bean itself.
     */
    private Class<?> builderClass;

    /**
     * The property which is designated with the DefaultProperty annotation on the
     * bean. This might be null.
     */
    private PropertyMetaData defaultProperty;

    /**
     * The type of the Bean that this BeanMetaData is for.
     */
    private Class<T> type;

    private final List<QualifierMetaData> qualifiers = new ArrayList<>();

    public BeanMetaData(final Class<T> beanClass, Map<Constructor<?>, Class[]> alternativeParameters) {
        this(beanClass, alternativeParameters, false);
    }
    /**
     * Creates a new BeanMetaData instance based on the supplied class. This
     * constructor will introspect the supplied bean to discover (a) whether it is a
     * bean, and if not will throw an IllegalArgumentException; (b) whether the bean
     * has public access, and if not throw an IllegalArgumentException; (c) the
     * name, displayName, and so forth associated with this bean based on
     * annotations, resource bundles, and direct computation where appropriate ; and
     * (d) what the properties, callbacks, and events are.
     *
     * @param beanClass The class to use
     */
    public BeanMetaData(final Class<T> beanClass, Map<Constructor<?>, Class[]> alternativeParameters, boolean disableInstantiate) {
        // Step 0a: Look for and load the resource bundles associated with
        // this bean. Look for a "resources" bundle in the same
        // package as the class, and a "FooResources" bundle also
        // in the same package as the class. The FooResources takes
        // precedence over "resources" in the case of lookup
        super(new Resources(beanClass), beanClass.getSimpleName());

        if (beanClass == null)
            throw new NullPointerException("beanClass cannot be null");

        this.type = beanClass;

        // Step 0: Verify that this is a JavaBean. It must have public access.
        // It must either have a public no-arg constructor, or a
        // Builder with a public static create() method and a public
        // instance build() method.

        if (!Modifier.isPublic(beanClass.getModifiers())) {
            throw new IllegalArgumentException("The supplied bean '" + beanClass + "' does not have public access");
        }

        // Look for a Builder class.
        String builderClassName = beanClass.getName() + "Builder";
        try {
            // TODO does this bail in an unsigned context?
            builderClass = Class.forName(builderClassName);
        } catch (ClassNotFoundException ex) {
            // There is no builder, this is an OK condition.
        } catch (ClassCastException ex) {
            // The builder wasn't of the expected type
        }

        Object instance = null;
        // Verify that there is a public no-arg constructor, or that there
        // is a builder with both a public static create() method and a
        // public instance build() method.

        if (disableInstantiate) {
            instanceSupplier = () -> null;
        } else {
            try {
                instance = computeInstance(beanClass, alternativeParameters);
                final Object finalInstance = instance;
                if (instance != null) {
                    instanceSupplier = () -> {
                        return (T)finalInstance;
                        //return (T) computeInstance(beanClass, alternativeParameters);
                    };
                } else {
                    Report.error(beanClass, "Unable to create a default instance");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        if (instanceSupplier != null) {

        } else if (builderClass == null) {
//            throw new IllegalArgumentException("The supplied bean '" +
//                    beanClass + "' does not have a no-arg constructor");
        } else {
            // There is a builder, and it may be able to create the bean,
            // so we need to do a quick check to make sure it has a no-arg
            // static create() method and a no-arg instance method build().
            try {
                Method method = builderClass.getMethod("create");
                if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
                    throw new NoSuchMethodException();
                }
            } catch (NoSuchMethodException ex2) {
//                throw new IllegalArgumentException("The supplied bean '" +
//                        beanClass + "' does not have a builder with a "
//                        + "public no-arg static create() method");
            }

//            try {
//                Method method = builderClass.getMethod("build");
//                if (Modifier.isStatic(method.getModifiers()) ||
//                        !Modifier.isPublic(method.getModifiers())) {
//                    throw new NoSuchMethodException();
//                }
//            } catch (NoSuchMethodException ex2) {
//                throw new IllegalArgumentException("The supplied bean '" +
//                        beanClass + "' does not have a builder with a "
//                        + "no-arg instance build() method");
//            }
        }

        // Step 3: Find all properties, callbacks, and events. Because immutable
        // properties only have a "getter" and no property method, and
        // because we don't support properties which have a setter but
        // no getter, we use the getter as the authoritative way to
        // identify a property. If a property has a return type of
        // Callback, then we have a Callback. Otherwise If the property
        // name (as derived from the getter) starts with "on", then we
        // have an event and create an EventMetaData. Otherwise we
        // create a PropertyMetaData. While iterating, locate the
        // property which matches the DefaultProperty annotation
        // Step 2d: Lookup the DefaultProperty

        Annotation[] annotations = beanClass.getAnnotations();
        final Annotation defaultPropertyAnnotation = Stream.of(annotations)
                .filter(a -> a.annotationType().getName().equals("javafx.beans.DefaultProperty")).findFirst()
                .orElse(null);

        String defaultPropertyName = "";

        try {
            defaultPropertyName = defaultPropertyAnnotation == null ? ""
                    : (String) defaultPropertyAnnotation.annotationType().getMethod("value")
                            .invoke(defaultPropertyAnnotation);
        } catch (Exception e) {
        }

        Method[] methods = beanClass.getMethods();
        //List<PropertyMetaData> sp = new ArrayList<PropertyMetaData>();
        List<PropertyMetaData> p = new ArrayList<PropertyMetaData>();

        for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers())) {
                Type propType = Type.VALUE;
                if (!Modifier.isStatic(m.getModifiers())) {
                    final String mname = m.getName();
                    final int paramCount = m.getParameterTypes().length;
                    final Class<?> ret = m.getReturnType();
                    if (mname.startsWith("get") && paramCount == 0) {


                        Class<?> jfxCallbackCls = null;
                        try {
                            jfxCallbackCls = Class.forName("javafx.util.Callback");
                        } catch (Exception e) {}

                        if (jfxCallbackCls != null && jfxCallbackCls.isAssignableFrom(ret)) {
                            propType = Type.CALLBACK;
                        } else if (mname.startsWith("getOn")) {
                            propType = Type.EVENT;
                        }

                        PropertyMetaData prop = new PropertyMetaData(beanClass, m, propType, instance);
                        p.add(prop);
                        if (defaultProperty == null && prop.getName().equals(defaultPropertyName)) {
                            defaultProperty = prop;
                        }
                    } else if (mname.startsWith("is") && paramCount == 0
                            && (ret == boolean.class || ret == Boolean.class)) {
                        PropertyMetaData prop = new PropertyMetaData(beanClass, m, propType, instance);
                        p.add(prop);
                        if (defaultProperty == null && prop.getName().equals(defaultPropertyName)) {
                            defaultProperty = prop;
                        }
                    }
                } else {
                    final String mname = m.getName();
                    final int paramCount = m.getParameterTypes().length;
                    final Class<?> ret = m.getReturnType();
                    if (mname.startsWith("get") && paramCount == 1) {
                        PropertyMetaData prop = new PropertyMetaData(beanClass, m, propType, instance);
                        p.add(prop);
                    } else if (mname.startsWith("is") && paramCount == 1
                            && (ret == boolean.class || ret == Boolean.class)) {
                        PropertyMetaData prop = new PropertyMetaData(beanClass, m, propType, instance);
                        p.add(prop);
                    }
                }
            }

        }

        //staticProperties = Collections.unmodifiableList(sp);
        properties = Collections.unmodifiableList(p);

        // Step 4: Find all other methods. These methods may be useful for
        // various event handlers. As such it is useful to locate these
        // methods. In all cases (for properties, events, and methods)
        // we only find those API members which are public, such that
        // we are not circumventing any security protocol.

        // Step 5: Find and load possible qualifiers
        String qualifiersValue = getBundleValue(beanClass, BundleValues.QUALIFIERS, null);
        if (qualifiersValue != null && !qualifiersValue.isBlank()) {
            Arrays.stream(qualifiersValue.split(","))
                .map(String::trim)
                .forEach(s -> qualifiers.add(new QualifierMetaData(beanClass, bundle, s)));
        }
    }

    /**
     * Gets a reference to an unmodifiable list of PropertyMetaData, one for each
     * public property defined on the bean.
     *
     * @return an unmodifiable List of properties
     */
    public final List<PropertyMetaData> getProperties() {
        return properties;
    }

    public final List<QualifierMetaData> getQualifiers() {
        return qualifiers;
    }

    /**
     * Gets the class of the Builder, if any, which is associated with this JavaFX
     * Bean. If there is a builder, you should use it when constructing an instance
     * of this bean, rather than using the bean itself.
     *
     * @return The class of the Builder for this bean, or null if there isn't one
     */
    public final Class<?> getBuilder() {
        return builderClass;
    }

    /**
     * Looks for the next-closest image to the one requested in this BeanMetaData.
     * The returned image may not match the given dimensions, so the caller may want
     * to ensure the requested dimensions are met by specifying them on the
     * ImageView which will display the returned Image. If there is no Image equal
     * to or larger than the requested dimensions, then the next closest
     * <em>smaller</em> sized image will be returned. If there simply is no image
     * available, then null is returned.
     *
     * @param width  The requested width of the image to look up
     * @param height The requested height of the image to look up
     * @return An image that most correctly matches the given width and height.
     *         First any larger image is found if an exact match cannot be, and then
     *         any smaller image is found. Null is ultimately returned if no image
     *         exists.
     */
    public final URL findImage(int width, int height) {
        // We might as well just get right to it. If there are no images,
        // then null is always returned.
        if (images.isEmpty())
            return null;

        // Look for the image associated with a specific size which
        // is greater than or equal to the requested width and height.
        // We simply iterate over all ImageSize values and check the
        // map for any value that is greater than or equal to the
        // requested width and height.
        final ImageSize[] imageSizes = ImageSize.values();
        for (ImageSize imageSize : imageSizes) {
            final int size = imageSize.getSize();
            if (size >= width && size >= height) {
                // We found the best match, so just return it
                URL image = images.get(imageSize);
                if (image != null)
                    return image;
            }
        }

        // Well, we didn't find an image bigger than the requested size, so we
        // now have to find the next closest smaller one.
        for (int i = imageSizes.length - 1; i >= 0; i--) {
            final ImageSize imageSize = imageSizes[i];
            final int size = imageSize.getSize();
            if (size <= width && size <= height) {
                // We found the best match, so just return it
                URL image = images.get(imageSize);
                if (image != null)
                    return image;
            }
        }

        throw new AssertionError("This code should be unreachable");
    }

//    /**
//     * Finds the PropertyMetaData matching the property with the given name.
//     *
//     * @param name The name of the property to find. Cannot be null.
//     * @return The property of the given name, or null if there isn't one.
//     */
//    public final PropertyMetaData findProperty(String name) {
//        if (name == null)
//            throw new NullPointerException("name cannot be null");
//        for (PropertyMetaData md : getProperties()) {
//            if (md.getName().equals(name)) {
//                return md;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Finds the PropertyMetaData matching the static property with the given name.
//     *
//     * @param name The name of the static property to find. Cannot be null.
//     * @return The static property of the given name, or null if there isn't one.
//     */
//    public final PropertyMetaData findStaticProperty(String name) {
//        if (name == null)
//            throw new NullPointerException("name cannot be null");
//        for (PropertyMetaData md : getProperties()) {
//            if (md.isStatic() && md.getName().equals(name)) {
//                return md;
//            }
//        }
//        return null;
//    }

    /**
     * Gets the PropertyMetaData corresponding to the property which was identified
     * via the DefaultProperty annotation used on the JavaFX Bean. If no
     * DefaultProperty annotation was used or the value identified a non-existent
     * property, then null is returned.
     *
     * @return The PropertyMetaData of the default property, or null if there isn't
     *         one.
     */
    public final PropertyMetaData getDefaultProperty() {
        return defaultProperty;
    }

    /**
     * Gets the class type for which this BeanMetaData represents.
     *
     * @return A non-null reference to the bean type
     */
    public final Class<T> getType() {
        return type;
    }

    public final boolean isAbstract() {
        return Modifier.isAbstract(type.getModifiers());
    }

    public String getDisplayName() {
        return getBundleValue(this.type, BundleValues.DISPLAY_NAME, toDisplayName(getName()));
    }

    public String getDescription() {
        return getBundleValue(this.type, BundleValues.DESCRIPTION, null);
    }

    public String getCategory() {
        return getBundleValue(this.type, BundleValues.CATEGORY, AbstractMetaData.HIDDEN);
    }

    public boolean isResizeNeededWhenTopElement() {
        return Boolean.parseBoolean(getBundleValue(this.type, BundleValues.RESIZE_WHEN_TOP_ELEMENT, "false"));
    }

    public String getVersion() {
        return getBundleValue(this.type, BundleValues.VERSION, "");
    }



    public T getDefaultInstance() {
        if (instanceSupplier != null) {
            return instanceSupplier.get();
        }
        return null;
    }

    private static Object computeInstance(Class<?> c, Map<Constructor<?>, Class[]> alternativeParameters) {

        final Class<?> instanciableClass;

        if (c.isPrimitive()) {
            return ReflectionUtils.getPrimitiveDefaultValue(c);
        } else if (c.isInterface()) {
            // if interface a simple null implementation of methods is sufficient
            return Enhancer.create(c, new MethodInterceptor() {

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    return null;
                }
            });
        } else if (Modifier.isAbstract(c.getModifiers())) {
            // An abstract class may contain code in constructor so we need to relay the proxy instance methods to super calls

            try {
                return Enhancer.create(c, new MethodInterceptor() {

                    @Override
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                            throws Throwable {

                        try {
                            return proxy.invokeSuper(obj, args);
                        } catch (Exception e) {}
                        return null;
                    }
                });
            } catch (Exception e) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(c);
                enhancer.setCallbackType(NoOp.class);
                instanciableClass = enhancer.createClass();
            }

        } else {
            instanciableClass = c;
        }

        Set<Constructor<?>> constructors = new TreeSet<>(Comparator
                .comparing((Constructor<?> cont) -> cont.getParameterCount()).thenComparing(cont -> cont.hashCode()));
        constructors.addAll(Arrays.asList(instanciableClass.getConstructors()));

        for (Constructor<?> constructor : constructors) {

            try {
                Class<?>[] parameters = alternativeParameters != null && alternativeParameters.containsKey(constructor)
                        ? alternativeParameters.get(constructor)
                        : constructor.getParameterTypes();

                Object[] args = Stream.of(parameters).map(pt -> {
                    try {
                        return computeInstance(pt, alternativeParameters);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList()).toArray(new Object[0]);

                return constructor.newInstance(args);
            } catch (Exception e1) {
                String args = Stream.of(constructor.getParameterTypes()).map(Class::getName).collect(Collectors.joining(","));
                Report.warn(c, "Failed to instanciate using constructor(" + args + ")", e1);
            }
        }

        // still no instance try to find some defaults as public static variable
        Object obj = ReflectionUtils.findStaticMemberValueByType(instanciableClass, instanciableClass);
        if (obj != null) {
            return obj;
        }

        if (constructors.isEmpty()) { // no public constructor so create one
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(c);
            enhancer.setCallback(new NoOp() {
            });
            enhancer.setCallbackType(NoOp.class);
            return enhancer.create(new Class[] {}, new Object[] {});
        }

        return null;
    }

}
