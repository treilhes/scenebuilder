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
package com.oracle.javafx.scenebuilder.drivers.gridpane.job;

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
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.job.editor.JobUtils;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;

import javafx.scene.layout.ColumnConstraints;

/**
 * Job invoked when adding column constraints
 * It adds a column constraint to each provided GridPane.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
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
    private final Metadata metadata;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final AddPropertyJob.Factory addPropertyJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    /**
     * Key = target GridPane instance
     * Value = list of target column indexes for this GridPane
     */
    private final Map<FXOMObject, Set<Integer>> targetGridPanes = new HashMap<>();
    private Position position;


    protected AddColumnConstraintsJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Metadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
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
            result.addAll(addColumnConstraints((FXOMInstance) targetGridPane, targetIndexes));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Add Column Constraints"; //NOCHECK
    }

    private Set<AbstractJob> addColumnConstraints(
            final FXOMInstance targetGridPane,
            final Set<Integer> targetIndexes) {

        final Set<AbstractJob> result = new LinkedHashSet<>();

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
                final FXOMInstance addedConstraints = makeColumnConstraintsInstance(
                        (FXOMInstance) targetConstraints);

                final AddPropertyValueJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
                        (FXOMPropertyC) constraintsProperty, addedIndex);

                result.add(addValueJob);
            } //
            // The target index is not associated to an existing constraints value :
            // - we add new empty constraints from the last existing one to the added index (excluded)
            // - we add a new constraints with default values for the added index
            else {
                for (int index = constraintsSize; index < addedIndex; index++) {
                    // Create new empty constraints for the exisiting columns
                    final FXOMInstance addedConstraints = makeColumnConstraintsInstance();
                    final AddPropertyValueJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
                            (FXOMPropertyC) constraintsProperty, index);
                    result.add(addValueJob);
                }
                // Create new constraints with default values for the new added column
                final FXOMInstance addedConstraints = makeColumnConstraintsInstance();

                JobUtils.setMinWidth(addedConstraints, ColumnConstraints.class, defaultMinWidth);
                JobUtils.setPrefWidth(addedConstraints, ColumnConstraints.class, defaultPrefWidth);
                final AddPropertyValueJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints,
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
            final AddPropertyJob addPropertyJob = addPropertyJobFactory.getJob(constraintsProperty, targetGridPane, -1);
            result.add(addPropertyJob);
        }

        return result;
    }

    private FXOMInstance makeColumnConstraintsInstance() {

        // Create new constraints instance
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result
                = new FXOMInstance(newDocument, ColumnConstraints.class);
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
        final boolean fillWidth = JobUtils.getFillWidth(constraints, ColumnConstraints.class);
        final double maxWidth = JobUtils.getMaxWidth(constraints, ColumnConstraints.class);
        final double minWidth = JobUtils.getMinWidth(constraints, ColumnConstraints.class);
        final double percentWidth = JobUtils.getPercentWidth(constraints, ColumnConstraints.class);
        final double prefWidth = JobUtils.getPrefWidth(constraints, ColumnConstraints.class);
        final String halignment = JobUtils.getHAlignment(constraints, ColumnConstraints.class);
        final String hgrow = JobUtils.getHGrow(constraints, ColumnConstraints.class);

        JobUtils.setFillWidth(result, ColumnConstraints.class, fillWidth);
        JobUtils.setMaxWidth(result, ColumnConstraints.class, maxWidth);
        // If the existing constraints minWidth is too small, we use the default one
        JobUtils.setMinWidth(result, ColumnConstraints.class, Math.max(minWidth, defaultMinWidth));
        JobUtils.setPercentWidth(result, ColumnConstraints.class, percentWidth);
        // If the existing constraints prefWidth is too small, we use the default one
        JobUtils.setPrefWidth(result, ColumnConstraints.class, Math.max(prefWidth, defaultPrefWidth));
        JobUtils.setHAlignment(result, ColumnConstraints.class, halignment);
        JobUtils.setHGrow(result, ColumnConstraints.class, hgrow);

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<AddColumnConstraintsJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
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
