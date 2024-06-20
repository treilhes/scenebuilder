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
package com.oracle.javafx.scenebuilder.fxml.error.collectors;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.error.AbstractErrorCollector;
import com.gluonhq.jfxapps.core.api.error.ErrorReportEntry;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.fxml.error.FxmlErrorReportEntryImpl;
import com.oracle.javafx.scenebuilder.fxml.error.Type;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class BindingExpressionCollector extends AbstractErrorCollector {

    private final FXOMObjectMask.Factory designHierarchyMaskFactory;
    private final FxmlDocumentManager documentManager;

    public BindingExpressionCollector(FxmlDocumentManager documentManager,
            FXOMObjectMask.Factory designHierarchyMaskFactory) {
        super();
        this.documentManager = documentManager;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
    }

    @Override
    public ErrorCollectorResult collect() {

        final ErrorCollectorResult result = new ErrorCollectorResult();

        for (FXOMPropertyT p : documentManager.fxomDocument().get().getFxomRoot().collect(PropertyCollector.allSimpleProperties())) {
            final PrefixedValue pv = new PrefixedValue(p.getValue());
            if (pv.isBindingExpression()) {
                final ErrorReportEntry newEntry = new FxmlErrorReportEntryImpl(p, Type.UNSUPPORTED_EXPRESSION,
                        designHierarchyMaskFactory);
                result.add(p, newEntry);
            }
        }

        return result;
    }

}
