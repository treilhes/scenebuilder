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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.mask.AbstractHierarchyMask;
import com.gluonhq.jfxapps.core.api.util.StringUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization.Qualifier;
import com.oracle.javafx.scenebuilder.metadata.custom.SbComponentClassMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;

import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Abstract class that provides a common implementation for the HierarchyMask interface.
 */
public class SbFXOMObjectMask
        extends AbstractHierarchyMask<
        SbMetadata,
        SbComponentClassMetadata<?>,
        SbComponentPropertyMetadata,
        ValuePropertyMetadata<ValuePropertyMetadataCustomization>,
        SbAccessory> implements SbHierarchyMask<SbAccessory> {

    private static final Logger logger = LoggerFactory.getLogger(SbFXOMObjectMask.class);

    protected SbFXOMObjectMask(SbMetadata metadata) {
        super(metadata, (ccm, cpm) -> new SbAccessoryImpl(ccm, cpm));
    }


    private Qualifier findFxomObjectQualifier() {
        final var fxomObject = getFxomObject();
        final var metadata = getMetadata();
        final Object sceneGraphObject;

        if (fxomObject.isVirtual() || fxomObject instanceof FXOMIntrinsic) {
            SbComponentClassMetadata<?> cm = metadata.queryComponentMetadata(fxomObject.getClass());
            return cm.getCustomization().applicableQualifiers(fxomObject).stream().findFirst().orElse(Qualifier.UNKNOWN);
//        }
//        // For FXOMIntrinsic, we use the source sceneGraphObject
//        else if (fxomObject instanceof FXOMIntrinsic) {
//            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject().get();
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
    public String getClassNameInfo(SbAccessory accessory) {
        final var fxomObject = getFxomObject();
        final SbComponentClassMetadata<?> componentClassMetadata = getComponentClassMetadata();
        final Object sceneGraphObject;
        String classNameInfo = null;
        String prefix = "", suffix = ""; //NOCHECK

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            assert fxomIntrinsic.getSceneGraphObject().isFromExternalSource();
            sceneGraphObject = fxomIntrinsic.getSceneGraphObject().get();

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
        final var fxomObject = getFxomObject();
        final var metadata = getMetadata();
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

    @Override
    public PropertyName getPropertyNameForDescription() {
        SbComponentClassMetadata<?> componentClassMetadata = getComponentClassMetadata();
        final var custo = componentClassMetadata.getCustomization();
        return componentClassMetadata == null ? null : custo.getDescriptionProperty();
    }

    /**
     * object can be moved if parent supports free child positioning
     *
     */

    public boolean isMovable() {
        PropertyName parentPropertyName = getFxomObject().getParentProperty().getName();
        SbAccessory parentAccessory = getAccessory(parentPropertyName);
        return parentAccessory.isFreeChildPositioning();
    }

    /**
     *
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
    @Override
    public boolean needResizeWhenTopElement() {
        final SbComponentClassMetadata<?> componentClassMetadata = getComponentClassMetadata();
        Set<? extends SbComponentPropertyMetadata> subComponents = getSubComponents();

        boolean result = subComponents.stream().map(cmp -> cmp.getCustomization())
                .anyMatch(cmp -> cmp.isResizeNeededWhenTopElement());

        final var custo = componentClassMetadata.getCustomization();
        result &= custo.isResizeNeededWhenTopElement();

        return result;
    }

    @ApplicationSingleton
    public static final class Factory extends AbstractFactory<SbFXOMObjectMask> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public SbFXOMObjectMask getMask(FXOMObject fxomObject) {
            return create(SbFXOMObjectMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
