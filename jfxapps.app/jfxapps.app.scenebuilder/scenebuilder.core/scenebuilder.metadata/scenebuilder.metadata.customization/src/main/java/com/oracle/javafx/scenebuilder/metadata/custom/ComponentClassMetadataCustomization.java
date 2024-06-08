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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.Node;

/**
 * This class describes an fxml component class
 *
 */
public class ComponentClassMetadataCustomization {

    public static final Comparator<ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ?>> COMPARATOR = Comparator
            .<ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ?>, Integer>comparing(
                    p -> p.getCustomization().getOrder())
            .thenComparing(p -> p.getName().getName());

    /** The component properties component subset. */
    private final Map<String, Qualifier> qualifiers = new HashMap<>();

    /** The free child positioning flag. default false */
    private final Map<ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>>, Boolean> freeChildPositioning = new HashMap<>();

    /** true if the component deserves a resizing using predefined size when set as root element of the document. default: true */
    private boolean resizeNeededWhenTopElement = true;

    /** property used for object description */
    private PropertyName descriptionProperty = null;

    /** if set operate a custom mutation on the original string */
    private LabelMutation labelMutation = null;

    /** if set operate a custom mutation on the original string */
    private final Map<ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>>, ChildLabelMutation> childLabelMutations = new HashMap<>();

    private SbComponentClassMetadata<?> owner;


    public ComponentClassMetadataCustomization() {
        super();
    }

    public void setOwner(SbComponentClassMetadata<?> owner) {
        this.owner = owner;
    }

    /**
     * Gets the component's qualifiers (aka default setups).
     *
     * @return the properties
     */
    public Map<String, Qualifier> getQualifiers() {
        return qualifiers;
    }

    /**
     * Checks if is child positioning is free or constrained.
     *
     * @return true, if is free child positioning
     */
    public boolean isFreeChildPositioning(ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ? extends SbComponentClassMetadata<?>> componentProperty) {
        var current = owner;

        while (current != null && !current.getCustomization().freeChildPositioning.containsKey(componentProperty)) {
            current = owner.getParentMetadata();
        }

        if (current == null) {
            return false;
        }

        Boolean free = current.getCustomization().freeChildPositioning.get(componentProperty);
        return free == null ? false : free;
    }



    protected void setDescriptionProperty(ValuePropertyMetadata<ValuePropertyMetadataCustomization> promptTextPropertyMetadata) {
        this.descriptionProperty = promptTextPropertyMetadata.getName();
    }

    /**
     * Get the property name used for the description
     * A lookup is done on parents metadata
     *
     * @return the property name or null if none found
     */
    public PropertyName getDescriptionProperty() {
        var current = owner;

        while (current != null && current.getCustomization().descriptionProperty == null ) {
            current = current.getParentMetadata();
        }

        if (current == null) {
            return null;
        }

        return current.getCustomization().descriptionProperty;
    }

    /*
     * Object
     */

    @Override
    public int hashCode() {
        return super.hashCode(); // Only to please FindBugs
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); // Only to please FindBugs
    }


    /**
     * Find the applicable qualifiers in the available qualifiers.
     * A {@link ComponentClassMetadataCustomization.Qualifier} is applicable if the {@link ApplicabilityCheck} provided trough
     * {@link Qualifier#Qualifier(URL, String, String, URL, URL, String, ApplicabilityCheck)} return true
     * If none were provided during the {@link Qualifier} instantiation then the {@link Qualifier} is always applicable
     *
     * @param sceneGraphObject the scene graph object
     * @return the applicable qualifiers sets
     */
    public Set<Qualifier> applicableQualifiers(Object sceneGraphObject) {
        if (!owner.getKlass().isAssignableFrom(sceneGraphObject.getClass()) || getQualifiers().size() == 0) {
            return Collections.unmodifiableSet(new HashSet<>());
        }
        return Collections.unmodifiableSet(getQualifiers().values().stream().filter(q -> q.isApplicable(sceneGraphObject)).collect(Collectors.toSet()));
    }

    public static class Qualifier {

        public static final Qualifier UNKNOWN = new Qualifier(null, null, null, null, null, null);

        public static final String HIDDEN = null;
        public static final String DEFAULT = "";
        public static final String EMPTY = "empty";

        private final URL fxmlUrl;
        private final String label;
        private final String description;
        private final URL iconUrl;
        private final URL iconX2Url;
        private final String category;
        private final ApplicabilityCheck applicabilityCheck;

        public Qualifier(URL fxmlUrl, String label, String description, URL iconUrl, URL iconX2Url, String category) {
            this(fxmlUrl, label, description, iconUrl, iconX2Url, category, (o) -> true);
        }

        public Qualifier(URL fxmlUrl, String label, String description, URL iconUrl, URL iconX2Url, String category, ApplicabilityCheck<?> applicabilityCheck) {
            super();
            this.fxmlUrl = fxmlUrl;
            this.label = label;
            this.description = description;
            this.iconUrl = iconUrl != null ? iconUrl : getClass().getResource("MissingIcon.png");
            this.iconX2Url = iconX2Url != null ? iconX2Url : getClass().getResource("MissingIcon@2x.png");
            this.category = category != null ? category : "Custom";
            this.applicabilityCheck = applicabilityCheck == null ? (o) -> true : applicabilityCheck;
        }

        @SuppressWarnings("unchecked")
        public boolean isApplicable(Object object) {
            return applicabilityCheck.isApplicable(object);
        }

        public URL getFxmlUrl() {
            return fxmlUrl;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }

        public URL getIconUrl() {
            return iconUrl;
        }

        public URL getIconX2Url() {
            return iconX2Url;
        }

        public String getCategory() {
            return category;
        }

        public ApplicabilityCheck getApplicabilityCheck() {
            return applicabilityCheck;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private URL fxmlUrl;
            private String label;
            private String description;
            private URL iconUrl;
            private URL iconX2Url;
            private String category;
            private ApplicabilityCheck<?> applicabilityCheck;

            public Builder fxmlUrl(URL fxmlUrl) {
                this.fxmlUrl = fxmlUrl;
                return this;
            }

            public Builder label(String label) {
                this.label = label;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder iconUrl(URL iconUrl) {
                this.iconUrl = iconUrl;
                return this;
            }

            public Builder iconX2Url(URL iconX2Url) {
                this.iconX2Url = iconX2Url;
                return this;
            }

            public Builder category(String category) {
                this.category = category;
                return this;
            }

            public Builder applicabilityCheck(ApplicabilityCheck<?> applicabilityCheck) {
                this.applicabilityCheck = applicabilityCheck;
                return this;
            }

            public Qualifier build() {
                return new Qualifier(fxmlUrl, label, description, iconUrl, iconX2Url, category, applicabilityCheck);
            }
        }
    }

    @FunctionalInterface
    public interface ApplicabilityCheck<T> {
        boolean isApplicable(T object);
    }

    @FunctionalInterface
    public interface LabelMutation {
        String mutate(String originalLabel, Object object);
    }

    @FunctionalInterface
    public interface ChildLabelMutation {
        String mutate(String originalLabel, Object object, Node child);
    }

    public boolean isResizeNeededWhenTopElement() {
        return resizeNeededWhenTopElement;
    }

    protected ComponentClassMetadataCustomization setResizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
        this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
        return this;
    }

    public LabelMutation getLabelMutation() {
        return labelMutation;
    }

    protected ComponentClassMetadataCustomization setLabelMutation(LabelMutation labelMutation) {
        this.labelMutation = labelMutation;
        return this;
    }

    public ChildLabelMutation getChildLabelMutations(ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>> cmp) {
        return childLabelMutations.get(cmp);
    }

    protected ComponentClassMetadataCustomization setChildLabelMutation(ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>> cmp, ChildLabelMutation labelMutation) {
        assert owner.getAllSubComponentProperties().contains(cmp);
        childLabelMutations.put(cmp, labelMutation);
        return this;
    }

    public void setFreeChildPositioning(ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, SbComponentClassMetadata<?>> componentProperty, boolean freeChildPositioning) {
        this.freeChildPositioning.put(componentProperty, freeChildPositioning);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Qualifier> qualifiers = new HashMap<>();
        private boolean resizeNeededWhenTopElement = true;
        private LabelMutation labelMutation = null;

        public Builder qualifier(String key, Qualifier qualifier) {
            qualifiers.put(key, qualifier);
            return this;
        }

        public Builder resizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
            this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
            return this;
        }

        public Builder labelMutation(LabelMutation labelMutation) {
            this.labelMutation = labelMutation;
            return this;
        }

        public ComponentClassMetadataCustomization build() {
            ComponentClassMetadataCustomization customization = new ComponentClassMetadataCustomization();
            qualifiers.forEach(customization.qualifiers::put);
            customization.setResizeNeededWhenTopElement(resizeNeededWhenTopElement);
            customization.setLabelMutation(labelMutation);
            return customization;
        }
    }
}
