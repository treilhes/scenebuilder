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

package com.gluonhq.jfxapps.core.job.editor.reference;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.FXOMVirtual;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;
import com.gluonhq.jfxapps.core.fxom.util.JavaLanguage;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 * This job look for all reference in an {@link FXOMDocument} and for each
 * reference r: <br/>
 * r is a forward reference<br/>
 * 0) r is a toggleGroup reference<br/>
 * => if toggle group exists, we swap it with the reference<br/>
 * => if not, replace the reference by a new toggle group<br/>
 * 1) r is a weak reference (like labelFor)<br/>
 * => we remove the reference<br/>
 * 2) else r is a strong reference<br/>
 * => we expand the reference<br/>
 */
@Singleton
public final class ReferencesUpdaterJob extends InlineDocumentJob {

    private final FXOMDocument fxomDocument;
    private final Set<String> declaredFxIds = new HashSet<>();
    private final FXOMCloner cloner;

    private final FxomJobsFactory fxomJobsFactory;

    // @formatter:off
    public ReferencesUpdaterJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomJobsFactory = fxomJobsFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        this.cloner = new FXOMCloner(this.fxomDocument);

    }

    public void setJobParameters() {
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> executedJobs = new LinkedList<>();

        if (fxomDocument.getFxomRoot() != null) {
            declaredFxIds.clear();
            update(fxomDocument.getFxomRoot(), executedJobs);
        }

        return executedJobs;
    }

    /*
     * Private
     */

    private void update(FXOMNode node, List<Job> jobCollector) {
        if (node instanceof FXOMCollection) {
            updateCollection((FXOMCollection) node, jobCollector);
        } else if (node instanceof FXOMInstance) {
            updateInstance((FXOMInstance) node, jobCollector);
        } else if (node instanceof FXOMIntrinsic) {
            updateIntrinsic((FXOMIntrinsic) node, jobCollector);
        } else if (node instanceof FXOMVirtual) {
            updateVirtual((FXOMVirtual) node, jobCollector);
        } else if (node instanceof FXOMPropertyC) {
            updatePropertyC((FXOMPropertyC) node, jobCollector);
        } else if (node instanceof FXOMPropertyT) {
            updatePropertyT((FXOMPropertyT) node, jobCollector);
        } else {
            throw new RuntimeException("Bug"); // NOCHECK
        }
    }

    private void updateCollection(FXOMCollection collection, List<Job> jobCollector) {
        if (collection.getFxId() != null) {
            declaredFxIds.add(collection.getFxId());
        }
        final List<FXOMObject> items = collection.getItems();
        for (int i = 0, count = items.size(); i < count; i++) {
            update(items.get(i), jobCollector);
        }
    }

    private void updateInstance(FXOMInstance instance, List<Job> jobCollector) {
        if (instance.getFxId() != null) {
            declaredFxIds.add(instance.getFxId());
        }
        final Map<PropertyName, FXOMProperty> properties = instance.getProperties();
        final List<PropertyName> names = new LinkedList<>(properties.keySet());
        for (PropertyName propertyName : names) {
            update(properties.get(propertyName), jobCollector);
        }
    }

    private void updateVirtual(FXOMVirtual instance, List<Job> jobCollector) {
        if (instance.getFxId() != null) {
            declaredFxIds.add(instance.getFxId());
        }
        final Map<PropertyName, FXOMProperty> properties = instance.getProperties();
        final List<PropertyName> names = new LinkedList<>(properties.keySet());
        for (PropertyName propertyName : names) {
            update(properties.get(propertyName), jobCollector);
        }
    }

    private void updateIntrinsic(FXOMIntrinsic intrinsic, List<Job> jobCollector) {
        switch (intrinsic.getType()) {
        case FX_REFERENCE:
        case FX_COPY:
            updateReference(intrinsic, intrinsic.getSource(), jobCollector);
            break;
        default:
            break;
        }
    }

    private void updatePropertyC(FXOMPropertyC property, List<Job> jobCollector) {
        final List<FXOMObject> values = property.getChildren();
        for (int i = 0, count = values.size(); i < count; i++) {
            update(values.get(i), jobCollector);
        }
    }

    private void updatePropertyT(FXOMPropertyT property, List<Job> jobCollector) {
        final PrefixedValue pv = new PrefixedValue(property.getValue());
        if (pv.isExpression()) {
            final String suffix = pv.getSuffix();
            if (JavaLanguage.isIdentifier(suffix)) {
                updateReference(property, suffix, jobCollector);
            }
        }
    }

    private void updateReference(FXOMNode r, String fxId, List<Job> jobCollector) {
        assert (r instanceof FXOMPropertyT) || (r instanceof FXOMIntrinsic);
        assert fxId != null;

        if (declaredFxIds.contains(fxId) == false) {
            // r is a forward reference
            //
            // 0) r is a toggleGroup reference
            // => if toggle group exists, we swap it with the reference
            // => if not, replace the reference by a new toggle group
            // 1) r is a weak reference (like labelFor)
            // => we remove the reference
            // 2) else r is a strong reference
            // => we expand the reference

            final FXOMObject declarer = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);

            // 0)
            if (FXOMNodes.isToggleGroupReference(r)) {
                final Job fixJob = fxomJobsFactory.fixToggleGroupReference(r);
                fixJob.execute();
                jobCollector.add(fixJob);
                declaredFxIds.add(fxId);
            }

            // 1
            else if (FXOMNodes.isWeakReference(r)) {
                final Job removeJob = fxomJobsFactory.removeNode(r);
                removeJob.execute();
                jobCollector.add(removeJob);
            }
            else if (declarer == null) {

                // TODO maybe add error in errorReport here
                // 2)
            } else {

                final Job expandJob = fxomJobsFactory.expandReference(r, cloner);
                expandJob.execute();
                jobCollector.add(expandJob);
            }
        }
    }

    @Override
    protected String makeDescription() {
        return this.getClass().getName();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
