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

import java.util.LinkedList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.collector.FxReferenceCollector;

/**
 * This Job updates the FXOM document at execution time. Delete an
 * {@link FXOMObject} if there is no reference to it. If some reference exists
 * the {@link FXOMObject} is moved in place of the first found reference If weak
 * references are found, they are deleted
 */
@Prototype
public final class DeleteRefereeObjectJob extends InlineDocumentJob {

    private final FXOMDocument fxomDocument;
    private final FxomJobsFactory fxomJobsFactory;

    private FXOMObject target;

    // @formatter:off
    public DeleteRefereeObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomJobsFactory = fxomJobsFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;

    }

    public void setJobParameters(FXOMObject target) {
        assert target != null;
        this.target = target;
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> executedJobs = new LinkedList<>();

        final FXOMNode node = prepareDeleteObject(executedJobs, target, target);

        if (node == target) {
            final Job removeJob = fxomJobsFactory.removeObject(target);
            removeJob.execute();
            executedJobs.add(removeJob);
        }

        return executedJobs;
    }

    /*
     * Private
     */

    private FXOMNode prepareDeleteObject(List<Job> executedJobs, FXOMObject node, FXOMObject target) {
        final FXOMNode result;

        final String nodeFxId = node.getFxId();
        if (nodeFxId == null) {
            // node has no fx:id : it can be deleted safely
            result = node;
        } else {
            final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
            final List<FXOMNode> references = fxomRoot.collect(FxReferenceCollector.referenceById(nodeFxId, target));
            if (references.isEmpty()) {
                // node has an fx:id but this one is not referenced
                // outside of the delete target : it can be deleted safely
                result = node;
            } else {
                // node has an fx:id referenced outside of the delete target
                // => we find the first strong reference R to it
                // => we remove all the weak references between node and R
                // => we combine node with R
                FXOMNode firstReference = null;
                for (FXOMNode r : references) {
                    if (FXOMNodes.isWeakReference(r)) {
                        // This weak reference will become a forward reference
                        // after the deletion => we remove it.
                        final Job clearJob = fxomJobsFactory.removeNode(r);
                        clearJob.execute();
                        executedJobs.add(clearJob);
                    } else {
                        firstReference = r;
                        break;
                    }
                }

                if (firstReference == null) {
                    // node has only weak references ; those references have
                    // been removed => node can be delete safely
                    result = node;
                } else {
                    // we combine firstReference with node ie node is
                    // disconnected from its parent and put in place of
                    // firstReference
                    final Job combineJob = fxomJobsFactory.combineReference(firstReference);
                    combineJob.execute();
                    executedJobs.add(combineJob);
                    result = null;
                }
            }
        }

        if (result == node) {
            if (node instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) node;
                for (FXOMProperty p : new LinkedList<>(fxomInstance.getProperties().values())) {
                    if (p instanceof FXOMPropertyC) {
                        final FXOMPropertyC cp = (FXOMPropertyC) p;
                        for (FXOMObject value : new LinkedList<>(cp.getChildren())) {
                            prepareDeleteObject(executedJobs, value, target);
                        }
                    }
                }
            } else if (result instanceof FXOMCollection) {
                final FXOMCollection fxomCollection = (FXOMCollection) result;
                for (FXOMObject i : new LinkedList<>(fxomCollection.getItems())) {
                    prepareDeleteObject(executedJobs, i, target);
                }
            } // else no prework needed
        }

        return result;
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
