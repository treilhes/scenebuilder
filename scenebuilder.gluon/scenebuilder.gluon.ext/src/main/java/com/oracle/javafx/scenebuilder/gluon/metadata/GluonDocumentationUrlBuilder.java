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
package com.oracle.javafx.scenebuilder.gluon.metadata;

import java.util.Locale;

import org.scenebuilder.fxml.api.DocumentationUrlBuilder;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.gluon.GluonConstants;

import javafx.scene.Node;

@Component
public class GluonDocumentationUrlBuilder implements DocumentationUrlBuilder {

    @Override
    public boolean canBuild(Class<?> cls) {
        return cls.getName().startsWith(GluonConstants.GLUON_PACKAGE);
    }

    @Override
    public String buildUrl(Class<?> cls, ValuePropertyMetadata propMeta) {
        String propNameStr = propMeta.getName().getName();
        // First char in uppercase
        propNameStr = propNameStr.substring(0, 1).toUpperCase(Locale.ENGLISH) + propNameStr.substring(1);
        String methodName;
        String posfix = "--";

        if (propMeta.getValueClass() == Boolean.class) {
            methodName = "is" + propNameStr + posfix; //NOCHECK
        } else if (propMeta.isStaticProperty()) {
            methodName = "get" + propNameStr + "(" + Node.class.getName() + ")"; //NOCHECK
        } else {
            methodName = "get" + propNameStr + posfix; //NOCHECK
        }

        String url = GluonConstants.GLUON_JAVADOC_HOME;
        url += cls.getName().replaceAll("\\.", "/") + ".html"; //NOCHECK
        url += "#" + methodName; //NOCHECK
        return url;
    }

}
