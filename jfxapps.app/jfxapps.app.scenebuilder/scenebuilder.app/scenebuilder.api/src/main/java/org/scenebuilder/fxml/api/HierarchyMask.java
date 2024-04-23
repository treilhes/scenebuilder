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
package org.scenebuilder.fxml.api;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.image.Image;

public interface HierarchyMask {
    // TODO this must disapear when control project will contain owned jobs/drivers
    public interface AccessoryProperty {
        PropertyName ROOT = new PropertyName("root");
        PropertyName CONTEXT_MENU = new PropertyName("contextMenu");
        PropertyName TOOLTIP = new PropertyName("tooltip");
    }
    public interface Accessory {
        PropertyName getName();
        Class<?> getContentType();
        boolean isCollection();
        boolean isAccepting(Object value);
        boolean isAccepting(Class<?> valueClass);
        boolean isMain();
        boolean isFreeChildPositioning();
        ComponentPropertyMetadata getPropertyMetadata();

        boolean isHidden();
        boolean isStandard();
        boolean isExpert();
    }

    public boolean isAcceptingAccessory(Accessory accessory);

    //public FXOMObject getAccessory(Accessory accessory);

    public Accessory getAccessory(PropertyName accessoryName);

    public List<FXOMObject> getAccessories(Accessory accessory, boolean includeVirtuals);

    public boolean hasMainAccessory();

    public int getSubComponentCount(Accessory accessory, boolean includeVirtuals);

    public int getSubComponentCount(boolean includeVirtuals);

    public FXOMObject getSubComponentAtIndex(Accessory accessory, int i, boolean includeVirtuals);

    public FXOMObject getSubComponentAtIndex(int i, boolean includeVirtuals);

    public boolean isAcceptingAccessory(Accessory accessory, FXOMObject newObject);

    public FXOMObject getFxomObject();

    //new
    public List<Accessory> getAccessories();

    public Accessory getMainAccessory();

    public PropertyName getPropertyNameForDescription();

    public String getClassNameInfo();

    public String getSingleLineDescription();

    public Image getClassNameIcon();

    public FXOMObject getParentFXOMObject();

    public boolean isResourceKey(PropertyName propertyNameForDescription);

    public FXOMObject getClosestFxNode();

    public String getDescription();



    public PropertyName getPropertyNameForAccessory(Accessory accessory);

    public boolean needResizeWhenTopElement();

    public boolean isAcceptingSubComponent(FXOMObject newObject);

    public boolean isAcceptingSubComponent(Collection<FXOMObject> fxomObjects);

    public List<FXOMObject> getSubComponents(boolean includeVirtuals);

    public URL getClassNameIconURL();

    public boolean isAcceptingAccessory(Accessory targetAccessory, Collection<FXOMObject> draggedObject);

    public String getNodeId();

    public String getFxId();

    public String getClassNameInfo(Accessory accessory);


    public ValuePropertyMetadata getPropertyMetadata(PropertyName propertyName);
    public Object getPropertyValue(PropertyName propertyName);
    public Object getPropertySceneGraphValue(PropertyName propertyName);
    public boolean isReadOnlyProperty(PropertyName propertyName);
    public boolean isMultilineProperty(PropertyName propertyName);
    public boolean hasProperty(PropertyName propertyName);

}
