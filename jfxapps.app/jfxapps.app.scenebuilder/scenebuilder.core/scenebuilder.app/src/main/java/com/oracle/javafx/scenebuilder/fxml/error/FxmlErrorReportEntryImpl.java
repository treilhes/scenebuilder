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
package com.oracle.javafx.scenebuilder.fxml.error;

import java.net.URL;

import com.gluonhq.jfxapps.core.api.error.ErrorReportEntry;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;

/**
 *
 *
 */
public class FxmlErrorReportEntryImpl implements ErrorReportEntry {

    private final SbFXOMObjectMask.Factory designHierarchyMaskFactory;

    private final I18N i18n;
    private final FXOMNode fxomNode;
    private final Type type;
    private URL linkedResourceUrl;
    private final CSSParsingReportImpl cssParsingReport; // relevant for INVALID_CSS_CONTENT

    public FxmlErrorReportEntryImpl(I18N i18n, FXOMNode fxomNode, Type type, CSSParsingReportImpl cssParsingReport,
            SbFXOMObjectMask.Factory designHierarchyMaskFactory) {
        assert fxomNode != null;
        assert (type == Type.INVALID_CSS_CONTENT) == (cssParsingReport != null);

        this.i18n = i18n;
        this.fxomNode = fxomNode;
        this.type = type;
        this.cssParsingReport = cssParsingReport;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
    }

    public FxmlErrorReportEntryImpl(I18N i18n, FXOMNode fxomNode, Type type,
            SbFXOMObjectMask.Factory designHierarchyMaskFactory) {
        this(i18n, fxomNode, type, null, designHierarchyMaskFactory);
    }

    @Override
    public FXOMNode getFxomNode() {
        return fxomNode;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public URL getLinkedResource() {
        return linkedResourceUrl;
    }

    @Override
    public String getText() {

        final StringBuilder result = new StringBuilder();

        final FXOMNode fxomNode = getFxomNode();

        if (getType() == Type.INVALID_CSS_CONTENT) {
            assert cssParsingReport != null;
            result.append(cssParsingReport.asString(5, "\n", "...")); // NOCHECK
        } else {
            result.append(i18n.get(getType().getI18nKey()));
        }

        result.append(" "); // NOCHECK
        if (fxomNode instanceof FXOMPropertyT) {
            final FXOMPropertyT fxomProperty = (FXOMPropertyT) fxomNode;
            result.append(fxomProperty.getValue());
        } else if (fxomNode instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomNode;
            result.append(fxomIntrinsic.getSource());
        } else if (fxomNode instanceof FXOMObject) {
            final FXOMObject fxomObject = (FXOMObject) fxomNode;
            final var mask = designHierarchyMaskFactory.getMask(fxomObject);
            // TODO check if an accessory, maybe the main one must be passed here
            result.append(mask.getClassNameInfo(null));
        }

        return result.toString();
    }

    /*
     * Object
     */

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getClass().getSimpleName());
        result.append("(fxomNode="); // NOCHECK
        result.append(fxomNode.getClass().getSimpleName());
        result.append(",type="); // NOCHECK
        result.append(type.getI18nKey());
        switch (type) {
        case UNRESOLVED_CLASS:
            break;
        case UNRESOLVED_LOCATION:
            result.append(",location="); // NOCHECK
            break;
        case UNRESOLVED_RESOURCE:
            result.append(",resource="); // NOCHECK
            break;
        case INVALID_CSS_CONTENT:
            result.append(",css file="); // NOCHECK
            break;
        case UNSUPPORTED_EXPRESSION:
            result.append(",expression="); // NOCHECK
            break;
        }
        if (fxomNode instanceof FXOMPropertyT) {
            final FXOMPropertyT fxomProperty = (FXOMPropertyT) fxomNode;
            result.append(fxomProperty.getValue());
        } else if (fxomNode instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomNode;
            result.append(fxomIntrinsic.getSource());
        } else {
            result.append("?"); // NOCHECK
        }
        result.append(")"); // NOCHECK

        return result.toString();
    }

}
