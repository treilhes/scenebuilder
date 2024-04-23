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
package org.scenebuilder.ext.javafx.customization.anchorpane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.metadata.javafx.PropertyNames;
import com.oracle.javafx.scenebuilder.metadata.javafx.hidden.NodeMetadata;

@Component
public class NodeMetadataAddin {

    public final PropertyName AnchorPane_anchorsGroupName = new PropertyName("anchorsGroup",
            javafx.scene.layout.AnchorPane.class); // NOCHECK

    public NodeMetadataAddin(@Autowired NodeMetadata nodeMetadata) {
        super();

        final AnchorPropertyGroupMetadata AnchorPane_AnchorPropertyGroupMetadata = new AnchorPropertyGroupMetadata.Builder()
                .withName(AnchorPane_anchorsGroupName)
                .withTopAnchorProperty(nodeMetadata.AnchorPane_topAnchorPropertyMetadata)
                .withRightAnchorProperty(nodeMetadata.AnchorPane_rightAnchorPropertyMetadata)
                .withBottomAnchorProperty(nodeMetadata.AnchorPane_bottomAnchorPropertyMetadata)
                .withLeftAnchorProperty(nodeMetadata.AnchorPane_leftAnchorPropertyMetadata)
                .withInspectorPath(new InspectorPath("Layout", "AnchorPane COnst", 0))
                .build();

        nodeMetadata.getProperties().add(AnchorPane_AnchorPropertyGroupMetadata);

//        nodeMetadata.getProperties().removeAll(Arrays.asList(
//                nodeMetadata.AnchorPane_topAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_rightAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_bottomAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_leftAnchorPropertyMetadata
//                ));


        nodeMetadata.getShadowedProperties().add(PropertyNames.AnchorPane_topAnchorName);
        nodeMetadata.getShadowedProperties().add(PropertyNames.AnchorPane_rightAnchorName);
        nodeMetadata.getShadowedProperties().add(PropertyNames.AnchorPane_bottomAnchorName);
        nodeMetadata.getShadowedProperties().add(PropertyNames.AnchorPane_leftAnchorName);



    }


}
