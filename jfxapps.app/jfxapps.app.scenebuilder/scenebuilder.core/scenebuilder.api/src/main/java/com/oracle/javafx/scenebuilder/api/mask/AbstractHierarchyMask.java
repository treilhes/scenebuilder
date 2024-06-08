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
package com.oracle.javafx.scenebuilder.api.mask;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.scenebuilder.fxml.api.HierarchyMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.util.StringUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata.Visibility;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization.Qualifier;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentPropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.SbComponentClassMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;

import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 *
 */
public abstract class AbstractHierarchyMask implements HierarchyMask {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHierarchyMask.class);
    private FXOMObject fxomObject;
    private List<Accessory> accessories;
    private Accessory mainAccessory;
    private Set<? extends ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ? extends SbComponentClassMetadata<?>>> subComponents;
    private SbComponentClassMetadata<?> componentClassMetadata;
    private final SbMetadata metadata;

    protected AbstractHierarchyMask(SbMetadata metadata) {
        this.metadata = metadata;
    }

    protected SbMetadata getMetadata() {
        return metadata;
    }

    protected void setupMask(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;

        if (fxomObject.getSceneGraphObject() != null) {
            logger.info("FxomObject [{}] has a scenegraph object of type {}", getFxomObject(), fxomObject.getSceneGraphObject().getClass());
            this.componentClassMetadata = metadata
                    .queryComponentMetadata(getFxomObject().getMetadataClass());
        } else {
            logger.info("FxomObject [{}] has no scenegraph object", getFxomObject());
            this.componentClassMetadata = metadata.queryComponentMetadata(getFxomObject().getClass());
        }

        if (componentClassMetadata != null) {
            logger.info("FxomObject [{}] has metadata class of type {}", getFxomObject(), componentClassMetadata.getClass());
            this.subComponents = componentClassMetadata.getAllSubComponentProperties();

            this.accessories = this.subComponents.stream()
                .map(cpm -> new AccessoryImpl(this.componentClassMetadata, cpm))
                .collect(Collectors.toList());

            var mainComponent = componentClassMetadata.getMainComponentProperty();
            mainAccessory = mainComponent == null ? null : new AccessoryImpl(this.componentClassMetadata, mainComponent);

            this.accessories.remove(mainAccessory);

        } else {
            logger.warn("FxomObject [{}] has metadata class", componentClassMetadata.getClass());
            this.mainAccessory = null;
            this.subComponents = new HashSet<>();
            this.accessories = new ArrayList<>();
        }
    }

    /**
     * Gets the fxml object model of the object.
     *
     * @return the fxom object
     */
    @Override
    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    /**
     * Gets the fxml object model of the parent object.
     * @return the parent fxom object
     */
    @Override
    public FXOMObject getParentFXOMObject() {
        return fxomObject.getParentObject();
    }

    /**
     * Checks if is the object is an instance of {@link Node}.
     *
     * @return true, if is a {@link Node}
     */
    public boolean isFxNode() {
        return fxomObject.getSceneGraphObject().isNode();
    }

    /**
     * Gets the closest instance of {@link Node} in the object inheritance chain.
     *
     * @return the closest instance of {@link Node} in the object inheritance chain.
     */
    @Override
    public FXOMObject getClosestFxNode() {
        FXOMObject result = fxomObject;

        while (result != null && !result.getSceneGraphObject().isNode()) {
            result = result.getParentObject();
        }

        return result;
    }

    private Qualifier findFxomObjectQualifier() {
        final Object sceneGraphObject;

        if (fxomObject.isVirtual() || fxomObject instanceof FXOMIntrinsic) {
            var cm = metadata.queryComponentMetadata(fxomObject.getClass());
            return cm.getCustomization().applicableQualifiers(fxomObject).stream().findFirst().orElse(Qualifier.UNKNOWN);
//        }
//        // For FXOMIntrinsic, we use the source sceneGraphObject
//        else if (fxomObject instanceof FXOMIntrinsic) {
//            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            // For now, handle icons for scenegraph objects only
            return null;
        }

        var cm = metadata.queryComponentMetadata(sceneGraphObject.getClass());

        return cm.getCustomization().applicableQualifiers(sceneGraphObject).stream().findFirst().orElse(Qualifier.UNKNOWN);

    }
    @Override
    public URL getClassNameIconURL() {
        Qualifier qualifier = findFxomObjectQualifier();
        return qualifier == null ? null : qualifier.getIconUrl();
    }

    @Override
    public Image getClassNameIcon() {
        final URL resource = getClassNameIconURL();
        return ImageUtils.getImage(resource);
    }

    @Override
    public String getClassNameInfo() {
        return getClassNameInfo(getMainAccessory());
    }

    @Override
    public String getClassNameInfo(Accessory accessory) {
        final Object sceneGraphObject;
        String classNameInfo = null;
        String prefix = "", suffix = ""; //NOCHECK

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
            switch (fxomIntrinsic.getType()) {
            case FX_COPY:
                // Add FXML prefix for included FXML file
                prefix += "COPY "; //NOCHECK
                break;
            case FX_INCLUDE:
                prefix += "FXML "; //NOCHECK
                break;
            case FX_REFERENCE:
                prefix += "REF "; //NOCHECK
                break;
            default:
                assert false;
                break;

            }
//            if (fxomIntrinsic.getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
//                // Add FXML prefix for included FXML file
//                prefix += "FXML "; //NOCHECK
//            }
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject().get();
        }

        if (sceneGraphObject == null && sceneGraphObject instanceof Node) {
            final Node node = (Node) sceneGraphObject;
            classNameInfo = prefix + sceneGraphObject.getClass().getSimpleName() + suffix;

            var parentMetadata = componentClassMetadata.getParentMetadata();
            var labelMutation = componentClassMetadata.getCustomization().getLabelMutation();
            if (labelMutation != null) {
                classNameInfo = labelMutation.mutate(classNameInfo, sceneGraphObject);
            }
            if (accessory != null && parentMetadata != null) {
                var parentCutomization = parentMetadata.getCustomization();
                var childMutation = parentCutomization.getChildLabelMutations(accessory.getPropertyMetadata());
                if (childMutation != null) {
                    classNameInfo = childMutation.mutate(classNameInfo, node.getParent(), node);
                }
            }
        } else {
            classNameInfo = prefix + fxomObject.getGlueElement().getTagName() + suffix;
        }

        return classNameInfo;
    }

    /**
     * Returns the string value for this FXOM object description property.
     * If the value is internationalized, the returned value is the resolved one.
     *
     * @return
     */
    @Override
    public String getDescription() {
        final var propertyName = getPropertyNameForDescription();
        if (propertyName != null) { // (1)

            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMElement;

            final var fxomElement = (FXOMElement) fxomObject;
            final var vpm = metadata.queryValueProperty(fxomElement, propertyName);
            final var description = vpm.getValueInSceneGraphObject(fxomElement); // resolved value
            return description == null ? null : description.toString();
        }
        return null;
    }

    /**
     * Returns a single line description for this FXOM object.
     *
     * @return
     */
    @Override
    public String getSingleLineDescription() {
        return StringUtils.firstLine(getDescription(), "...");
    }

    /**
     * Returns the object value for this FXOM object node id property.
     *
     * @return
     */
    public Object getNodeIdValue() {
        Object result = null;
        if (fxomObject instanceof FXOMElement) {
            final var fxomElement = (FXOMElement) fxomObject;
            final var propertyName = new PropertyName("id"); //NOCHECK
            final var vpm = metadata.queryValueProperty(fxomElement, propertyName);
            result = vpm.getValueObject(fxomElement);
        }
        return result;
    }

    /**
     * Returns the string value for this FXOM object node id property.
     *
     * @return
     */
    @Override
    public String getNodeId() {
        final Object value = getNodeIdValue();
        String result = null;
        if (value != null) {
            result = value.toString();
        }
        return result;
    }

    @Override
    public String getFxId() {
        String result = null;
        if (fxomObject instanceof FXOMElement) { // Can be null for place holder items
            final var fxomElement = (FXOMElement) fxomObject;
            final var fxId = fxomElement.getFxId();
            result = fxId == null ? "" : fxId; //NOCHECK
        }
        return result;
    }

    @Override
    public boolean isAcceptingAccessory(Accessory accessory) {
        assert accessory != null;
        //TODO an extra look must be given to implement the equal method of Accessoryimpl
        return accessories.contains(accessory) || accessory.equals(getMainAccessory());
//        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
//        final Class<?> valueClass = getClassForAccessory(accessory);
//        return isAcceptingProperty(propertyName, valueClass);
    }

    /**
     * Returns true if this mask accepts the specified fxomObject as accessory.
     *
     * @param accessory
     * @param fxomObject
     * @return
     */
    @Override
    public boolean isAcceptingAccessory(final Accessory accessory, final FXOMObject fxomObject) {

        if (!isAcceptingAccessory(accessory)) {
            logger.info("Object {} does not contain accessory: {}", getFxomObject(), accessory.getName());
            return false;
        }

        if (fxomObject.isVirtual()) {
            logger.info("virtual object {} accepted into accessory: {}", getFxomObject(), accessory.getName());
            return true;
        } else {
            final Object sceneGraphObject;
            if (fxomObject instanceof FXOMIntrinsic) {
                sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
            } else {
                sceneGraphObject = fxomObject.getSceneGraphObject();
            }

            if (accessory.isAccepting(sceneGraphObject)) {
                logger.info("object {} accepted into accessory:{} this object {}",
                        getFxomObject() == null ? "null" : getFxomObject().getClass().getName(),
                        accessory == null? "null" : accessory.getName().getName(),
                        fxomObject == null ? "null" : fxomObject.getClass().getSimpleName());
                return true;
            }
        }

        logger.info("object {} refused into accessory:{} this object {}",
                getFxomObject() == null ? "null" : getFxomObject().getClass().getName(),
                accessory == null? "null" : accessory.getName().getName(),
                fxomObject == null ? "null" : fxomObject.getClass().getSimpleName());

        return false;
    }

    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    @Override
    public boolean isAcceptingAccessory(final Accessory accessory, final Collection<FXOMObject> fxomObjects) {

        if (accessory != null && fxomObjects != null) {
            final boolean forbidenItemExists = fxomObjects.stream().anyMatch(fo -> !isAcceptingAccessory(accessory, fo));

            if (forbidenItemExists) {
                return false;
            }

            // handle case when accessory is not a collection and more than one non virtual objects in fxomObjects
            if (!accessory.isCollection()
                    && fxomObjects.stream().filter(Predicate.not(FXOMObject::isVirtual)).count() > 1) {
                return false;
            }

            return true;
        }

        return false;
    }


//    @Override
//    public FXOMObject getAccessory(Accessory accessory) {
//        assert !accessory.isCollection();
//        final List<FXOMObject> results = getAccessories(accessory);
//
//        if (results != null) {
//            assert results.size() >= 1 : "accessory=" + accessory;
//            return results.stream().filter(f -> !FXOMVirtual.class.isInstance(f)).findFirst().orElse(null);
//        } else {
//            return null;
//        }
//    }


    @Override
    public List<FXOMObject> getAccessories(Accessory accessory, boolean includeVirtuals) {
        assert isAcceptingAccessory(accessory);
        assert fxomObject instanceof FXOMElement;

        final var fxomElement = (FXOMElement) fxomObject;
        final var propertyName = getPropertyNameForAccessory(accessory);
        final var fxomProperty = fxomElement.getProperties().get(propertyName);
        final List<FXOMObject> result = new ArrayList<>();

        if (fxomProperty instanceof FXOMPropertyC) {
            final var fxomPropertyC = (FXOMPropertyC) fxomProperty;
            assert fxomPropertyC.getChildren() != null : "accessory=" + accessory;
            result.addAll(fxomPropertyC.getChildren());
        }

        if (!includeVirtuals) {
            result.removeIf(FXOMObject::isVirtual);
        }
        return result;
    }

    @Override
    public boolean hasMainAccessory() {
        return mainAccessory != null;
    }

    /**
     * Returns true if this mask accepts the specified sub component.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean isAcceptingSubComponent(FXOMObject obj) {
        assert obj != null;
        return hasMainAccessory() && isAcceptingAccessory(mainAccessory, obj);
    }

    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    @Override
    public boolean isAcceptingSubComponent(final Collection<FXOMObject> fxomObjects) {
        if (mainAccessory != null) {

            boolean hasRealObjects = fxomObjects.stream().anyMatch(Predicate.not(FXOMObject::isVirtual));

            if (!mainAccessory.isCollection() && hasRealObjects && getSubComponentCount(mainAccessory, false) >= 1) {
                return false;
            }

            return isAcceptingAccessory(mainAccessory, fxomObjects);

        }
        return false;
    }

    @Override
    public int getSubComponentCount(Accessory accessory, boolean includeVirtuals) {
        final var name = accessory.getName();
        return (name == null) ? 0 : getSubComponents(accessory, includeVirtuals).size();
    }

    @Override
    public FXOMObject getSubComponentAtIndex(Accessory accessory, int i, boolean includeVirtuals) {
        assert 0 <= i;
        assert i < getSubComponentCount(accessory, includeVirtuals);
        assert accessory.getName() != null;

        return getSubComponents(accessory, includeVirtuals).get(i);
    }

    public List<FXOMObject> getSubComponents(Accessory accessory, boolean includeVirtuals) {

        assert accessory.getName() != null;
        // not true anymore main component can be a single element
        //assert accessory.isCollection();
        assert fxomObject instanceof FXOMElement;

        final var subComponentPropertyName = accessory.getName();
        final var fxomInstance = (FXOMElement) fxomObject;
        final var fxomProperty = fxomInstance.getProperties().get(subComponentPropertyName);

        final List<FXOMObject> result;
        if (fxomProperty instanceof FXOMPropertyC fpc) {
            result = fpc.getChildren();
        } else {
            result = Collections.emptyList();
        }

        if (!includeVirtuals) {
            result.removeIf(FXOMObject::isVirtual);
        }

        return result;
    }

    @Override
    public PropertyName getPropertyNameForDescription() {
        final var custo = componentClassMetadata.getCustomization();
        return componentClassMetadata == null ? null : custo.getDescriptionProperty();
    }

    @Override
    public boolean isResourceKey(PropertyName propertyName) {
        if (propertyName != null) { // (1)
         // Retrieve the unresolved description
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final var fxomInstance = (FXOMInstance) fxomObject;
            final var vpm = metadata.queryValueProperty(fxomInstance, propertyName);
            final var description = vpm.getValueObject(fxomInstance); // unresolved value
            //FIXME description can be null
            assert description != null;
            final var pv = new PrefixedValue(description.toString());
            return pv.isResourceKey();
        }
        return false;
    }

    @Override
    public PropertyName getPropertyNameForAccessory(Accessory accessory) {
        return accessory.getName();
    }

    @Override
    public Accessory getAccessory(PropertyName propertyName) {
        if (propertyName == null) {
            return null;
        }

        if (getMainAccessory() != null && propertyName.equals(getMainAccessory().getName())) {
            return getMainAccessory();
        }

        Optional<Accessory> result = accessories.stream().filter(a -> a.getName().equals(propertyName)).findFirst();
        return result.isEmpty() ? null : result.get();
    }

    public FXOMPropertyC getAccessoryProperty(Accessory accessory) {

        assert getPropertyNameForAccessory(accessory) != null;
        assert fxomObject instanceof FXOMElement;

        final PropertyName accessoryPropertyName = getPropertyNameForAccessory(accessory);
        final FXOMElement fxomElement = (FXOMElement) fxomObject;
        final FXOMProperty result = fxomElement.getProperties().get(accessoryPropertyName);

        assert (result == null) || (result instanceof FXOMPropertyC);

        return (FXOMPropertyC) result;
    }

    /**
     *
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
    @Override
    public boolean needResizeWhenTopElement() {
        boolean result = subComponents.stream().map(cmp -> cmp.getCustomization())
                .anyMatch(cmp -> cmp.isResizeNeededWhenTopElement());

        final var custo = componentClassMetadata.getCustomization();
        result &= custo.isResizeNeededWhenTopElement();

        return result;
    }

    // new from here

    @Override
    public int getSubComponentCount(boolean includeVirtuals) {
        if (mainAccessory == null) {
            return 0;
        }
        return getSubComponents(mainAccessory, includeVirtuals).size();
    }

    @Override
    public FXOMObject getSubComponentAtIndex(int i, boolean includeVirtuals) {
        if (mainAccessory == null) {
            return null;
        }
        return getSubComponents(mainAccessory, includeVirtuals).get(i);
    }

    @Override
    public List<FXOMObject> getSubComponents(boolean includeVirtuals) {
        if (mainAccessory == null) {
            return Collections.emptyList();
        }
        return getSubComponents(mainAccessory, includeVirtuals);
    }

    @Override
    public ValuePropertyMetadata<ValuePropertyMetadata<ValuePropertyMetadataCustomization>> getPropertyMetadata(PropertyName propertyName) {
        if (fxomObject instanceof FXOMElement fxe) {
            return metadata.queryValueProperty(fxe, propertyName);
        }
        return null;
    }

    @Override
    public Object getPropertyValue(PropertyName propertyName) {
        var vpm = getPropertyMetadata(propertyName);
        if (vpm != null && fxomObject instanceof FXOMElement fxe) {
            return vpm.getValueObject(fxe);
        }
        return null;
    }

    @Override
    public Object getPropertySceneGraphValue(PropertyName propertyName) {
        var vpm = getPropertyMetadata(propertyName);
        if (vpm != null && fxomObject instanceof FXOMElement fxe) {
            return vpm.getValueInSceneGraphObject(fxe);
        }
        return null;
    }

    @Override
    public boolean isReadOnlyProperty(PropertyName propertyName) {
        var vpm = getPropertyMetadata(propertyName);
        if (vpm != null) {
            return !vpm.isReadWrite();
        }
        return true;
    }

    @Override
    public boolean isMultilineProperty(PropertyName propertyName) {
        var vpm = getPropertyMetadata(propertyName);
        if (vpm != null && vpm instanceof StringPropertyMetadata spm) {
            return spm.isMultiline();
        }
        return false;
    }

    @Override
    public boolean hasProperty(PropertyName propertyName) {
        return getPropertyMetadata(propertyName) != null;
    }

    public static class AccessoryImpl implements Accessory{
        private final SbComponentClassMetadata<?> owner;
        private final ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ? extends SbComponentClassMetadata<?>> propertyMetadata;

        public AccessoryImpl(SbComponentClassMetadata<?> owner,
                ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ? extends SbComponentClassMetadata<?>> propertyMetadata) {
            super();
            this.owner = owner;
            this.propertyMetadata = propertyMetadata;
        }

        public SbComponentClassMetadata<?> getOwner() {
            return owner;
        }

        @Override
        public ComponentPropertyMetadata<ComponentPropertyMetadataCustomization, ? extends SbComponentClassMetadata<?>> getPropertyMetadata() {
            return propertyMetadata;
        }

        @Override
        public PropertyName getName() {
            return propertyMetadata.getName();
        }

        @Override
        public Class<?> getContentType() {
            if (propertyMetadata != null && propertyMetadata.getClassMetadata() != null) {
                return propertyMetadata.getClassMetadata().getKlass();
            }
            return null;
        }

        @Override
        public boolean isCollection() {
            return propertyMetadata.isCollection();
        }

        @Override
        public boolean isAccepting(Object value) {
            if (value == null) {
                return false;
            }
            return isAccepting(value.getClass());
        }

        @Override
        public boolean isAccepting(Class<?> valueClass) {
            final boolean result;
            if (propertyMetadata == null) {
                result = false;
            } else if (getContentType() == null) {
                result = true;
            } else {
                result = getContentType().isAssignableFrom(valueClass);
            }

            return result;
        }

        @Override
        public boolean isMain() {
            return propertyMetadata.isMain();
        }

        @Override
        public boolean isHidden() {
            return propertyMetadata.getVisibility() == Visibility.HIDDEN;
        }

        @Override
        public boolean isStandard() {
            return propertyMetadata.getVisibility() == Visibility.STANDARD;
        }

        @Override
        public boolean isExpert() {
            return propertyMetadata.getVisibility() == Visibility.EXPERT;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((propertyMetadata == null) ? 0 : propertyMetadata.getName().getName().hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            AccessoryImpl other = (AccessoryImpl) obj;
            if (propertyMetadata == null) {
                if (other.propertyMetadata != null)
                    return false;
            } else if (!propertyMetadata.getName().getName().equals(other.propertyMetadata.getName().getName()))
                return false;
            return true;
        }

        @Override
        public boolean isFreeChildPositioning() {
            var custo = owner.getCustomization();
            return custo.isFreeChildPositioning(propertyMetadata);
        }



    }
    @Override
    public List<Accessory> getAccessories() {
        return accessories;
    }

    @Override
    public Accessory getMainAccessory() {
        return mainAccessory;
    }

}
