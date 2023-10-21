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

/**
 *
 *
 */
class TransientComment extends TransientVirtual {

    private String value;

    public TransientComment(
            TransientNode parentNode,
            GlueDocument glueDocument,
            GlueElement glueElement,
            long virtualIndex,
            String comment) {
        super(parentNode, glueElement, virtualIndex);

        assert comment != null;
        assert comment.equals(glueElement.getContentText());

        this.value = comment;
    }

    protected String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }

    @Override
    public FXOMObject makeFxomObject(FXOMDocument fxomDocument) {
        return new FXOMComment(fxomDocument, getGlueElement(), value);
    }

    @Override
    public FXOMProperty makeFxomProperty(FXOMDocument fxomDocument) {
        return new FXOMPropertyV(fxomDocument, getVirtualIndex(), List.of(makeFxomObject(fxomDocument)));
    }

//    @Override
//    public FXOMProperty makeFxomProperty(FXOMDocument document) {
//        return new FXOMPropertyT(document, name, value);
//    }



//    public List<FXOMObject> getValues() {
//        return values;
//    }
//
//    public List<FXOMProperty> getCollectedProperties() {
//        return collectedProperties;
//    }
//
//    public FXOMProperty makeFxomProperty(FXOMDocument fxomDocument) {
//        final FXOMProperty result;
//
//        if (collectedProperties.isEmpty()) {
//            /*
//             * Two cases:
//             *
//             * 1) (values.size() == 0)
//             *
//             *    => it's a textual property expressed as plain text
//             *    => for example with Button.text:
//             *    <Button><text>OK</text></Button>
//             *
//             * 2) (values.size() == 1) &&
//             *    (values.get(0).properties().size() == 0) &&
//             *    (values.get(0).getFxValue() != null
//             *
//             *    => it's a textual property expressed with fx:value
//             *    => we create an FXOMPropertyT instance
//             *    => for example with Button.text:
//             *
//             *    <Button><text><String fx:value="OK"/></text></Button>
//             *
//             *
//             * 3) else
//             *
//             *    => it's a complex property
//             *    => we create an FXOMPropertyC instance
//             */
//
//            if (values.isEmpty()) {
//                // Case #1
//                assert propertyElement.getChildren().isEmpty();
//                assert propertyElement.getContent().isEmpty() == false;
//                result = new FXOMPropertyT(fxomDocument, name,
//                        propertyElement, null, propertyElement.getContentText());
//            }
//            else if ((values.size() == 1) && (values.get(0) instanceof FXOMInstance)) {
//                final FXOMInstance value = (FXOMInstance) values.get(0);
//                final String fxValue = value.getFxValue();
//                if (fxValue != null) {
//                    // Case #2
//                    result = new FXOMPropertyT(fxomDocument, name,
//                            propertyElement, value.getGlueElement(), fxValue);
//                } else {
//                    // Case #3
//                    result = new FXOMPropertyC(fxomDocument, name,
//                            values, propertyElement);
//                }
//            } else {
//                // Case #3
//                result = new FXOMPropertyC(fxomDocument, name,
//                        values, propertyElement);
//            }
//        } else {
//            // It is a property of type Map ; currently we don't support
//            // map property editing ; so we create a fake value.
//            assert getSceneGraphObject() instanceof Map;
//            result = new FXOMPropertyT(fxomDocument, name, "fake-value");
//        }
//
//        return result;
//    }
}
