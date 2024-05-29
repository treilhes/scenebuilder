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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveObjectJob;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

import javafx.scene.layout.RowConstraints;

/**
 * Job invoked when moving rows ABOVE or BELOW.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class MoveRowJob extends BatchSelectionJob {

    private FXOMObject targetGridPane;
    private final List<Integer> targetIndexes = new ArrayList<>();
    private Position position;
    private FXOMDocument fxomDocument;

    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final ReIndexRowContentJob.Factory reIndexRowContentJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;

    // @formatter:off
    protected MoveRowJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Selection selection,
            RemoveObjectJob.Factory removeObjectJobFactory,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ReIndexRowContentJob.Factory reIndexRowContentJobFactory,
            GridPaneHierarchyMask.Factory maskFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.reIndexRowContentJobFactory = reIndexRowContentJobFactory;
        this.maskFactory = maskFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
    }

    protected void setJobParameters(Position position) {
        assert position == Position.ABOVE || position == Position.BELOW;
        this.position = position;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        if (GridPaneJobUtils.canPerformMove(getSelection(), position, maskFactory)) {

            // Retrieve the target GridPane
            final Selection selection = getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof GridSelectionGroup; // Because of (1)
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;

            targetGridPane = gsg.getHitItem();
            targetIndexes.addAll(gsg.getIndexes());

            // Add sub jobs
            // First move the row constraints
            result.addAll(moveRowConstraints());
            // Then move the row content
            result.addAll(moveRowContent());
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Move Row " + position.name(); //NOCHECK
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final Set<Integer> movedIndexes = new HashSet<>();
        for (int targetIndex : targetIndexes) {
            int movedIndex = position == Position.ABOVE
                    ? targetIndex - 1 : targetIndex + 1;
            movedIndexes.add(movedIndex);
        }
        return gridSelectionGroupFactory.getGroup(targetGridPane, GridSelectionGroup.Type.ROW, movedIndexes);
    }

    private List<AbstractJob> moveRowConstraints() {

        final List<AbstractJob> result = new ArrayList<>();

        // Retrieve the constraints property for the specified target GridPane
        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOCHECK
        assert targetGridPane instanceof FXOMInstance;
        FXOMProperty constraintsProperty
                = ((FXOMInstance) targetGridPane).getProperties().get(propertyName);
        // GridPane has no constraints property => no constraints to move
        if (constraintsProperty == null) {
            return result;
        }

        final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);

        for (int targetIndex : targetIndexes) {

            final int positionIndex;
            switch (position) {
                case ABOVE:
                    positionIndex = targetIndex - 1;
                    break;
                case BELOW:
                    positionIndex = targetIndex + 1;
                    break;
                default:
                    assert false;
                    return result;
            }

            // Retrieve the target constraints
            final FXOMObject targetConstraints
                    = mask.getRowConstraintsAtIndex(targetIndex);

            // If the target index is associated to an existing constraints value :
            // we remove the target constraints and add it back at new position
            // No need to move the constraints of the row above/below :
            // indeed, they are automatically shifted while updating the target ones
            if (targetConstraints != null) {
                // First remove current target constraints
                final AbstractJob removeValueJob = removeObjectJobFactory.getJob(targetConstraints);
                result.add(removeValueJob);

                // Then add the target constraints at new positionIndex
                final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(targetConstraints,(FXOMPropertyC) constraintsProperty, positionIndex);
                result.add(addValueJob);
            }//
            // The target index is not associated to an existing constraints value :
            // we may need to move the constraints above the target one if any
            else if (position == Position.ABOVE) {
                // Retrieve the constraints above the target one
                final FXOMObject aboveConstraints
                        = mask.getRowConstraintsAtIndex(targetIndex - 1);

                // The index above is associated to an existing constraints value :
                // we insert a new constraints with default values at the position index
                if (aboveConstraints != null) {
                    // Create new empty constraints for the target row
                    final FXOMInstance addedConstraints = makeRowConstraintsInstance();
                    final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(addedConstraints, (FXOMPropertyC) constraintsProperty, positionIndex);
                    result.add(addValueJob);
                }
            }
        }
        return result;
    }

    private List<AbstractJob> moveRowContent() {

        final List<AbstractJob> result = new ArrayList<>();

        for (int targetIndex : targetIndexes) {

            switch (position) {
                case ABOVE:
                    // First move the target row content
                    result.add(reIndexRowContentJobFactory.getJob(-1, targetGridPane, targetIndex));
                    int aboveIndex = targetIndex - 1;
                    // Then move the content of the row above the target one
                    // If the index above is not part of the target indexes (selected indexes),
                    // we move the row content as many times as consecutive target indexes
                    if (targetIndexes.contains(aboveIndex) == false) {
                        int shiftIndex = 1;
                        while (targetIndexes.contains(targetIndex + shiftIndex)) {
                            shiftIndex++;
                        }
                        result.add(reIndexRowContentJobFactory.getJob(shiftIndex, targetGridPane, aboveIndex));
                    }
                    break;
                case BELOW:
                    // First move the target row content
                    result.add(reIndexRowContentJobFactory.getJob(+1, targetGridPane, targetIndex));
                    int belowIndex = targetIndex + 1;
                    // Then move the content of the row below the target one
                    // If the index below is not part of the target indexes (selected indexes),
                    // we move the row content as many times as consecutive target indexes
                    if (targetIndexes.contains(belowIndex) == false) {
                        int shiftIndex = -1;
                        while (targetIndexes.contains(targetIndex + shiftIndex)) {
                            shiftIndex--;
                        }
                        result.add(reIndexRowContentJobFactory.getJob(shiftIndex, targetGridPane, belowIndex));
                    }
                    break;
                default:
                    assert false;
            }
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<MoveRowJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link MoveRowJob} job
         *
         * @param position position relative to selected grid pane rows or to the
         *                 selected grid pane
         * @return the job to execute
         */
        public MoveRowJob getJob(Position position) {
            return create(MoveRowJob.class, j -> j.setJobParameters(position));
        }
    }
}
