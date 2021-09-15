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
package com.oracle.javafx.scenebuilder.core.metadata.property;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/**
 * This class describes a property used as a placeholder for other component 
 * 
 */
public class ComponentPropertyMetadata extends PropertyMetadata {
    
    /** The class metadata of the owner component. */
    private final ComponentClassMetadata<?> classMetadata;
    
    /** Does this placeholder accept a collection of components. */
    private final boolean collection;

    /** Icon illustrating this placeholder. */
    private final URL iconUrl;
    
    /** Icon illustrating this placeholder. double sized */
    private final URL iconX2Url;
    
    /** true if the component deserves a resizing while used as top element of the layout. default: false */
    private boolean resizeNeededWhenTopElement = false;
    
    /** true if the component deserves a resizing while used as top element of the layout. default: false */
    private boolean main = false;
    
    
    /** The requested order. component properties are ordered by "order" then by name */
    private int order;
    
    private Set<PropertyName> disabledChildProperties = new HashSet<>();
    
    
    /**
     * Instantiates a new component property metadata.
     *
     * @param name the property name
     * @param classMetadata the owner component metadata
     * @param collection true if it accepts a collection of components or only one
     * @param iconUrl
     * @param iconX2Url
     * @param main true if it is the main placeholder
     */
    public ComponentPropertyMetadata(PropertyName name, ComponentClassMetadata<?> classMetadata, boolean collection, URL iconUrl, URL iconX2Url, boolean main, int order) {
        super(name, false);
        this.classMetadata = classMetadata;
        this.collection = collection;
        this.iconUrl = iconUrl != null ? iconUrl : getClass().getResource("MissingIcon.png");
        this.iconX2Url = iconX2Url != null ? iconX2Url : getClass().getResource("MissingIcon@2x.png");
        this.main = main;
        this.order = order;
    }
    
    public ComponentPropertyMetadata(PropertyName name, ComponentClassMetadata<?> classMetadata, boolean collection, URL iconUrl, URL iconX2Url, boolean main) {
        this(name, classMetadata, collection, iconUrl, iconX2Url, main, 0);
    }
    
    public ComponentPropertyMetadata(PropertyName name, ComponentClassMetadata<?> classMetadata, boolean collection, URL iconUrl, URL iconX2Url, int order) {
        this(name, classMetadata, collection, iconUrl, iconX2Url, false, order);
    }
    
    /**
     * Instantiates a new component property metadata.
     *
     * @param name the property name
     * @param classMetadata the children components metadata accepted by this component property
     * @param collection true if it accepts a collection of components or only one
     * @param iconUrl the icon url
     * @param iconX2Url the icon X 2 url
     */
    public ComponentPropertyMetadata(PropertyName name, ComponentClassMetadata<?> classMetadata, boolean collection, URL iconUrl, URL iconX2Url) {
        this(name, classMetadata, collection, iconUrl, iconX2Url, false);
    }

    /**
     * Gets the the owner component metadata.
     *
     * @return the owner component metadata
     */
    public ComponentClassMetadata<?> getClassMetadata() {
        return classMetadata;
    }

    /**
     * Checks if if it accepts a collection of components or only one.
     *
     * @return true, if is accepts a collection
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    public URL getIconUrl() {
        return iconUrl;
    }
    
    /**
     * Gets the icon double sized.
     *
     * @return the icon
     */
    public URL getIconX2Url() {
        return iconX2Url;
    }

    @Override
    public int hashCode() { // To please FindBugs
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) { // To please FindBugs
        if (obj == null) {
            return false;
        }
        if (PropertyMetadata.class != obj.getClass()) {
            return false;
        }
        
        return super.equals(obj);
    }

    public boolean isResizeNeededWhenTopElement() {
        return resizeNeededWhenTopElement;
    }

    public ComponentPropertyMetadata setResizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
        this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
        return this;
    }
    public boolean isMain() {
        return main;
    }
   
    public ComponentPropertyMetadata disableChildProperty(PropertyName propertyName) {
        if (!disabledChildProperties.contains(propertyName)) {
            disabledChildProperties.add(propertyName);
        }
        return this;
    }
    
    public boolean isChildPropertyDisabled(PropertyName propertyName) {
        return disabledChildProperties.contains(propertyName);
    }
    
    public Set<PropertyName> getDisabledProperties() {
        return disabledChildProperties;
    }

    public int getOrder() {
        return order;
    }
    
    
}
