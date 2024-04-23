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
package com.oracle.javafx.scenebuilder.metadata.custom;

import java.net.URL;

import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;

/**
 * This class describes a property used as a placeholder for other component
 *
 */
public class ComponentPropertyMetadataCustomization {

    /** Icon illustrating this placeholder. */
    private final URL iconUrl;

    /** Icon illustrating this placeholder. double sized */
    private final URL iconX2Url;

    /** true if the component deserves a resizing while used as top element of the layout. default: false */
    private boolean resizeNeededWhenTopElement = false;

    /** The requested order. component properties are ordered by "order" then by name */
    private int order;


    protected ComponentPropertyMetadataCustomization(Builder builder) {
        this.iconUrl = builder.iconUrl;
        this.iconX2Url = builder.iconX2Url;
        this.order = builder.order;
        this.resizeNeededWhenTopElement = builder.resizeNeededWhenTopElement;
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

    public ComponentPropertyMetadataCustomization setResizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
        this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public static class Builder {

        /** Icon illustrating this placeholder. */
        protected URL iconUrl = getClass().getResource("MissingIcon.png");

        /** Icon illustrating this placeholder. double sized */
        protected URL iconX2Url = getClass().getResource("MissingIcon.png");

        /** true if the component deserves a resizing while used as top element of the layout. default: false */
        protected boolean resizeNeededWhenTopElement = false;

        /** The requested order. component properties are ordered by "order" then by name */
        protected int order;

        public Builder() {
            super();
        }

        protected Builder iconUrl(URL iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        protected Builder iconX2Url(URL iconX2Url) {
            this.iconX2Url = iconX2Url;
            return this;
        }

        protected Builder order(int order) {
            this.order = order;
            return this;
        }

        protected Builder resizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
            this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
            return this;
        }

        public ComponentPropertyMetadataCustomization build() {
            return new ComponentPropertyMetadataCustomization(this);
        }
    }

}
