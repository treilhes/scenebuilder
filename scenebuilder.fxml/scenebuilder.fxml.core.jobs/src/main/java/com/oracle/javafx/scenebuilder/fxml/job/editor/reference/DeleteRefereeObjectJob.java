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

package com.oracle.javafx.scenebuilder.fxml.job.editor.reference;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.fxml.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemoveNodeJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemoveObjectJob;

/**
 * This Job updates the FXOM document at execution time. Delete an
 * {@link FXOMObject} if there is no reference to it. If some reference exists
 * the {@link FXOMObject} is moved in place of the first found reference If weak
 * refrence found, they are deleted
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteRefereeObjectJob extends InlineDocumentJob {

    private final FXOMDocument fxomDocument;
    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final RemoveNodeJob.Factory removeNodeJobFactory;
    private final CombineReferenceJob.Factory combineReferenceJobFactory;

    private FXOMObject target;

    // @formatter:off
    public DeleteRefereeObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            RemoveObjectJob.Factory removeObjectJobFactory,
            RemoveNodeJob.Factory removeNodeJobFactory,
            CombineReferenceJob.Factory combineReferenceJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.removeNodeJobFactory = removeNodeJobFactory;
        this.combineReferenceJobFactory = combineReferenceJobFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;

    }

    protected void setJobParameters(FXOMObject target) {
        assert target != null;
        this.target = target;
    }

    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> executedJobs = new LinkedList<>();

        final FXOMNode node = prepareDeleteObject(executedJobs, target, target);

        if (node == target) {
            final AbstractJob removeJob = removeObjectJobFactory.getJob(target);
            removeJob.execute();
            executedJobs.add(removeJob);
        }

        return executedJobs;
    }

    /*
     * Private
     */

    private FXOMNode prepareDeleteObject(List<AbstractJob> executedJobs, FXOMObject node, FXOMObject target) {
        final FXOMNode result;

        final String nodeFxId = node.getFxId();
        if (nodeFxId == null) {
            // node has no fx:id : it can be deleted safely
            result = node;
        } else {
            final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
            final List<FXOMNode> references = fxomRoot.collectReferences(nodeFxId, target);
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
                        final AbstractJob clearJob = removeNodeJobFactory.getJob(r);
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
                    final AbstractJob combineJob = combineReferenceJobFactory.getJob(firstReference);
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<DeleteRefereeObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link DeleteRefereeObjectJob} job
         *
         * @param target the object to delete
         * @return the job to execute
         */
        public DeleteRefereeObjectJob getJob(FXOMObject target) {
            return create(DeleteRefereeObjectJob.class, j -> j.setJobParameters(target));
        }
    }

}
