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
package com.oracle.javafx.scenebuilder.core.metadata.util;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.core.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.ChildLabelMutation;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.Node;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 */
public class DesignHierarchyMask implements HierarchyMask {
    
    private static final Logger logger = LoggerFactory.getLogger(DesignHierarchyMask.class);
    
    private final FXOMObject fxomObject;
    private final List<Accessory> accessories;
    private final Accessory mainAccessory;
    private final Set<ComponentPropertyMetadata> subComponents;
    private final ComponentClassMetadata<?> componentClassMetadata;
    
    public DesignHierarchyMask(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;
        
        this.componentClassMetadata = Metadata.getMetadata()
                .queryComponentMetadata(getFxomObject().getSceneGraphObject().getClass());
        
//        this.subComponents = Metadata.getMetadata()
//                .queryComponentProperties(getFxomObject().getSceneGraphObject().getClass());
        this.subComponents = componentClassMetadata.getAllSubComponentProperties();
        
        this.accessories = this.subComponents.stream()
            .map(cpm -> new AccessoryImpl(this.componentClassMetadata, cpm))
            .collect(Collectors.toList());
        
        ComponentPropertyMetadata mainComponent = componentClassMetadata.getMainComponentProperty();
        mainAccessory = mainComponent == null ? null : new AccessoryImpl(this.componentClassMetadata, mainComponent);
        
        this.accessories.remove(mainAccessory);
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
    public FXOMObject getParentFXOMObject() {
        return fxomObject.getParentObject();
    }

    /**
     * Checks if is the object is an instance of {@See Node}.
     *
     * @return true, if is a {@See Node}
     */
    public boolean isFxNode() {
        return fxomObject.getSceneGraphObject() instanceof Node;
    }

    /**
     * Gets the closest instance of {@See Node} in the object inheritance chain.
     *
     * @return the closest instance of {@See Node} in the object inheritance chain.
     */
    public FXOMObject getClosestFxNode() {
        FXOMObject result = fxomObject;
        DesignHierarchyMask mask = this;

        while ((result != null) && (mask.isFxNode() == false)) {
            result = mask.getParentFXOMObject();
            mask = (result == null) ? null : new DesignHierarchyMask(result);
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
        
        ComponentClassMetadata<?> cm = Metadata.getMetadata().queryComponentMetadata(sceneGraphObject.getClass());
        
        return cm.applicableQualifiers(sceneGraphObject).stream().findFirst().orElse(Qualifier.UNKNOWN);
        
    }
    public URL getClassNameIconURL() {
        Qualifier qualifier = findFxomObjectQualifier();
        return qualifier == null ? null : qualifier.getIconUrl();
    }

    public Image getClassNameIcon() {
        final URL resource = getClassNameIconURL();
        return ImageUtils.getImage(resource);
    }

    public String getClassNameInfo() {
        return getClassNameInfo(getMainAccessory());
    }
    
    public String getClassNameInfo(Accessory accessory) {
        final Object sceneGraphObject;
        String classNameInfo = null;
        String prefix = "", suffix = ""; //NOI18N

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
            if (fxomIntrinsic.getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
                // Add FXML prefix for included FXML file
                prefix += "FXML "; //NOI18N
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
    public String getDescription() {
        final PropertyName propertyName = getPropertyNameForDescription();
        if (propertyName != null) { // (1)
            
            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
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
    public String getSingleLineDescription() {
        String result = getDescription();
        if (result != null && containsLineFeed(result)) {
            result = result.substring(0, result.indexOf('\n')) + "..."; //NOI18N
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
            final PropertyName propertyName = new PropertyName("id"); //NOI18N
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            result = vpm.getValueObject(fxomInstance);
        }
        return result;
    }

    /**
     * Returns the string value for this FXOM object node id property.
     *
     * @return
     */
    public String getNodeId() {
        final Object value = getNodeIdValue();
        String result = null;
        if (value != null) {
            result = value.toString();
        }
        return result;
    }

    public String getFxId() {
        String result = null;
        if (fxomObject instanceof FXOMInstance) { // Can be null for place holder items
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final String fxId = fxomInstance.getFxId();
            result = fxId == null ? "" : fxId; //NOI18N
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
    public boolean isAcceptingAccessory(final Accessory accessory, final FXOMObject fxomObject) {
        final Object sceneGraphObject;
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        
        boolean accept =isAcceptingAccessory(accessory)
                && accessory.getContentType().isInstance(sceneGraphObject);
        
        if (logger.isDebugEnabled()) {
            logger.info("object {} accepted into accessory:{} this object {}",
                    getFxomObject() == null ? "null" : getFxomObject().getClass().getName(),
                    accessory == null? "null" : accessory.getName().getName(),
                    fxomObject == null ? "null" : fxomObject.getClass().getSimpleName());
        }
        
        return accept;
    }
    
    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    public boolean isAcceptingAccessory(final Accessory accessory, final Collection<FXOMObject> fxomObjects) {
        boolean accept = false;
        if (accessory != null && getAccessories().contains(accessory)) {
            final ComponentPropertyMetadata subComponentMetadata
                    = accessory.getPropertyMetadata();
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            
            accept = true;
            for (FXOMObject obj : fxomObjects) {
                final Object sceneGraphObject;
                if (obj instanceof FXOMIntrinsic) {
                    final FXOMIntrinsic intrinsicObj = (FXOMIntrinsic) obj;
                    sceneGraphObject = intrinsicObj.getSourceSceneGraphObject();
                } else {
                    sceneGraphObject = obj.getSceneGraphObject();
                }
                if (!subComponentClass.isInstance(sceneGraphObject)) {
                    accept = false;
                    break;
                }
            }
            
        }
        
        if (accept && logger.isDebugEnabled()) {
            logger.info("object {} accepted into accessory:{} those objects {}",
                    getFxomObject() == null ? "null" : getFxomObject().getSceneGraphObject().getClass().getName(),
                    accessory == null? "null" : accessory.getName().getName(),
                    fxomObjects == null ? "null" : fxomObjects.stream().map(fxo -> fxo.getSceneGraphObject().getClass().getSimpleName()).collect(Collectors.toList()));
        }
        
        return accept;
    }

    
    @Override
    //TODO what about collection true accessories?
    public FXOMObject getAccessory(Accessory accessory) {
        assert isAcceptingAccessory(accessory);
        assert fxomObject instanceof FXOMInstance;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        final FXOMObject result;

        if (fxomProperty instanceof FXOMPropertyC) {
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            assert fxomPropertyC.getValues().size() >= 1 : "accessory=" + accessory;
            result = fxomPropertyC.getValues().get(0);
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
    public boolean isAcceptingSubComponent(FXOMObject obj) {
        final boolean result;

        assert obj != null;

        if (mainAccessory == null) {
            result = false;
        } else {
            final ComponentPropertyMetadata subComponentMetadata
                    = mainAccessory.getPropertyMetadata();
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            final Object sceneGraphObject;
            if (obj instanceof FXOMIntrinsic) {
                sceneGraphObject = ((FXOMIntrinsic) obj).getSourceSceneGraphObject();
            } else {
                sceneGraphObject = obj.getSceneGraphObject();
            }
            result = subComponentClass.isInstance(sceneGraphObject);
        }

        return result;
    }

    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    public boolean isAcceptingSubComponent(final Collection<FXOMObject> fxomObjects) {
        if (mainAccessory != null) {
            final ComponentPropertyMetadata subComponentMetadata
                    = mainAccessory.getPropertyMetadata();
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            for (FXOMObject obj : fxomObjects) {
                final Object sceneGraphObject;
                if (obj instanceof FXOMIntrinsic) {
                    final FXOMIntrinsic intrinsicObj = (FXOMIntrinsic) obj;
                    sceneGraphObject = intrinsicObj.getSourceSceneGraphObject();
                } else {
                    sceneGraphObject = obj.getSceneGraphObject();
                }
                if (!subComponentClass.isInstance(sceneGraphObject)) {
                    return false;
                }
            }
            return true;
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
        assert accessory.isCollection();
        assert fxomObject instanceof FXOMInstance;

        final PropertyName subComponentPropertyName = accessory.getName();
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty fxomProperty
                = fxomInstance.getProperties().get(subComponentPropertyName);

        final List<FXOMObject> result;
        if (fxomProperty instanceof FXOMPropertyC) {
            result = ((FXOMPropertyC) fxomProperty).getValues();
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    public PropertyName getPropertyNameForDescription() {
        return componentClassMetadata.getDescriptionProperty();
    }

    public boolean isResourceKey(PropertyName propertyName) {
        if (propertyName != null) { // (1)
         // Retrieve the unresolved description
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueObject(fxomInstance); // unresolved value
            final PrefixedValue pv = new PrefixedValue(description.toString());
            return pv.isResourceKey();
        }
        return false;
    }

    public PropertyName getPropertyNameForAccessory(Accessory accessory) {
        return accessory.getName();
    }
    
    public Accessory getAccessoryForPropertyName(PropertyName propertyName) {
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
        return str.contains("\n"); //NOI18N
    }

    /**
     *
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
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
        public boolean isAccepting(Class<?> valueClass) {
            final boolean result;
            if (propertyMetadata == null) {
                result = false;
            } else {
                result = propertyMetadata.getClassMetadata().getKlass().isAssignableFrom(valueClass);
            }

            return result;
        }

        @Override
        public boolean isMain() {
            return propertyMetadata.isMain();
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

    public Accessory getMainAccessory() {
        return mainAccessory;
    }
    
    
}
