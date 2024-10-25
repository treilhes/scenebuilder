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
package com.oracle.javafx.scenebuilder.app.error.collectors;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.error.AbstractErrorCollector;
import com.gluonhq.jfxapps.core.api.error.ErrorReportEntry;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.app.error.FxmlErrorReportEntryImpl;
import com.oracle.javafx.scenebuilder.app.error.Type;

@ApplicationInstanceSingleton
public class BindingExpressionCollector extends AbstractErrorCollector {

    private final I18N i18n;
    private final SbFXOMObjectMask.Factory designHierarchyMaskFactory;
    private final ApplicationInstanceEvents documentManager;

    public BindingExpressionCollector(
            I18N i18n,
            ApplicationInstanceEvents documentManager,
            SbFXOMObjectMask.Factory designHierarchyMaskFactory) {
        super();
        this.i18n = i18n;
        this.documentManager = documentManager;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
    }

    @Override
    public ErrorCollectorResult collect() {

        final ErrorCollectorResult result = new ErrorCollectorResult();

        for (FXOMPropertyT p : documentManager.fxomDocument().get().getFxomRoot().collect(PropertyCollector.allSimpleProperties())) {
            final PrefixedValue pv = new PrefixedValue(p.getValue());
            if (pv.isBindingExpression()) {
                final ErrorReportEntry newEntry = new FxmlErrorReportEntryImpl(i18n, p, Type.UNSUPPORTED_EXPRESSION,
                        designHierarchyMaskFactory);
                result.add(p, newEntry);
            }
        }

        return result;
    }

}