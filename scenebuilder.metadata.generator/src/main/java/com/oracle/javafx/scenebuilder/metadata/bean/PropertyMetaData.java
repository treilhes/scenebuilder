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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Optional;

import com.oracle.javafx.scenebuilder.metadata.util.ReflectionUtils;
import com.oracle.javafx.scenebuilder.metadata.util.Report;
import com.oracle.javafx.scenebuilder.metadata.util.Resources;

/**
 * Represents the meta-data for a JavaFX Bean property. A PropertyMetaData is
 * created by invoking one of the constructors. These constructors reflect on a
 * Class representing a JavaFX Bean and the Method that represents the public
 * instance getter and extracts and/or generates the meta-data for that
 * property. Some information, such as the actual name of the property, are
 * determined reflectively and cannot be customized. Most information however
 * can be customized through the use of one or more
 * {@link java.util.ResourceBundle}s.
 * <p>
 * During the reflective process, this class will look first for a resource
 * bundle named after the bean, and then for a generic "resources"
 * ResourceBundle located in the same package as the bean, the same as with the
 * BeanMetaData. When looking up the displayName of a property in the specific
 * "[MyBean]Resources" bundle, we will look for "[propertyName]-displayName",
 * and when looking up a value in the package level "resources" bundle, we will
 * look for "[MyBean].[propertyName]-displayName".
 * <p>
 * Likewise, for other attributes, we follow the same lookup scheme but instead
 * of MyBean-displayName we look for MyBean-shortDescription, and so on.
 * <p>
 * From the PropertyMetaData it is possible to get a reference to the getter,
 * setter, and property methods of the property. If a property is readonly, the
 * setter will be null. If a property is immutable, the setter and the property
 * method will both be null. The getter is never null, as only those properties
 * that have a public getter can have a PropertyMetaData created for them.
 *
 * @author Richard
 */
public class PropertyMetaData extends AbstractMetaData {
    // TODO need to add the ability to reference a custom PropertyEditor!

    /**
     * Defines the mutability of a property, which can either be immutable,
     * read-only, or writable.
     */
    public enum Mutability {
        IMMUTABLE, READ_ONLY, WRITABLE
    }

    public enum Type {
        EVENT, CALLBACK, VALUE
    }

    /**
     * Define is the property is applied staticaly on other bean.
     */
    private boolean isStatic;

    /**
     * A reference to the getter for this property. This must not be null.
     */
    private Method getter;

    /**
     * A reference to the setter for this property. If this is set, then there is a
     * public setter, otherwise it is null. If null, the property is considered
     * read-only.
     */
    private Method setter;

    /**
     * A reference to the property method for this property. This should never be
     * null when setter is not null, and vice versa. The property method will return
     * a property (either ReadOnlyProperty or Property) object when invoked of the
     * appropriate type.
     */
    private Method property;

    /**
     * The reflectively determined type of the property. This is essentially the
     * return type of the getter method.
     */
    private Class<?> type;

    /**
     * The mutability of the property.
     */
    private Mutability mutability;

    /**
     * The reflectively determined type of the object on which the property is
     * applicable. This is essentially the bean class but for static getter methods
     * it is the type of the only parameter.
     */
    private Class<?> applicability;

    /**
     * A default beanClass instance. May be null
     */
    private Object instance;

    /**
     * Boolean true if the property return type is a collection
     */
    private boolean collection;

    /**
     * if the property return type is a collection then indicate if the collection
     * mutability
     */
    private Mutability collectionMutability = null;

    private Class<?> collectionType;

    private Type propertyType;

    private Class<?> beanClass;

    private Object defaultValue;

    /**
     * Package private constructor which reuses the bundle and omits some checks for
     * the sake of performance efficiency.
     *
     * @param beanClass The bean class, cannot be null
     * @param getter    The getter, cannot be null
     * @param instance
     * @param bundle    The bundle, cannot be null
     */
    PropertyMetaData(Class<?> beanClass, Method getter, Type type, Object instance) {
        super(new Resources(beanClass), extractName(getter));
        this.instance = instance;
        this.beanClass = beanClass;
        this.propertyType = type;

        // If either the bean class or getter are null, throw an exception
        if (beanClass == null || getter == null) {
            throw new NullPointerException("Both the beanClass and getter cannot be null");
        }

        init(beanClass, getter);
    }

    private static String extractName(Method getter) {
        if (getter == null)
            return null;
        final String getterName = getter.getName();
        final String capitalizedName = getterName.startsWith("get") ? getterName.substring(3) : getterName.substring(2);
        return decapitalize(capitalizedName);
    }

    /**
     * Initializes the class, called only from one of the above constructors.
     *
     * @param beanClass The bean class, cannot be null
     * @param getter    The getter, cannot be null
     * @param bundle    The bundle, cannot be null
     */
    private void init(Class<?> beanClass, Method getter) {
        // Step 0: Discover the type of applicability
        if (Modifier.isStatic(getter.getModifiers())) {
            isStatic = true;
            applicability = getter.getParameters()[0].getType();
        } else {
            isStatic = false;
            applicability = beanClass;
        }

        // Step 2: Discover the type of the property. This is simply the return
        // type of the getter method.
        type = getter.getReturnType();

        final String getterName = getter.getName();
        String capitalizedName = getterName.startsWith("get") ? getterName.substring(3) : getterName.substring(2);

        // Step 4: Discover the setter and property methods.
        this.getter = getter;
        final String setterName = "set" + capitalizedName;
        try {
            if (!isStatic) {
                this.setter = beanClass.getMethod(setterName, type);
                int mods = this.setter.getModifiers();
                // TODO I couldn't figure out how to use Void.class to ensure
                // that the setter has a void return type. So right now I will
                // accept as the setter methods that don't have a void return type.
                if (Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                    this.setter = null;
                }
            } else {
                this.setter = beanClass.getMethod(setterName, this.applicability, type);
                int mods = this.setter.getModifiers();
                if (!Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                    this.setter = null;
                }
            }

        } catch (NoSuchMethodException ex) {
        }

        String propertyName = getName() + "Property";
        try {
            try {
                this.property = beanClass.getMethod(propertyName);
            } catch (NoSuchMethodException e) {
                propertyName = capitalizedName + "Property";
                this.property = beanClass.getMethod(propertyName);
                setName(capitalizedName);
            }
            int mods = this.property.getModifiers();
            if (Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                this.setter = null;
            } else {
                // Check the return type to make sure it is assignable from
                // the ReadOnlyPropertyClass, or Property class, as
                // appropriate.
                Class<?> returnType = this.property.getReturnType();

                try {
                    Class<?> readOnlyProperty = Class.forName("javafx.beans.property.ReadOnlyProperty");
                    Class<?> writableProperty = Class.forName("javafx.beans.property.Property");

//                if (!readOnlyProperty.isAssignableFrom(returnType) || this.setter != null) {
//
//                	if (!writableProperty.isAssignableFrom(returnType) || this.setter == null) {
//                        this.property = null;
//                    }
//                }
                    if (readOnlyProperty != null && readOnlyProperty.isAssignableFrom(returnType)
                            && this.setter == null) {
                        this.property = null;
                    }
                } catch (ClassNotFoundException e) {
                    Report.warn(beanClass,
                            "Unable to load javafx.beans.property.ReadOnlyProperty, javafx.beans.property.Property for detecting mutability of "
                                    + this.property.getName(),
                            e);
                }

            }
        } catch (NoSuchMethodException ex) {
        }

        // Step 5: Establish mutability
        if (property == null && !isStatic) {
            mutability = Mutability.IMMUTABLE;
        } else if (setter == null) {
            mutability = Mutability.READ_ONLY;
        } else {
            mutability = Mutability.WRITABLE;
        }

        collection = getType().isArray() || Collection.class.isAssignableFrom(getType());

        if (collection) {

            Object collectionInstance = null;

            try {
                collectionInstance = instance == null ? null : getter.invoke(instance);
            } catch (Exception e) {
            }

            if (collection && collectionInstance == null) {
                collectionMutability = Mutability.READ_ONLY; // we don't know so
            } else {
                collectionMutability = Mutability.WRITABLE;
                Class<?> instanceClass = collectionInstance.getClass();
                while (instanceClass != null) {
                    if (instanceClass.getSimpleName().toLowerCase().contains("unmodifiable")) {
                        collectionMutability = Mutability.READ_ONLY;
                        break;
                    }
                    instanceClass = instanceClass.getSuperclass();
                }
            }

            try {
                if (getType().isArray()) {
                    collectionType = getType().getComponentType();
                } else {
                    collectionType = ReflectionUtils.findGenericTypes(beanClass, getName()).get(0);
                }

            } catch (Exception e) {
            }
        }

        try {
            if (isStatic()) {
                defaultValue = getter.invoke(null, instance);
            } else {
                defaultValue = getter.invoke(instance);
            }

        } catch (Exception e1) {}
    }

    /**
     * Gets the data type for this property. This is simply the return type of the
     * getter method for the property, and should match the setter type and the type
     * housed within the property returned by the property method.
     *
     * @return the data type for this property
     */
    public final Class<?> getType() {
        String btype = getBundleValue(beanClass, BundleValues.COLLECTION_TYPE, null);

        if (btype == null) {
            return type;
        }

        try {
            type = Class.forName(btype);
        } catch (ClassNotFoundException e) {
            Report.error(beanClass,
                    String.format("Unable to load type for property '%s' the metadata class : %s", getName(), btype));
        }

        return type;
    }

    /**
     * Gets the getter method for this property. This will never be null.
     *
     * @return The getter
     */
    public final Method getGetterMethod() {
        return getter;
    }

    /**
     * Gets the setter method for this property. If there is not a publicly visible
     * instance setter, then this method will return null.
     *
     * @return The setter method, or null
     */
    public final Method getSetterMethod() {
        return setter;
    }

    /**
     * Gets the property method for this property. This is the method which returns
     * the Property object for the property. If the property is immutable, this
     * method will return null. Otherwise, this will return a method.
     *
     * @return The property method for this property, or null if the property is
     *         immutable.
     */
    public final Method getPropertyMethod() {
        return property;
    }

    /**
     * Gets the mutability of this property, which can either be IMMUTABLE,
     * READ_ONLY, or WRITABLE.
     *
     * @return The mutability of this property. This is never null.
     */
    public final Mutability getMutability() {
        return mutability;
    }


    public Object getDefaultValue() {
        return isReadWrite() && propertyType != Type.EVENT ? defaultValue : null;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Class<?> getApplicability() {
        return applicability;
    }

    public boolean isCollection() {
        String isCollection = getBundleValue(beanClass, BundleValues.IS_COLLECTION, null);

        if (isCollection == null) {
            return collection;
        }

        collection = Boolean.parseBoolean(isCollection);

        return collection;
    }

    public Mutability getCollectionMutability() {
        return collectionMutability;
    }

    public Class<?> getCollectionType() {
        String type = getBundleValue(beanClass, BundleValues.COLLECTION_TYPE, null);

        if (type == null) {
            return collectionType;
        }

        try {
            collectionType = Class.forName(type);
        } catch (ClassNotFoundException e) {
            Report.error(beanClass,
                    String.format("Unable to load collectionType for property '%s' the metadata class : %s", getName(), type));
        }
        return collectionType;
    }

    public Class<?> getContentType() {
        return isCollection() ? getCollectionType() : getType();
    }

    public boolean isReadWrite() {
        return getMutability() == Mutability.WRITABLE
                || (isCollection() && getCollectionMutability() == Mutability.WRITABLE);
    }

    public boolean isLocal() {
        return getter.getDeclaringClass().equals(beanClass);
    }

    public Class<?> getResidenceClass() {
        return getter.getDeclaringClass();
    }

    public Type getPropertyType() {
        return propertyType;
    }

    public String getDisplayName() {
        return getBundleValue(beanClass, BundleValues.DISPLAY_NAME, toDisplayName(getName()));
    }

    public String getChildLabelMutation() {
        return getBundleValue(beanClass, BundleValues.CHILD_LABEL_MUTATION_LAMBDA, null);
    }

    public String getCategory() {
        return getBundleValue(beanClass, BundleValues.CATEGORY, AbstractMetaData.HIDDEN);
    }

    public String getSection() {
        return getBundleValue(beanClass, BundleValues.INSPECTOR_SECTION, null);
    }

    public String getSubSection() {
        return getBundleValue(beanClass, BundleValues.INSPECTOR_SUBSECTION, null);
    }

    public int getOrder() {
        return Integer.parseInt(getBundleValue(beanClass, BundleValues.ORDER, "-1"));
    }

    public String getImage() {
        return getBundleValue(beanClass, BundleValues.IMAGE, null);
    }

    public String getImageX2() {
        return getBundleValue(beanClass, BundleValues.IMAGE_X2, null);
    }

    public String getNullEquivalent() {
        return getBundleValue(beanClass, BundleValues.NULL_EQUIVALENT, null);
    }
    public Class<?> getMetadataClass() {
        String cls = getBundleValue(beanClass, BundleValues.METACLASS, null);
        try {
            return Class.forName(cls);
        } catch (Exception e) {
            Report.error(beanClass,
                    String.format("Unable to load for property '%s' the metadata class : %s", getName(), cls));
            return null;
        }
    }

    @Deprecated
    public String getKind() {
        return getBundleValue(beanClass, BundleValues.TMP_METACLASS_KIND, null);
    }

    public boolean isFreeChildPositioning() {
        return Boolean.parseBoolean(getBundleValue(beanClass, BundleValues.FREE_POSITIONING, "false"));
    }

    public boolean isHidden() {
        return Boolean.parseBoolean(getBundleValue(beanClass, BundleValues.HIDDEN, "false"));
    }

    public Optional<Boolean> isComponent() {
        String isComponent = getBundleValue(beanClass, BundleValues.IS_COMPONENT, null);
        if (isComponent == null) {
            return Optional.empty();
        }
        return Optional.of(Boolean.parseBoolean(isComponent));
    }

    @Override
    public String getBundleValue(Class<?> beanClass, String name, String defaultValue) {
        if (isStatic) {
            return super.getBundleValue(beanClass, "static", name, defaultValue);
        } else {
            return super.getBundleValue(beanClass, name, defaultValue);
        }

    }

    @Override
    public void setBundleValue(Class<?> beanClass, String name, String value) {
        if (isStatic) {
            super.setBundleValue(beanClass, "static", name, value);
        } else {
            super.setBundleValue(beanClass, name, value);
        }
    }

    public static PropertyMetaData relocalize(PropertyMetaData property, BeanMetaData<?> targetBean) {
        return new PropertyMetaData(targetBean.getType(), property.getGetterMethod(), property.getPropertyType(),
                targetBean.getDefaultInstance());
    }

    public static PropertyMetaData relocalizeStatic(PropertyMetaData property, BeanMetaData<?> targetBean) {

        if (!property.isStatic()) {
            throw new RuntimeException("Only static property can use relocalizeStatic() function");
        }

        return new PropertyMetaData(property.getResidenceClass(), property.getGetterMethod(), property.getPropertyType(),
                targetBean.getDefaultInstance());
    }
}
