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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;

import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.selection.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.metadata.javafx.javafx.scene.NodeMetadata;

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
    private final IMetadata metadata;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final ToggleFxRootJob.Factory toggleFxRootJobFactory;
    private final ModifyFxControllerJob.Factory modifyFxControllerJobFactory;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final RemovePropertyValueJob.Factory removePropertyValueJobFactory;
    private final RemovePropertyJob.Factory removePropertyJobFactory;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final DSelectionGroupFactory.Factory objectSelectionGroupFactory;

    public AbstractWrapInJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Selection selection,
            FXOMObjectMask.Factory designMaskFactory,
            IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.designMaskFactory = designMaskFactory;
        this.metadata = metadata;
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.toggleFxRootJobFactory = toggleFxRootJobFactory;
        this.modifyFxControllerJobFactory = modifyFxControllerJobFactory;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.removePropertyValueJobFactory = removePropertyValueJobFactory;
        this.removePropertyJobFactory = removePropertyJobFactory;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }



    protected boolean canWrapIn() {
        final Selection selection = getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof DSelectionGroupFactory) == false) {
            return false;
        }
        final DSelectionGroupFactory osg = (DSelectionGroupFactory) asg;
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
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        if (canWrapIn()) { // (1)

            final Selection selection = getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof DSelectionGroupFactory; // Because of (1)
            final DSelectionGroupFactory osg = (DSelectionGroupFactory) asg;

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
                final AbstractJob newContainerAddValueJob = addPropertyValueJobFactory.getJob(
                        newContainer,
                        oldContainerProperty,
                        newContainerIndex);
                result.add(newContainerAddValueJob);

                // Remove children from the old container
                final List<AbstractJob> removeChildrenJobs = removeChildrenJobs(oldContainerProperty, children);
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
                    final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final AbstractJob fxControllerJob
                            = modifyFxControllerJobFactory.getJob(rootObject, null);
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object
                final AbstractJob setDocumentRoot = setDocumentRootJobFactory.getJob(newContainer);
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final AbstractJob fxControllerJob
                            = modifyFxControllerJobFactory.getJob(newContainer, fxController);
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
    protected AbstractSelectionGroup getNewSelectionGroup() {
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
    protected abstract List<AbstractJob> wrapChildrenJobs(final List<FXOMObject> children);

    protected List<AbstractJob> addChildrenJobs(
            final FXOMPropertyC containerProperty,
            final Collection<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();
        int index = 0;
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(
                    child,
                    containerProperty,
                    index++);
            jobs.add(addValueJob);
        }
        return jobs;
    }

    protected List<AbstractJob> removeChildrenJobs(
            final FXOMPropertyC containerProperty,
            final List<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final AbstractJob removeValueJob = removePropertyValueJobFactory.getJob(child);
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
    protected List<AbstractJob> modifyChildrenJobs(final List<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();
        final HierarchyMask newContainerMask = designMaskFactory.getMask(newContainer);
        final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);

        for (FXOMObject child : children) {

            ValuePropertyMetadata layoutXmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutX", null));
            ValuePropertyMetadata layoutYmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutY", null));

            // Modify child LAYOUT bounds
            if (newContainerMask.getMainAccessory() != null && newContainerMask.getMainAccessory().isFreeChildPositioning()) {
                assert child.getSceneGraphObject().isInstanceOf(Node.class);
                final Node childNode = child.getSceneGraphObject().getAs(Node.class);
                final Bounds childBounds = childNode.getLayoutBounds();

                final Point2D point = childNode.localToParent(
                        childBounds.getMinX(), childBounds.getMinY());
                double layoutX = point.getX() - unionOfBounds.getMinX();
                double layoutY = point.getY() - unionOfBounds.getMinY();

                final AbstractJob modifyLayoutX = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutXmeta, layoutX);
                jobs.add(modifyLayoutX);
                final AbstractJob modifyLayoutY = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutYmeta, layoutY);
                jobs.add(modifyLayoutY);
            } else {
                assert child.getSceneGraphObject().isInstanceOf(Node.class);

                final AbstractJob modifyLayoutX = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutXmeta, 0.0);
                jobs.add(modifyLayoutX);
                final AbstractJob modifyLayoutY = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutYmeta, 0.0);
                jobs.add(modifyLayoutY);
            }

            // Remove static properties from child
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null) {
                        jobs.add(removePropertyJobFactory.getJob(p));
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
            final HierarchyMask oldContainerMask = designMaskFactory.getMask(oldContainer);
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
                        final ValuePropertyMetadata vpm = metadata.queryValueProperty(fxomInstance, p.getName());
                        final Object value = vpm.getValueObject(fxomInstance);
                        vpm.setValueObject(newContainer, value);
                    }
                }
            }
        }
    }

    protected FXOMInstance makeNewContainerInstance(final Class<?> containerClass) {
        // Create new container instance
        final FXOMDocument newDocument = new FXOMDocument();
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
