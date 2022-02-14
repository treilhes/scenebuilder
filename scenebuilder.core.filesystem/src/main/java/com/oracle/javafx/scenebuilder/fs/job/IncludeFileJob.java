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
package com.oracle.javafx.scenebuilder.fs.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.util.URLUtils;

/**
 * Cannot include in non saved document<br/>
 * Cannot include same file as document one which will create cyclic reference<br/>
 * Cannot include as root <br/>
 * Link the provided fxml {@link File} using an fx:include<br/>
 * We insert the new object under the common parent of the selected objects or under root if nothing is selected
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class IncludeFileJob extends BatchSelectionJob {

    private File file;
    private FXOMObject targetObject;
    private FXOMIntrinsic newInclude;
    private final FXOMDocument fxomDocument;
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

 // @formatter:off
    protected IncludeFileJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters(File file) {
        assert file != null;
        this.file = file;
    }

    public FXOMObject getTargetObject() {
        return targetObject;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        try {
            final FXOMDocument targetDocument = fxomDocument;
            final URL documentURL = fxomDocument.getLocation();
            final URL fileURL = file.toURI().toURL();

            // Cannot include in non saved document
            // Cannot include same file as document one which will create cyclic reference
            if (documentURL != null && URLUtils.equals(documentURL, fileURL) == false) {
                newInclude = FXOMNodes.newInclude(targetDocument, file);

                // newInclude is null when file is empty
                if (newInclude != null) {

                    // Cannot include as root
                    final FXOMObject rootObject = targetDocument.getFxomRoot();
                    if (rootObject != null) {
                        // We include the new object under the common parent
                        // of the selected objects.
                        final Selection selection = getSelection();
                        if (selection.isEmpty() || selection.isSelected(rootObject)) {
                            // No selection or root is selected -> we insert below root
                            targetObject = rootObject;
                        } else {
                            // Let's use the common parent of the selected objects.
                            // It might be null if selection holds some non FXOMObject entries
                            targetObject = selection.getAncestor();
                        }
                        // Build InsertAsSubComponent jobs
                        final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);
                        if (targetMask.isAcceptingSubComponent(newInclude)) {
                            result.add(insertAsSubComponentJobFactory.getJob(newInclude,targetObject,targetMask.getSubComponentCount()));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            result.clear();
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return I18N.getString("include.file", file.getName());
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final List<FXOMObject> fxomObjects = new ArrayList<>();
        fxomObjects.add(newInclude);
        return objectSelectionGroupFactory.getGroup(fxomObjects, newInclude, null);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends JobFactory<IncludeFileJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link IncludeFileJob} job
         *
         * @param file the file to include
         * @return the job to execute
         */
        public IncludeFileJob getJob(File file) {
            return create(IncludeFileJob.class, j -> j.setJobParameters(file));
        }
    }
}
