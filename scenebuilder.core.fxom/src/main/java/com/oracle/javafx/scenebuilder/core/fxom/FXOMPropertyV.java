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
package com.oracle.javafx.scenebuilder.core.fxom;

import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueDocument;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/*

 */
/**
 * A FXOMPropertyV represents a virtual property not involved in the scenegraph<br/>
 * This kind of property has an unique name and a totaly empty glue element
 * <br/>
 * There are mainly fx tags like <br/>
 * &lt;fx:include><br/>
 * &lt;fx:constant><br/>
 * &lt;fx:reference><br/>
 * &lt;fx:copy><br/>
 * &lt;fx:script><br/>
 * &lt;fx:define><br/>
 *
 * and also comments
 *
 * @author ptreilhes
 *
 */
public class FXOMPropertyV extends FXOMPropertyC {

    private static GlueElement emptyGlue(GlueDocument document) {
        GlueElement e = new GlueElement(document, "", 0, false);
        e.setSynthetic(true);
        return e;
    }

    public FXOMPropertyV(FXOMDocument document, long index, List<FXOMObject> values) {
        super(document, new PropertyName(VIRTUAL_PREFIX + index), values, emptyGlue(document.getGlue()));
    }

    protected FXOMPropertyV(FXOMDocument document, PropertyName name) {
        super(document, name, List.of(), emptyGlue(document.getGlue()));
        assert name.getName().startsWith(VIRTUAL_PREFIX);
    }

}
