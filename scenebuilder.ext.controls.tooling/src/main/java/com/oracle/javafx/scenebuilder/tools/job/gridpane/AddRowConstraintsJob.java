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
package com.oracle.javafx.scenebuilder.tools.job.gridpane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.metadata.javafx.hidden.RowConstraintsMetadata;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

import javafx.scene.layout.RowConstraints;

/**
 * Job invoked when adding row constraints.
 * It adds a row constraint to each provided GridPane
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class AddRowConstraintsJob extends BatchDocumentJob {

    /**
     * If the selected column is associated to an existing constraints,
     * we duplicate the existing constraints.
     * Otherwise, we use this constant as minHeight default value
     */
    private static final double defaultMinHeight = 10.0;
    /**
     * If the selected column is associated to an existing constraints,
     * we duplicate the existing constraints.
     * Otherwise, we use this constant as prefHeight default value
     */
    private static final double defaultPrefHeight = 30.0;

    private final FXOMDocument fxomDocument;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final AddPropertyJob.Factory addPropertyJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    /**
     * Key = target GridPane instance
     * Value = list of target row indexes for this GridPane
     */
    private final Map<FXOMObject, Set<Integer>> targetGridPanes = new HashMap<>();
    private Position position;


    protected AddRowConstraintsJob(SceneBuilderBeanFactory context,
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.addPropertyJobFactory = addPropertyJobFactory;
        this.maskFactory = maskFactory;
    }

    protected void setJobParameters(final Position position, final Map<FXOMObject, Set<Integer>> targetGridPanes) {
        this.position = position;
        this.targetGridPanes.putAll(targetGridPanes);
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        // Add column constraints job
        assert targetGridPanes.isEmpty() == false;
        for (FXOMObject targetGridPane : targetGridPanes.keySet()) {
            assert targetGridPane instanceof FXOMInstance;
            final Set<Integer> targetIndexes = targetGridPanes.get(targetGridPane);
            result.addAll(addRowConstraints((FXOMInstance) targetGridPane, targetIndexes));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Add Row Constraints"; //NOCHECK
    }

    private Set<AbstractJob> addRowConstraints(
            final FXOMInstance targetGridPane,
            final Set<Integer> targetIndexes) {

        final Set<AbstractJob> result = new LinkedHashSet<>();

        // Retrieve the constraints property for the specified target GridPane
        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOCHECK
        FXOMProperty constraintsProperty = targetGridPane.getProperties().get(propertyName);
        if (constraintsProperty == null) {
            constraintsProperty = new FXOMPropertyC(fxomDocument, propertyName);
        }
        assert constraintsProperty instanceof FXOMPropertyC;

        final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);

        int shiftIndex = 0;
        int constraintsSize = mask.getRowsConstraintsSize();
        for (int targetIndex : targetIndexes) {

            // Retrieve the index for the new constraints to be added
            int addedIndex = targetIndex + shiftIndex;
            if (position == Position.BELOW) {
                addedIndex++;
            }

            final FXOMObject targetConstraints
                    = mask.getRowConstraintsAtIndex(targetIndex);
            // The target index is associated to an existing constraints value :
            // we add a new constraints using the values of the existing one
            if (targetConstraints != null) {
                assert targetConstraints instanceof FXOMInstance;
                // Create new constraints instance with same values as the target one
                final FXOMInstance addedConstraints = makeRowConstraintsInstance(
                        (FXOMInstance) targetConstraints);

                final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
                        (FXOMPropertyC) constraintsProperty, addedIndex);
                result.add(addValueJob);
            } //
            // The target index is not associated to an existing constraints value :
            // - we add new empty constraints from the last existing one to the added index (excluded)
            // - we add a new constraints with default values for the added index
            else {
                for (int index = constraintsSize; index < addedIndex; index++) {
                    // Create new empty constraints for the exisiting rows
                    final FXOMInstance addedConstraints = makeRowConstraintsInstance();
                    final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
                            (FXOMPropertyC) constraintsProperty, index);
                    result.add(addValueJob);
                }
                // Create new constraints with default values for the new added row
                final FXOMInstance addedConstraints = makeRowConstraintsInstance();

                RowConstraintsMetadata.minHeightPropertyMetadata.setValue(addedConstraints, defaultMinHeight);
                RowConstraintsMetadata.prefHeightPropertyMetadata.setValue(addedConstraints, defaultPrefHeight);

                final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
                        (FXOMPropertyC) constraintsProperty, addedIndex);
                result.add(addValueJob);
                constraintsSize = addedIndex + 1;
            }
            shiftIndex++;
        }

        // Add the constraints property to the target GridPane if not already there.
        // IMPORTANT :
        // Note that the AddPropertyJob must be called after the AddPropertyValueJob.
        if (constraintsProperty.getParentInstance() == null) {
            final AbstractJob addPropertyJob = addPropertyJobFactory.getJob(constraintsProperty, targetGridPane, -1);
            result.add(addPropertyJob);
        }

        return result;
    }

    private FXOMInstance makeRowConstraintsInstance() {

        // Create new constraints instance
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result
                = new FXOMInstance(newDocument, RowConstraints.class);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(fxomDocument);

        return result;
    }

    private FXOMInstance makeRowConstraintsInstance(final FXOMInstance constraints) {

        assert constraints != null;
        assert constraints.getDeclaredClass() == RowConstraints.class;

        // Create new constraints instance
        final FXOMInstance result = makeRowConstraintsInstance();

        // Set the new row constraints values with the values of the specified instance
        final boolean fillHeight = RowConstraintsMetadata.fillHeightPropertyMetadata.getValue(constraints);
        final double maxHeight = RowConstraintsMetadata.maxHeightPropertyMetadata.getValue(constraints);
        final double minHeight = RowConstraintsMetadata.minHeightPropertyMetadata.getValue(constraints);
        final double percentHeight = RowConstraintsMetadata.percentHeightPropertyMetadata.getValue(constraints);
        final double prefHeight = RowConstraintsMetadata.prefHeightPropertyMetadata.getValue(constraints);
        final String valignment = RowConstraintsMetadata.valignmentPropertyMetadata.getValue(constraints);
        final String vgrow = RowConstraintsMetadata.vgrowPropertyMetadata.getValue(constraints);

        RowConstraintsMetadata.fillHeightPropertyMetadata.setValue(result, fillHeight);
        RowConstraintsMetadata.maxHeightPropertyMetadata.setValue(result, maxHeight);
        // If the existing constraints minHeight is too small, we use the default one
        RowConstraintsMetadata.minHeightPropertyMetadata.setValue(result, Math.max(minHeight, defaultMinHeight));
        RowConstraintsMetadata.percentHeightPropertyMetadata.setValue(result, percentHeight);
        // If the existing constraints prefHeight is too small, we use the default one
        RowConstraintsMetadata.prefHeightPropertyMetadata.setValue(result, Math.max(prefHeight, defaultPrefHeight));
        RowConstraintsMetadata.valignmentPropertyMetadata.setValue(result, valignment);
        RowConstraintsMetadata.vgrowPropertyMetadata.setValue(result, vgrow);

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<AddRowConstraintsJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link AddRowConstraintsJob} job
         * @param position the position relative to a row
         * @param targetGridPanes map and their targeted row indexes
         * @return the job to execute
         */
        public AddRowConstraintsJob getJob(final Position position, final Map<FXOMObject, Set<Integer>> targetGridPanes) {
            return create(AddRowConstraintsJob.class, j -> j.setJobParameters(position, targetGridPanes));
        }
    }
}
