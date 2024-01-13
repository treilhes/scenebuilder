/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.fxom;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueCharacters;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;

public class FXOMScript extends FXOMVirtual {

    private static final String CHARSET_PROPERTY = "charset";
    private static final String SOURCE_PROPERTY = "source";

    FXOMScript(FXOMDocument fxomDocument, GlueElement glueElement, Object sceneGraphObject) {
        super(fxomDocument, glueElement, sceneGraphObject);
    }

    public FXOMScript(FXOMDocument document) {
        super(document, "fx:script");
    }

    @Override
    public List<FXOMObject> getChildObjects() {
        return Collections.emptyList();
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        // TODO Auto-generated method stub

    }

    public String getSource() {
        return getGlueElement().getAttributes().get(SOURCE_PROPERTY);
    }

    public void setSource(String source) {
        if (source == null) {
            getGlueElement().getAttributes().remove(SOURCE_PROPERTY);
        } else {
            getGlueElement().getAttributes().put(SOURCE_PROPERTY, source);
        }
    }

    public String getScript() {
        StringBuilder builder = new StringBuilder();

        getGlueElement().getFront().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });
        getGlueElement().getContent().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });
        getGlueElement().getTail().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });

        return builder.toString();
    }

    public void setScript(String script) {
        getGlueElement().getFront().clear();
        getGlueElement().getContent().clear();
        getGlueElement().getTail().clear();
        getGlueElement().setContentText(script);
    }

}