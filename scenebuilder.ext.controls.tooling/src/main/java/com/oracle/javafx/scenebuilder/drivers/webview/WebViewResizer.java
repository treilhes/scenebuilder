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
package com.oracle.javafx.scenebuilder.drivers.webview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.util.MathUtils;
import com.oracle.javafx.scenebuilder.drivers.region.RegionResizer;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

/**
 *
 * 
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class WebViewResizer extends AbstractResizer<WebView> {

    private double originalMinWidth;
    private double originalMinHeight;
    private double originalPrefWidth;
    private double originalPrefHeight;
    private double originalMaxWidth;
    private double originalMaxHeight;
    private final PropertyName minWidthName  = new PropertyName("minWidth"); //NOCHECK
    private final PropertyName minHeightName = new PropertyName("minHeight"); //NOCHECK
    private final PropertyName prefWidthName  = new PropertyName("prefWidth"); //NOCHECK
    private final PropertyName prefHeightName = new PropertyName("prefHeight"); //NOCHECK
    private final PropertyName maxWidthName  = new PropertyName("maxWidth"); //NOCHECK
    private final PropertyName maxHeightName = new PropertyName("maxHeight"); //NOCHECK
    private final List<PropertyName> propertyNames = new ArrayList<>();
    
    public WebViewResizer() {
        super();
        propertyNames.add(prefWidthName);
        propertyNames.add(prefHeightName);
        propertyNames.add(minWidthName);
        propertyNames.add(minHeightName);
        propertyNames.add(maxWidthName);
        propertyNames.add(maxHeightName);
    }

    @Override
    public void initialize() {
        originalMinWidth   = sceneGraphObject.getMinWidth();
        originalMinHeight  = sceneGraphObject.getMinHeight();
        originalPrefWidth  = sceneGraphObject.getPrefWidth();
        originalPrefHeight = sceneGraphObject.getPrefHeight();
        originalMaxWidth   = sceneGraphObject.getMaxWidth();
        originalMaxHeight  = sceneGraphObject.getMaxHeight();
    }
    
    @Override
    public final Bounds computeBounds(double width, double height) {
        return new BoundingBox(0, 0, Math.round(width), Math.round(height));
    }

    @Override
    public Feature getFeature() {
        return Feature.FREE;
    }

    @Override
    public void changeWidth(double width) {
        final double w = Math.round(width);
        
        sceneGraphObject.setPrefWidth(w);
        
        if ((originalMinWidth != Region.USE_COMPUTED_SIZE) && (originalMinWidth != Region.USE_PREF_SIZE)) {
            sceneGraphObject.setMinWidth(Math.min(w, originalMinWidth));
        }
        if ((originalMaxWidth != Region.USE_COMPUTED_SIZE) && (originalMaxWidth != Region.USE_PREF_SIZE)) {
            sceneGraphObject.setMaxWidth(Math.max(w, originalMaxWidth));
        }
    }

    @Override
    public void changeHeight(double height) {
        final double h = Math.round(height);
        
        sceneGraphObject.setPrefHeight(h);
        
        if ((originalMinHeight != Region.USE_COMPUTED_SIZE) && (originalMinHeight != Region.USE_PREF_SIZE)) {
            sceneGraphObject.setMinHeight(Math.min(h, originalMinHeight));
        }
        if ((originalMaxHeight != Region.USE_COMPUTED_SIZE) && (originalMaxHeight != Region.USE_PREF_SIZE)) {
            sceneGraphObject.setMaxHeight(Math.max(h, originalMaxHeight));
        }
    }

    @Override
    public void revertToOriginalSize() {
        sceneGraphObject.setMinWidth(originalMinWidth);
        sceneGraphObject.setMinHeight(originalMinHeight);
        sceneGraphObject.setPrefWidth(originalPrefWidth);
        sceneGraphObject.setPrefHeight(originalPrefHeight);
        sceneGraphObject.setMaxWidth(originalMaxWidth);
        sceneGraphObject.setMaxHeight(originalMaxHeight);
    }
    
    @Override
    public List<PropertyName> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public Object getValue(PropertyName propertyName) {
        assert propertyName != null;
        assert propertyNames.contains(propertyName);
        
        final Object result;
        if (propertyName.equals(minWidthName)) {
            result = RegionResizer.makePrefSizeString(sceneGraphObject.getMinWidth());
        } else if (propertyName.equals(minHeightName)) {
            result = RegionResizer.makePrefSizeString(sceneGraphObject.getMinHeight());
        } else if (propertyName.equals(prefWidthName)) {
            result = RegionResizer.makeComputedSizeString(sceneGraphObject.getPrefWidth());
        } else if (propertyName.equals(prefHeightName)) {
            result = RegionResizer.makeComputedSizeString(sceneGraphObject.getPrefHeight());
        } else if (propertyName.equals(maxWidthName)) {
            result = RegionResizer.makePrefSizeString(sceneGraphObject.getMaxWidth());
        } else if (propertyName.equals(maxHeightName)) {
            result = RegionResizer.makePrefSizeString(sceneGraphObject.getMaxHeight());
        } else {
            // Emergency code
            result = null;
        }
        
        return result;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        final Map<PropertyName, Object> result = new HashMap<>();
        if (MathUtils.equals(sceneGraphObject.getMinWidth(), originalMinWidth) == false) {
            result.put(minWidthName, sceneGraphObject.getMinWidth());
        }
        if (MathUtils.equals(sceneGraphObject.getMinHeight(), originalMinHeight) == false) {
            result.put(minHeightName, sceneGraphObject.getMinHeight());
        }
        if (MathUtils.equals(sceneGraphObject.getPrefWidth(), originalPrefWidth) == false) {
            result.put(prefWidthName, sceneGraphObject.getPrefWidth());
        }
        if (MathUtils.equals(sceneGraphObject.getPrefHeight(), originalPrefHeight) == false) {
            result.put(prefHeightName, sceneGraphObject.getPrefHeight());
        }
        if (MathUtils.equals(sceneGraphObject.getMaxWidth(), originalMaxWidth) == false) {
            result.put(maxWidthName, sceneGraphObject.getMaxWidth());
        }
        if (MathUtils.equals(sceneGraphObject.getMaxHeight(), originalMaxHeight) == false) {
            result.put(maxHeightName, sceneGraphObject.getMaxHeight());
        }
        return result;
    }

}
