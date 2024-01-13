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
package com.oracle.javafx.scenebuilder.core.error.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.oracle.javafx.scenebuilder.api.error.ErrorCollector;
import com.oracle.javafx.scenebuilder.api.error.ErrorReport;
import com.oracle.javafx.scenebuilder.api.error.ErrorReportEntry;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

/**
 *
 *
 */
@ApplicationInstanceSingleton
public class ErrorReportImpl implements ErrorReport {

    private final Map<FXOMObject, List<ErrorReportEntry>> documentErrors = new HashMap<>();
    private final DocumentManager documentManager;

    private boolean dirty = true;

    private final Optional<List<ErrorCollector>> errorCollectors;

    public ErrorReportImpl(
            DocumentManager documentManager,
            Optional<List<ErrorCollector>> errorCollectors) {
        super();
        this.documentManager = documentManager;
        this.errorCollectors = errorCollectors;

        this.documentManager.fxomDocument().subscribe(fd -> forget());
        this.errorCollectors.ifPresent(l -> l.forEach(ec -> ec.setErrorReport(this)));
    }

    @Override
    public void forget() {
        this.documentErrors.clear();
        this.dirty = true;
    }

    @Override
    public <T> List<ErrorReportEntry> query(T fxomObject, InternalItemsCollector<T> internalCollector) {
        final List<ErrorReportEntry> result;

        updateReport();

        final List<ErrorReportEntry> collected = new ArrayList<>();

        if (documentErrors.get(fxomObject) != null) {
            collected.addAll(documentErrors.get(fxomObject));
        }

        if (internalCollector != null) {
            List<Object> internals = internalCollector.collectInternals(fxomObject);

            if (internals != null) {
                internals.forEach(i -> {
                    List<ErrorReportEntry> internalErrors = documentErrors.get(i);
                    if (internalErrors != null) {
                        collected.addAll(internalErrors);
                    }
                });
            }
        }

        if (collected.isEmpty()) {
            result = null;
        } else {
            result = collected;
        }

        assert (result == null) || (result.size() >= 1);

        return result;
    }

    @Override
    public <T> List<ErrorReportEntry> queryRecursive(T fxomObject, InternalItemsCollector<T> internalCollector, ChildrenCollector<T> childrenCollector) {

        final List<ErrorReportEntry> result;

        updateReport();

        final List<ErrorReportEntry> collected = new ArrayList<>();

        List<ErrorReportEntry> localErrors = query(fxomObject, internalCollector);

        if (localErrors != null) {
            collected.addAll(localErrors);
        }

        if (childrenCollector != null) {
            List<T> children = childrenCollector.collectChildren(fxomObject);

            if (children != null) {
                children.forEach(c -> {
                    List<ErrorReportEntry> childErrors = queryRecursive(c, internalCollector, childrenCollector);

                    if (childErrors != null) {
                        collected.addAll(childErrors);
                    }
                });
            }
        }

        if (collected.isEmpty()) {
            result = null;
        } else {
            result = collected;
        }

        assert (result == null) || (result.size() >= 1);

        return result;
    }

    @Override
    public Map<Object, List<ErrorReportEntry>> getEntries() {
        updateReport();
        return Collections.unmodifiableMap(documentErrors);
    }

    /*
     * Private
     */

    private void updateReport() {
        if (dirty) {
            assert documentErrors.isEmpty();
            if (documentManager.fxomDocument().get() != null && errorCollectors != null) {
                errorCollectors.ifPresent(l -> l.stream().map(dec -> dec.collect()).forEach(this::processCollectedErrors));
            }
            dirty = false;
        }
    }

    private void processCollectedErrors(ErrorCollector.ErrorCollectorResult result) {
        documentErrors.putAll(result.getDocumentErrors());
    }

    @Override
    public void fileDidChange(Path target) {
        // TODO Implement me to replace function like cssFileDidChange
        // FIXME Implement me to replace function like cssFileDidChange
        throw new UnsupportedOperationException("Implement me ASAP!");
    }

}
