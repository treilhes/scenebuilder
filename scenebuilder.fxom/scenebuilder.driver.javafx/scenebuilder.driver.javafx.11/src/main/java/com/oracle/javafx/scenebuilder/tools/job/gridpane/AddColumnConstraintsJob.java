/*
 * Copyright (c) 2016, 2026, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2026, Pascal Treilhes and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.javafx.scene.layout.ColumnConstraintsMetadata;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.fxom.api.job.base.BatchDocumentJob;
import com.treilhes.jfxplace.fxom.api.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.treilhes.jfxplace.fxom.model.FXOMInstance;
import com.treilhes.jfxplace.fxom.model.FXOMObject;
import com.treilhes.jfxplace.fxom.model.FXOMProperty;
import com.treilhes.jfxplace.fxom.model.FXOMPropertyC;
import com.treilhes.jfxplace.fxom.model.util.PropertyName;

import javafx.scene.layout.ColumnConstraints;

/**
 * Job invoked when adding column constraints
 * It adds a column constraint to each provided GridPane.
 */
@ApplicationInstancePrototype
public final class AddColumnConstraintsJob extends BatchDocumentJob {

    /**
     * If the selected column is associated to an existing constraints,
     * we duplicate the existing constraints.
     * Otherwise, we use this constant as minWidth default value
     */
    private static final double defaultMinWidth = 10.0;
    /**
     * If the selected column is associated to an existing constraints,
     * we duplicate the existing constraints.
     * Otherwise, we use this constant as prefWidth default value
     */
    private static final double defaultPrefWidth = 100.0;

    private final FXOMDocument fxomDocument;
    private final SbMetadata metadata;
    private final FxomJobsFactory fxomJobsFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    /**
     * Key = target GridPane instance
     * Value = list of target column indexes for this GridPane
     */
    private final Map<FXOMObject, Set<Integer>> targetGridPanes = new HashMap<>();
    private Position position;


    protected AddColumnConstraintsJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
        this.maskFactory = maskFactory;
    }

    protected void setJobParameters(final Position position, final Map<FXOMObject, Set<Integer>> targetGridPanes) {
        this.position = position;
        this.targetGridPanes.putAll(targetGridPanes);
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();

        // Add column constraints job
        assert targetGridPanes.isEmpty() == false;
        for (var targetGridPane : targetGridPanes.keySet()) {
            assert targetGridPane instanceof FXOMInstance;
            final Set<Integer> targetIndexes = targetGridPanes.get(targetGridPane);
            result.addAll(addColumnConstraints((FXOMInstance) targetGridPane, targetIndexes));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Add Column Constraints"; //NOCHECK
    }

    private Set<Job> addColumnConstraints(
            final FXOMInstance targetGridPane,
            final Set<Integer> targetIndexes) {

        final Set<Job> result = new LinkedHashSet<>();

        // Retrieve the constraints property for the specified target GridPane
        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOCHECK
        FXOMProperty constraintsProperty = targetGridPane.getProperties().get(propertyName);
        if (constraintsProperty == null) {
            constraintsProperty = new FXOMPropertyC(fxomDocument, propertyName);
        }
        assert constraintsProperty instanceof FXOMPropertyC;

        final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);

        int shiftIndex = 0;
        int constraintsSize = mask.getColumnsConstraintsSize();
        for (int targetIndex : targetIndexes) {

            // Retrieve the index for the new constraints to be added
            int addedIndex = targetIndex + shiftIndex;
            if (position == Position.AFTER) {
                addedIndex++;
            }

            final FXOMObject targetConstraints
                    = mask.getColumnConstraintsAtIndex(targetIndex);
            // The target index is associated to an existing constraints value :
            // we add a new constraints using the values of the existing one
            if (targetConstraints != null) {
                assert targetConstraints instanceof FXOMInstance;
                // Create new constraints instance with same values as the target one
                final FXOMInstance addedConstraints = makeColumnConstraintsInstance((FXOMInstance) targetConstraints);

                final var addValueJob = fxomJobsFactory.addPropertyValue(addedConstraints,
                        (FXOMPropertyC) constraintsProperty, addedIndex);

                result.add(addValueJob);
            } //
            // The target index is not associated to an existing constraints value :
            // - we add new empty constraints from the last existing one to the added index (excluded)
            // - we add a new constraints with default values for the added index
            else {
                for (int index = constraintsSize; index < addedIndex; index++) {
                    // Create new empty constraints for the exisiting columns
                    final var addedConstraints = makeColumnConstraintsInstance();
                    final var addValueJob = fxomJobsFactory.addPropertyValue(addedConstraints,
                            (FXOMPropertyC) constraintsProperty, index);
                    result.add(addValueJob);
                }
                // Create new constraints with default values for the new added column
                final var addedConstraints = makeColumnConstraintsInstance();

                ColumnConstraintsMetadata.minWidthPropertyMetadata.setValue(addedConstraints, defaultMinWidth);
                ColumnConstraintsMetadata.prefWidthPropertyMetadata.setValue(addedConstraints, defaultPrefWidth);
                final var addValueJob = fxomJobsFactory.addPropertyValue(addedConstraints,
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
            final var addPropertyJob = fxomJobsFactory.addProperty(constraintsProperty, targetGridPane, -1);
            result.add(addPropertyJob);
        }

        return result;
    }

    private FXOMInstance makeColumnConstraintsInstance() {

        // Create new constraints instance
        final FXOMDocument newDocument = fxomDocument.getFactory().newDocument();
        final FXOMInstance result = new FXOMInstance(newDocument, ColumnConstraints.class);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(fxomDocument);

        return result;
    }

    private FXOMInstance makeColumnConstraintsInstance(final FXOMInstance constraints) {

        assert constraints != null;
        assert constraints.getDeclaredClass() == ColumnConstraints.class;

        // Create new constraints instance
        final FXOMInstance result = makeColumnConstraintsInstance();

        // Set the new column constraints values with the values of the specified instance
        //

        final boolean fillWidth = ColumnConstraintsMetadata.fillWidthPropertyMetadata.getValue(constraints);
        final double maxWidth = ColumnConstraintsMetadata.maxWidthPropertyMetadata.getValue(constraints);
        final double minWidth = ColumnConstraintsMetadata.minWidthPropertyMetadata.getValue(constraints);
        final double percentWidth = ColumnConstraintsMetadata.percentWidthPropertyMetadata.getValue(constraints);
        final double prefWidth = ColumnConstraintsMetadata.prefWidthPropertyMetadata.getValue(constraints);
        final String halignment = ColumnConstraintsMetadata.halignmentPropertyMetadata.getValue(constraints);
        final String hgrow = ColumnConstraintsMetadata.hgrowPropertyMetadata.getValue(constraints);

        ColumnConstraintsMetadata.fillWidthPropertyMetadata.setValue(result, fillWidth);
        ColumnConstraintsMetadata.maxWidthPropertyMetadata.setValue(result, maxWidth);
        // If the existing constraints minWidth is too small, we use the default one
        ColumnConstraintsMetadata.minWidthPropertyMetadata.setValue(result, Math.max(minWidth, defaultMinWidth));
        ColumnConstraintsMetadata.percentWidthPropertyMetadata.setValue(result, percentWidth);
        // If the existing constraints prefWidth is too small, we use the default one
        ColumnConstraintsMetadata.prefWidthPropertyMetadata.setValue(result, Math.max(prefWidth, defaultPrefWidth));
        ColumnConstraintsMetadata.halignmentPropertyMetadata.setValue(result, halignment);
        ColumnConstraintsMetadata.hgrowPropertyMetadata.setValue(result, hgrow);

        return result;
    }

    @ApplicationInstanceSingleton
    public static final class Factory extends JobFactory<AddColumnConstraintsJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link AddColumnConstraintsJob} job
         * @param position the position relative to a column
         * @param targetGridPanes map and their targeted column indexes
         * @return the job to execute
         */
        public AddColumnConstraintsJob getJob(final Position position, final Map<FXOMObject, Set<Integer>> targetGridPanes) {
            return create(AddColumnConstraintsJob.class, j -> j.setJobParameters(position, targetGridPanes));
        }
    }
}
