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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.fxom.jobs.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.fxom.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.control.Accordion;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Main class used for the wrap jobs.
 */
public abstract class AbstractWrapInJob extends BatchSelectionJob {

    protected Class<?> newContainerClass;
    protected FXOMInstance oldContainer, newContainer;
    private final FXOMDocument fxomDocument;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final SbMetadata metadata;
    private final SelectionJobsFactory selectionJobFactory;
    private final FxomJobsFactory fxomJobsFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    //@formatter:off
    public AbstractWrapInJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            Selection selection,
            FXOMObjectMask.Factory designMaskFactory,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        //@formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.designMaskFactory = designMaskFactory;
        this.metadata = metadata;
        this.selectionJobFactory = selectionJobFactory;
        this.fxomJobsFactory = fxomJobsFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }



    protected boolean canWrapIn() {
        final Selection selection = getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final SelectionGroup asg = selection.getGroup();
        if ((asg instanceof ObjectSelectionGroup) == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        if (osg.hasSingleParent() == false) {
            return false;
        }
        if (selection.isSelectionNode() == false) {
            return false;
        }
        // Cannot wrap in Axis nodes
        for (FXOMObject fxomObject : osg.getItems()) {
            if (fxomObject.getSceneGraphObject().isInstanceOf(Axis.class)) {
                return false;
            }
        }
        final FXOMObject parent = osg.getAncestor();
        if (parent == null) { // selection == root object
            return true;
        }
        final Object parentSceneGraphObject = parent.getSceneGraphObject().get();
        if (parentSceneGraphObject instanceof BorderPane
                || parentSceneGraphObject instanceof DialogPane) {
            return osg.getItems().size() == 1;
        }
        return !(parentSceneGraphObject instanceof Accordion) // accepts only TitledPanes
                && !(parentSceneGraphObject instanceof TabPane); // accepts only Tabs
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (canWrapIn()) { // (1)

            final Selection selection = getSelection();
            final SelectionGroup asg = selection.getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;

            // Retrieve the old container
            oldContainer = (FXOMInstance) osg.getAncestor();

            // Retrieve the children to be wrapped
            final List<FXOMObject> children = osg.getSortedItems();

            // Create the new container
            newContainer = makeNewContainerInstance();
            // Update the new container
            modifyNewContainer(children);

            //==================================================================
            // STEP #1
            //==================================================================
            // If the target object is NOT the FXOM root :
            // - we add the new container to the old container
            // - we remove the children from the old container
            //------------------------------------------------------------------
            if (oldContainer != null) {

                // Retrieve the old container property name in use
                final PropertyName oldContainerPropertyName
                        = WrapJobUtils.getContainerPropertyName(designMaskFactory, oldContainer, children);
                // Retrieve the old container property (already defined and not null)
                final FXOMPropertyC oldContainerProperty
                        = (FXOMPropertyC) oldContainer.getProperties().get(oldContainerPropertyName);
                assert oldContainerProperty != null
                        && oldContainerProperty.getParentInstance() != null;

                // Add the new container to the old container
                final int newContainerIndex = getIndex(oldContainer, children);
                final Job newContainerAddValueJob = fxomJobsFactory.addPropertyValue(
                        newContainer,
                        oldContainerProperty,
                        newContainerIndex);
                result.add(newContainerAddValueJob);

                // Remove children from the old container
                final List<Job> removeChildrenJobs = removeChildrenJobs(oldContainerProperty, children);
                result.addAll(removeChildrenJobs);
            } //
            //------------------------------------------------------------------
            // If the target object is the FXOM root :
            // - we update the document root with the new container
            //------------------------------------------------------------------
            else {
                assert children.size() == 1; // Wrap the single root node
                final FXOMObject rootObject = children.iterator().next();
                assert rootObject instanceof FXOMInstance;
                boolean isFxRoot = ((FXOMInstance) rootObject).isFxRoot();
                final String fxController = rootObject.getFxController();
                // First remove the fx:controller/fx:root from the old root object
                if (isFxRoot) {
                    final Job fxRootJob = fxomJobsFactory.toggleFxRoot();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final Job fxControllerJob = fxomJobsFactory.modifyFxController(rootObject, null);
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object
                final Job setDocumentRoot = selectionJobFactory.setDocumentRoot(newContainer);
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final Job fxRootJob = fxomJobsFactory.toggleFxRoot();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final Job fxControllerJob = fxomJobsFactory.modifyFxController(newContainer, fxController);
                    result.add(fxControllerJob);
                }
            }

            //==================================================================
            // STEP #2
            //==================================================================
            // This step depends on the new container property
            // (either either the SUB COMPONENT or the CONTENT property)
            //------------------------------------------------------------------
            result.addAll(wrapChildrenJobs(children));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Wrap in " + newContainerClass.getSimpleName();
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        List<FXOMObject> newObjects = new ArrayList<>();
        newObjects.add(newContainer);
        return objectSelectionGroupFactory.getGroup(newObjects, newObjects.iterator().next(), null);
    }

    /**
     * Used to wrap the specified children in the new container. May use either
     * the SUB COMPONENT or the CONTENT property.
     *
     * @param children The children to be wrapped.
     * @return A list of jobs.
     */
    protected abstract List<Job> wrapChildrenJobs(final List<FXOMObject> children);

    protected List<Job> addChildrenJobs(
            final FXOMPropertyC containerProperty,
            final Collection<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        int index = 0;
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final Job addValueJob = fxomJobsFactory.addPropertyValue(
                    child,
                    containerProperty,
                    index++);
            jobs.add(addValueJob);
        }
        return jobs;
    }

    protected List<Job> removeChildrenJobs(
            final FXOMPropertyC containerProperty,
            final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final Job removeValueJob = fxomJobsFactory.removePropertyValue(child);
            jobs.add(removeValueJob);
        }
        return jobs;
    }

    /**
     * Used to modify the specified children.
     *
     * @param children The children to be modified.
     * @return A list of jobs.
     */
    protected List<Job> modifyChildrenJobs(final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();
        final var newContainerMask = designMaskFactory.getMask(newContainer);
        final var unionOfBounds = WrapJobUtils.getUnionOfBounds(children);

        for (FXOMObject child : children) {

            var layoutXmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutX", null));
            var layoutYmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutY", null));

            // Modify child LAYOUT bounds
            if (newContainerMask.getMainAccessory() != null && newContainerMask.getMainAccessory().isFreeChildPositioning()) {
                assert child.getSceneGraphObject().isInstanceOf(Node.class);
                final Node childNode = child.getSceneGraphObject().getAs(Node.class);
                final Bounds childBounds = childNode.getLayoutBounds();

                final Point2D point = childNode.localToParent(
                        childBounds.getMinX(), childBounds.getMinY());
                double layoutX = point.getX() - unionOfBounds.getMinX();
                double layoutY = point.getY() - unionOfBounds.getMinY();

                final Job modifyLayoutX = fxomJobsFactory.modifyObject((FXOMInstance) child, layoutXmeta, layoutX);
                jobs.add(modifyLayoutX);
                final Job modifyLayoutY = fxomJobsFactory.modifyObject((FXOMInstance) child, layoutYmeta, layoutY);
                jobs.add(modifyLayoutY);
            } else {
                assert child.getSceneGraphObject().isInstanceOf(Node.class);

                final Job modifyLayoutX = fxomJobsFactory.modifyObject((FXOMInstance) child, layoutXmeta, 0.0);
                jobs.add(modifyLayoutX);
                final Job modifyLayoutY = fxomJobsFactory.modifyObject((FXOMInstance) child, layoutYmeta, 0.0);
                jobs.add(modifyLayoutY);
            }

            // Remove static properties from child
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null) {
                        var job = fxomJobsFactory.removeProperty(p);
                        jobs.add(job);
                    }
                }
            }
        }

        return jobs;
    }

    /**
     * Used to modify the new container.
     *
     * Note that unlike the modifyChildrenJobs method, we do not use any job
     * here but directly set the properties.
     *
     * @param children The children.
     */
    protected void modifyNewContainer(final List<FXOMObject> children) {
        if (oldContainer != null) {
            final var oldContainerMask = designMaskFactory.getMask(oldContainer);
            if (oldContainerMask.getMainAccessory() != null && oldContainerMask.getMainAccessory().isFreeChildPositioning()) {
                final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);
                NodeMetadata.layoutXPropertyMetadata.setValue(newContainer, unionOfBounds.getMinX());
                NodeMetadata.layoutYPropertyMetadata.setValue(newContainer, unionOfBounds.getMinY());
//            JobUtils.setMinHeight(newContainer, Region.class, unionOfBounds.getHeight());
//            JobUtils.setMinWidth(newContainer, Region.class, unionOfBounds.getMinY());
            }
        }

        // Add static properties to the new container
        // (meaningfull for single selection only)
        if (children.size() == 1) {

            final FXOMObject child = children.get(0);
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null) {
                        final var vpm = metadata.queryValueProperty(fxomInstance, p.getName());
                        final var value = vpm.getValueObject(fxomInstance);
                        vpm.setValueObject(newContainer, value);
                    }
                }
            }
        }
    }

    protected FXOMInstance makeNewContainerInstance(final Class<?> containerClass) {
        // Create new container instance
        final FXOMDocument newDocument = fxomDocument.getFactory().newDocument();
        final FXOMInstance result = new FXOMInstance(newDocument, containerClass);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(fxomDocument);

        return result;
    }

    private FXOMInstance makeNewContainerInstance() {
        return AbstractWrapInJob.this.makeNewContainerInstance(newContainerClass);
    }

    /**
     * Returns the index to be used in order to add the new container to the old
     * container.
     *
     * @param container
     * @param fxomObjects
     * @return
     */
    private int getIndex(final FXOMInstance container, final List<FXOMObject> fxomObjects) {
        final HierarchyMask mask = designMaskFactory.getMask(container);
        if (mask.hasMainAccessory() == false) {
            return -1;
        }
        // Use the smaller index of the specified FXOM objects
        final Iterator<FXOMObject> iterator = fxomObjects.iterator();
        assert iterator.hasNext();
        int result = iterator.next().getIndexInParentProperty();
        while (iterator.hasNext()) {
            int index = iterator.next().getIndexInParentProperty();
            if (index < result) {
                result = index;
            }
        }
        return result;
    }



}
