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
package com.oracle.javafx.scenebuilder.fxml.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

/**
 * This job set the property defined by the provided {@link ValuePropertyMetadata}<br/>
 * with the provided "value" on each object selected if the property is available for the object
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ModifySelectionJob extends BatchDocumentJob {

    private static Logger logger = LoggerFactory.getLogger(ModifySelectionJob.class);

    private final Selection selection;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

    protected ValuePropertyMetadata propertyMetadata;
    protected Object newValue;

    // @formatter:off
    protected ModifySelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

    protected void setJobParameters(ValuePropertyMetadata propertyMetadata, Object newValue) {
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();
        final Set<FXOMInstance> candidates = new HashSet<>();


        if (selection.getGroup() != null) {
            for (FXOMObject fxomObject : selection.getGroup().getItems()) {
                handleFxomInstance(fxomObject, candidates);
                handleFxomIntrinsic(fxomObject, candidates);
            }
        } else {
            logger.warn("selection.getGroup() is null");
        }

        // Add ModifyObject jobs
        for (FXOMInstance fxomInstance : candidates) {
            final AbstractJob subJob = modifyObjectJobFactory.getJob(null, fxomInstance, propertyMetadata, newValue);

            if (subJob.isExecutable()) {
                result.add(subJob);
            }
        }
        return result;
    }

//    private void handleObjectSelectionGroup(AbstractSelectionGroup group, Set<FXOMInstance> candidates) {
//        final ObjectSelectionGroup osg = (ObjectSelectionGroup) group;
//        for (FXOMObject fxomObject : osg.getItems()) {
//            handleFxomInstance(fxomObject, candidates);
//            handleFxomIntrinsic(fxomObject, candidates);
//        }
//    }


    private void handleFxomInstance(FXOMObject fxomObject, Set<FXOMInstance> candidates) {
        if (fxomObject instanceof FXOMInstance) {
            candidates.add((FXOMInstance) fxomObject);
        }

    }

    private void handleFxomIntrinsic(FXOMObject fxomObject, Set<FXOMInstance> candidates) {
        if(fxomObject instanceof FXOMIntrinsic) {
            FXOMIntrinsic intrinsic = (FXOMIntrinsic) fxomObject;
            FXOMInstance fxomInstance = intrinsic.createFxomInstanceFromIntrinsic();
            candidates.add(fxomInstance);
        }
    }


//    private void handleGridSelectionGroup(AbstractSelectionGroup group, Set<FXOMInstance> candidates) {
//        final GridSelectionGroup gsg = (GridSelectionGroup) group;
//        final GridPaneHierarchyMask mask = gridMaskFactory.getMask(gsg.getAncestor());
//        for (int index : gsg.getIndexes()) {
//            FXOMObject constraints = null;
//            switch (gsg.getType()) {
//                case COLUMN:
//                    constraints = mask.getColumnConstraintsAtIndex(index);
//                    break;
//                case ROW:
//                    constraints = mask.getRowConstraintsAtIndex(index);
//                    break;
//                default:
//                    assert false;
//                    break;
//            }
//
//            assert constraints instanceof FXOMInstance;
//            candidates.add((FXOMInstance) constraints);
//        }
//    }

    @Override
    protected String makeDescription() {
        final String result;
        final List<AbstractJob> subJobs = getSubJobs();
        final int subJobCount = subJobs.size();

        switch (subJobCount) {
            case 0:
                result = "Unexecutable Set"; //NOCHECK
                break;
            case 1: // Single selection
                result = subJobs.get(0).getDescription();
                break;
            default:
                result = I18N.getString("label.action.edit.set.n",
                        propertyMetadata.getName().toString(),
                        subJobCount);
                break;
        }

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<ModifySelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  ModifySelectionJob} job
         * @param propertyMetadata the definition of property to set
         * @param newValue the new value of the property to set
         * @return the job to execute
         */
        public ModifySelectionJob getJob(ValuePropertyMetadata propertyMetadata, Object newValue) {
            return create(ModifySelectionJob.class, j -> j.setJobParameters(propertyMetadata, newValue));
        }
    }
}
