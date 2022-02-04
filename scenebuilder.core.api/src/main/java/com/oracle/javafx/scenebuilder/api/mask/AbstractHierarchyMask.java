/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMVirtual;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.ChildLabelMutation;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata.Visibility;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.Node;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 */
public abstract class AbstractHierarchyMask implements HierarchyMask {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHierarchyMask.class);

    private FXOMObject fxomObject;
    private List<Accessory> accessories;
    private Accessory mainAccessory;
    private Set<ComponentPropertyMetadata> subComponents;
    private ComponentClassMetadata<?> componentClassMetadata;
    private final Metadata metadata;

    protected AbstractHierarchyMask(Metadata metadata) {
        this.metadata = metadata;
    }

    protected Metadata getMetadata() {
        return metadata;
    }

    protected void setupMask(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;

        if (fxomObject.getSceneGraphObject() == null) {
            logger.warn("FxomObject [{}] has no scenegraph object", getFxomObject());
            this.componentClassMetadata = null;
            this.mainAccessory = null;
            this.subComponents = new HashSet<ComponentPropertyMetadata>();
            this.accessories = new ArrayList<HierarchyMask.Accessory>();
        } else {
            this.componentClassMetadata = metadata
                    .queryComponentMetadata(getFxomObject().getSceneGraphObject().getClass());

            this.subComponents = componentClassMetadata.getAllSubComponentProperties();

            this.accessories = this.subComponents.stream()
                .map(cpm -> new AccessoryImpl(this.componentClassMetadata, cpm))
                .collect(Collectors.toList());

            ComponentPropertyMetadata mainComponent = componentClassMetadata.getMainComponentProperty();
            mainAccessory = mainComponent == null ? null : new AccessoryImpl(this.componentClassMetadata, mainComponent);

            this.accessories.remove(mainAccessory);
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
        return fxomObject.getSceneGraphObject() instanceof Node;
    }

    /**
     * Gets the closest instance of {@link Node} in the object inheritance chain.
     *
     * @return the closest instance of {@link Node} in the object inheritance chain.
     */
    @Override
    public FXOMObject getClosestFxNode() {
        FXOMObject result = fxomObject;

        while ((result != null) && !(result.getSceneGraphObject() instanceof Node)) {
            result = result.getParentObject();
        }

        return result;
    }

    private Qualifier findFxomObjectQualifier() {
        final Object sceneGraphObject;

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            // For now, handle icons for scenegraph objects only
            return null;
        }

        ComponentClassMetadata<?> cm = metadata.queryComponentMetadata(sceneGraphObject.getClass());

        return cm.applicableQualifiers(sceneGraphObject).stream().findFirst().orElse(Qualifier.UNKNOWN);

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
            if (fxomIntrinsic.getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
                // Add FXML prefix for included FXML file
                prefix += "FXML "; //NOCHECK
            }
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null && sceneGraphObject instanceof Node) {
            final Node node = (Node) sceneGraphObject;
            classNameInfo = prefix + sceneGraphObject.getClass().getSimpleName() + suffix;

            if (componentClassMetadata.getLabelMutation() != null) {
                classNameInfo = componentClassMetadata.getLabelMutation().mutate(classNameInfo, sceneGraphObject);
            }
            if (accessory != null && componentClassMetadata.getParentMetadata() != null) {
                ChildLabelMutation childMutation = componentClassMetadata.getParentMetadata().getChildLabelMutations(accessory.getPropertyMetadata());
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
        final PropertyName propertyName = getPropertyNameForDescription();
        if (propertyName != null) { // (1)

            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = metadata.queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueInSceneGraphObject(fxomInstance); // resolved value
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
        String result = getDescription();
        if (result != null && containsLineFeed(result)) {
            result = result.substring(0, result.indexOf('\n')) + "..."; //NOCHECK
        }
        return result;
    }

    /**
     * Returns the object value for this FXOM object node id property.
     *
     * @return
     */
    public Object getNodeIdValue() {
        Object result = null;
        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final PropertyName propertyName = new PropertyName("id"); //NOCHECK
            final ValuePropertyMetadata vpm
                    = metadata.queryValueProperty(fxomInstance, propertyName);
            result = vpm.getValueObject(fxomInstance);
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
        if (fxomObject instanceof FXOMInstance) { // Can be null for place holder items
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final String fxId = fxomInstance.getFxId();
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

        if (fxomObject instanceof FXOMVirtual) {
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
            return !forbidenItemExists;
        }

        return false;
    }


    @Override
    public FXOMObject getAccessory(Accessory accessory) {
        assert !accessory.isCollection();
        final List<FXOMObject> results = getAccessories(accessory);

        if (results != null) {
            assert results.size() >= 1 : "accessory=" + accessory;
            return results.stream().filter(f -> !FXOMVirtual.class.isInstance(f)).findFirst().orElse(null);
        } else {
            return null;
        }
    }
//
//    public FXOMObject getAccessory(Accessory accessory, boolean includeVirtualElements) {
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
    public List<FXOMObject> getAccessories(Accessory accessory) {
        assert isAcceptingAccessory(accessory);
        assert fxomObject instanceof FXOMInstance;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        final List<FXOMObject> result;

        if (fxomProperty instanceof FXOMPropertyC) {
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            assert fxomPropertyC.getChildren() != null : "accessory=" + accessory;
            result = fxomPropertyC.getChildren();
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public boolean isAcceptingSubComponent() {
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
        return isAcceptingSubComponent() && isAcceptingAccessory(mainAccessory, obj);
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

            if (!mainAccessory.isCollection() && getSubComponentCount(mainAccessory) >= 1) {
                return false;
            }

            return isAcceptingAccessory(mainAccessory, fxomObjects);

        }
        return false;
    }

    @Override
    public int getSubComponentCount(Accessory accessory) {
        final PropertyName name = accessory.getName();
        return (name == null) ? 0 : getSubComponents(accessory).size();
    }

    @Override
    public FXOMObject getSubComponentAtIndex(Accessory accessory, int i) {
        assert 0 <= i;
        assert i < getSubComponentCount(accessory);
        assert accessory.getName() != null;

        return getSubComponents(accessory).get(i);
    }

    public List<FXOMObject> getSubComponents(Accessory accessory) {

        assert accessory.getName() != null;
        // not true anymore main component can be a single element
        //assert accessory.isCollection();
        assert fxomObject instanceof FXOMInstance;

        final PropertyName subComponentPropertyName = accessory.getName();
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty fxomProperty
                = fxomInstance.getProperties().get(subComponentPropertyName);

        final List<FXOMObject> result;
        if (fxomProperty instanceof FXOMPropertyC) {
            result = ((FXOMPropertyC) fxomProperty).getChildren();
        //} if (fxomProperty instanceof FXOMPropertyT) {
        //    result = ((FXOMPropertyT) fxomProperty).getValues();
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    @Override
    public PropertyName getPropertyNameForDescription() {
        return componentClassMetadata == null ? null : componentClassMetadata.getDescriptionProperty();
    }

    @Override
    public boolean isResourceKey(PropertyName propertyName) {
        if (propertyName != null) { // (1)
         // Retrieve the unresolved description
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = metadata.queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueObject(fxomInstance); // unresolved value
            //FIXME description can be null
            assert description != null;
            final PrefixedValue pv = new PrefixedValue(description.toString());
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
        assert fxomObject instanceof FXOMInstance;

        final PropertyName accessoryPropertyName = getPropertyNameForAccessory(accessory);
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty result
                = fxomInstance.getProperties().get(accessoryPropertyName);

        assert (result == null) || (result instanceof FXOMPropertyC);

        return (FXOMPropertyC) result;
    }

    // Should be in a shared Utils class ?
    public static boolean containsLineFeed(String str) {
        // LF (\n) is used for files generated on UNIX
        // CR+LF (\r\n) is used for files generated on WINDOWS
        // So in both cases, a file containing multi lines will contain LF
        if (str == null) {
            return false;
        }
        return str.contains("\n"); //NOCHECK
    }

    /**
     *
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
    @Override
    public boolean needResizeWhenTopElement() {
        boolean result = subComponents.stream().anyMatch(cmp -> cmp.isResizeNeededWhenTopElement());
        result &= componentClassMetadata.isResizeNeededWhenTopElement();
        return result;
    }

    // new from here

    @Override
    public int getSubComponentCount() {
        if (mainAccessory == null) {
            return 0;
        }
        return getSubComponents(mainAccessory).size();
    }

    @Override
    public FXOMObject getSubComponentAtIndex(int i) {
        if (mainAccessory == null) {
            return null;
        }
        return getSubComponents(mainAccessory).get(i);
    }

    @Override
    public List<FXOMObject> getSubComponents() {
        if (mainAccessory == null) {
            return Collections.emptyList();
        }
        return getSubComponents(mainAccessory);
    }
    @RequiredArgsConstructor
    public static class AccessoryImpl implements Accessory{
        private final @Getter ComponentClassMetadata<?> owner;
        private final @Getter ComponentPropertyMetadata propertyMetadata;

        @Override
        public PropertyName getName() {
            return propertyMetadata.getName();
        }

        @Override
        public Class<?> getContentType() {
            return propertyMetadata.getClassMetadata().getKlass();
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
            return owner.isFreeChildPositioning(propertyMetadata);
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
