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

package com.oracle.javafx.scenebuilder.job.editor.reference;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCloner;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMVirtual;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveNodeJob;

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
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ReferencesUpdaterJob extends InlineDocumentJob {

    private final FXOMDocument fxomDocument;
    private final Set<String> declaredFxIds = new HashSet<>();
    private final FXOMCloner cloner;

    private final FixToggleGroupReferenceJob.Factory fixToggleGroupReferenceJobFactory;
    private final RemoveNodeJob.Factory removeNodeJobFactory;
    private final ExpandReferenceJob.Factory expandReferenceJobFactory;

    // @formatter:off
    public ReferencesUpdaterJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FixToggleGroupReferenceJob.Factory fixToggleGroupReferenceJobFactory,
            RemoveNodeJob.Factory removeNodeJobFactory,
            ExpandReferenceJob.Factory expandReferenceJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.fixToggleGroupReferenceJobFactory = fixToggleGroupReferenceJobFactory;
        this.removeNodeJobFactory = removeNodeJobFactory;
        this.expandReferenceJobFactory = expandReferenceJobFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        this.cloner = new FXOMCloner(this.fxomDocument);

    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> executedJobs = new LinkedList<>();

        if (fxomDocument.getFxomRoot() != null) {
            declaredFxIds.clear();
            update(fxomDocument.getFxomRoot(), executedJobs);
        }

        return executedJobs;
    }

    /*
     * Private
     */

    private void update(FXOMNode node, List<AbstractJob> jobCollector) {
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

    private void updateCollection(FXOMCollection collection, List<AbstractJob> jobCollector) {
        if (collection.getFxId() != null) {
            declaredFxIds.add(collection.getFxId());
        }
        final List<FXOMObject> items = collection.getItems();
        for (int i = 0, count = items.size(); i < count; i++) {
            update(items.get(i), jobCollector);
        }
    }

    private void updateInstance(FXOMInstance instance, List<AbstractJob> jobCollector) {
        if (instance.getFxId() != null) {
            declaredFxIds.add(instance.getFxId());
        }
        final Map<PropertyName, FXOMProperty> properties = instance.getProperties();
        final List<PropertyName> names = new LinkedList<>(properties.keySet());
        for (PropertyName propertyName : names) {
            update(properties.get(propertyName), jobCollector);
        }
    }

    private void updateVirtual(FXOMVirtual instance, List<AbstractJob> jobCollector) {
        if (instance.getFxId() != null) {
            declaredFxIds.add(instance.getFxId());
        }
        final Map<PropertyName, FXOMProperty> properties = instance.getProperties();
        final List<PropertyName> names = new LinkedList<>(properties.keySet());
        for (PropertyName propertyName : names) {
            update(properties.get(propertyName), jobCollector);
        }
    }

    private void updateIntrinsic(FXOMIntrinsic intrinsic, List<AbstractJob> jobCollector) {
        switch (intrinsic.getType()) {
        case FX_REFERENCE:
        case FX_COPY:
            updateReference(intrinsic, intrinsic.getSource(), jobCollector);
            break;
        default:
            break;
        }
    }

    private void updatePropertyC(FXOMPropertyC property, List<AbstractJob> jobCollector) {
        final List<FXOMObject> values = property.getChildren();
        for (int i = 0, count = values.size(); i < count; i++) {
            update(values.get(i), jobCollector);
        }
    }

    private void updatePropertyT(FXOMPropertyT property, List<AbstractJob> jobCollector) {
        final PrefixedValue pv = new PrefixedValue(property.getValue());
        if (pv.isExpression()) {
            final String suffix = pv.getSuffix();
            if (JavaLanguage.isIdentifier(suffix)) {
                updateReference(property, suffix, jobCollector);
            }
        }
    }

    private void updateReference(FXOMNode r, String fxId, List<AbstractJob> jobCollector) {
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

            final FXOMObject declarer = fxomDocument.searchWithFxId(fxId);

            // 0)
            if (FXOMNodes.isToggleGroupReference(r)) {
                final AbstractJob fixJob = fixToggleGroupReferenceJobFactory.getJob(r);
                fixJob.execute();
                jobCollector.add(fixJob);
                declaredFxIds.add(fxId);
            }

            // 1
            else if (FXOMNodes.isWeakReference(r)) {
                final AbstractJob removeJob = removeNodeJobFactory.getJob(r);
                removeJob.execute();
                jobCollector.add(removeJob);
            }
            else if (declarer == null) {

                // TODO maybe add error in errorReport here
                // 2)
            } else {

                final AbstractJob expandJob = expandReferenceJobFactory.getJob(r, cloner);
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<ReferencesUpdaterJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ReferencesUpdaterJob} job
         *
         * @return the job to execute
         */
        public ReferencesUpdaterJob getJob() {
            return create(ReferencesUpdaterJob.class, j -> j.setJobParameters());
        }
    }
}
