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
import java.util.List;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Main class used for the unwrap jobs.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UnwrapJob extends BatchSelectionJob {

    private FXOMInstance oldContainer, newContainer;
    private List<FXOMObject> oldContainerChildren;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final RemovePropertyJob.Factory removePropertyJobFactory;
    private final RemovePropertyValueJob.Factory removePropertyValueJobFactory;
    private final ToggleFxRootJob.Factory toggleFxRootJobFactory;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final ModifyFxControllerJob.Factory modifyFxControllerJobFactory;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final IMetadata metadata;
    private final FxomJobsFactory fxomJobsFactory;
    private final WrapInJobFactory wrapInJobFactory;
    private final DSelectionGroupFactory.Factory objectSelectionGroupFactory;

 // @formatter:off
    protected UnwrapJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            RemovePropertyJob.Factory removePropertyJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            FxomJobsFactory fxomJobsFactory,
            FXOMObjectMask.Factory designMaskFactory,
            IMetadata metadata,
            WrapInJobFactory wrapInJobFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.removePropertyJobFactory = removePropertyJobFactory;
        this.removePropertyValueJobFactory = removePropertyValueJobFactory;
        this.toggleFxRootJobFactory = toggleFxRootJobFactory;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.modifyFxControllerJobFactory = modifyFxControllerJobFactory;
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
        this.wrapInJobFactory = wrapInJobFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters() {
    }

    protected boolean canUnwrap() {
        final Selection selection = getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof DSelectionGroupFactory) == false) {
            return false;
        }
        final DSelectionGroupFactory osg = (DSelectionGroupFactory) asg;
        if (osg.getItems().size() != 1) {
            return false;
        }
        final FXOMObject container = osg.getItems().iterator().next();
        if (container instanceof FXOMInstance == false) {
            return false;
        }
        final FXOMInstance containerInstance = (FXOMInstance) container;

        // Unresolved custom type
        if (container.getSceneGraphObject().isEmpty()) {
            return false;
        }

        // Can unwrap ALL classes supporting wrapping
        boolean isAssignableFrom = false;
        for (Class<?> clazz : wrapInJobFactory.getClassesSupportingWrapping()) {
            isAssignableFrom |= clazz.isAssignableFrom(
                    containerInstance.getDeclaredClass());
        }
        if (isAssignableFrom == false) {
            return false;
        }

        // Retrieve the children of the container to unwrap
        final List<FXOMObject> children = getChildren(containerInstance);
        int childrenCount = children.size();
        // If the container to unwrap has no childen, it cannot be unwrapped
        if (childrenCount == 0) {
            return false;
        }

        // Retrieve the parent of the container to unwrap
        final FXOMObject parentContainer = container.getParentObject();
        // Unwrap the root node
        if (parentContainer == null) {
            return childrenCount == 1;
        } else {
            // Check that the num and type of children can be added to the parent container
            final HierarchyMask parentContainerMask = designMaskFactory.getMask(parentContainer);

            if (parentContainerMask.hasMainAccessory()) {
                if (parentContainerMask.getMainAccessory().isCollection()) {
                    return childrenCount >= 1;
                } else {
                    final FXOMObject child = children.iterator().next();
                    return parentContainerMask.isAcceptingAccessory(parentContainerMask.getMainAccessory(), child);
                }

            } else {
                // those cases are handled by sub component now
                // TODO what about collection
                // TODO what about graphics
//                assert parentContainerMask.isAcceptingAccessory(Accessory.CONTENT)
//                        || parentContainerMask.isAcceptingAccessory(Accessory.GRAPHIC)
//                        || parentContainerMask.isAcceptingAccessory(Accessory.ROOT)
//                        || parentContainerMask.isAcceptingAccessory(Accessory.SCENE)
//                        || parentContainerMask.getFxomObject().getSceneGraphObject().isInstanceOf(BorderPane.class)
//                        || parentContainerMask.getFxomObject().getSceneGraphObject().isInstanceOf(DialogPane.class);
                assert parentContainerMask.getFxomObject().getSceneGraphObject().isInstanceOf(BorderPane.class)
                    || parentContainerMask.getFxomObject().getSceneGraphObject().isInstanceOf(DialogPane.class);
                if (childrenCount != 1) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        if (canUnwrap()) { // (1)

            final Selection selection = getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof DSelectionGroupFactory; // Because of (1)
            final DSelectionGroupFactory osg = (DSelectionGroupFactory) asg;
            assert osg.getItems().size() == 1; // Because of (1)

            // Retrieve the old container (container to unwrap)
            oldContainer = (FXOMInstance) osg.getItems().iterator().next();
            // Retrieve the children of the old container
            oldContainerChildren = getChildren(oldContainer);
            // Retrieve the old container property name in use
            final PropertyName oldContainerPropertyName
                    = WrapJobUtils.getContainerPropertyName(designMaskFactory, oldContainer, oldContainerChildren);
            // Retrieve the old container property (already defined and not null)
            final FXOMPropertyC oldContainerProperty
                    = (FXOMPropertyC) oldContainer.getProperties().get(oldContainerPropertyName);
            assert oldContainerProperty != null
                    && oldContainerProperty.getParentInstance() != null;

            // Retrieve the parent of the old container (aka new container)
            newContainer = (FXOMInstance) oldContainer.getParentObject();

            // Remove the old container property from the old container instance
            final AbstractJob removePropertyJob = removePropertyJobFactory.getJob(oldContainerProperty);
            result.add(removePropertyJob);

            // Remove the children from the old container property
            final List<AbstractJob> removeChildrenJobs
                    = removeChildrenJobs(oldContainerProperty, oldContainerChildren);
            result.addAll(removeChildrenJobs);

            //------------------------------------------------------------------
            // If the target object is NOT the FXOM root :
            // - we update the new container bounds and add it to the old container
            // - we update the children bounds and remove them from the old container
            //------------------------------------------------------------------
            if (newContainer != null) {

                // Retrieve the new container property name in use
                final List<FXOMObject> newContainerChildren = new ArrayList<>();
                newContainerChildren.add(oldContainer);
                final PropertyName newContainerPropertyName
                        = WrapJobUtils.getContainerPropertyName(designMaskFactory, newContainer, newContainerChildren);
                // Retrieve the new container property (already defined and not null)
                final FXOMPropertyC newContainerProperty
                        = (FXOMPropertyC) newContainer.getProperties().get(newContainerPropertyName);
                assert newContainerProperty != null
                        && newContainerProperty.getParentInstance() != null;

                // Update children bounds before adding them to the new container
                result.addAll(modifyChildrenJobs(oldContainerChildren));

                // Add the children to the new container
                int index = oldContainer.getIndexInParentProperty();
                final List<AbstractJob> addChildrenJobs
                        = addChildrenJobs(newContainerProperty, index, oldContainerChildren);
                result.addAll(addChildrenJobs);

                // Remove the old container from the new container property
                final AbstractJob removeValueJob = removePropertyValueJobFactory.getJob(oldContainer);
                result.add(removeValueJob);
            } //
            //------------------------------------------------------------------
            // If the target object is the FXOM root :
            // - we update the document root with the single child of the root node
            //------------------------------------------------------------------
            else {
                assert oldContainerChildren.size() == 1; // Because of (1)
                boolean isFxRoot = oldContainer.isFxRoot();
                final String fxController = oldContainer.getFxController();
                // First remove the fx:controller/fx:root from the old root object
                if (isFxRoot) {
                    final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final AbstractJob fxControllerJob = modifyFxControllerJobFactory.getJob(oldContainer, null);
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object
                final FXOMObject child = oldContainerChildren.iterator().next();
                final AbstractJob setDocumentRoot = setDocumentRootJobFactory.getJob(child);
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final AbstractJob fxControllerJob = modifyFxControllerJobFactory.getJob(child, fxController);
                    result.add(fxControllerJob);
                }
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Unwrap";
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        return objectSelectionGroupFactory.getGroup(oldContainerChildren, oldContainerChildren.iterator().next(), null);
    }

    protected List<AbstractJob> addChildrenJobs(
            final FXOMPropertyC containerProperty,
            final int start,
            final List<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();
        int index = start;
        for (FXOMObject child : children) {
            assert child instanceof FXOMInstance;
            final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(child, containerProperty, index++);
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

        assert oldContainer.getSceneGraphObject().isInstanceOf(Node.class);
        final Node oldContainerNode = oldContainer.getSceneGraphObject().getAs(Node.class);

        for (FXOMObject child : children) {
            assert child.getSceneGraphObject().isInstanceOf(Node.class);

            final Node childNode = child.getSceneGraphObject().getAs(Node.class);
            final double currentLayoutX = childNode.getLayoutX();
            final double currentLayoutY = childNode.getLayoutY();

            ValuePropertyMetadata layoutXmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutX", null));
            ValuePropertyMetadata layoutYmeta = metadata.queryValueProperty((FXOMInstance) child, new PropertyName("layoutY", null));

            // Modify child LAYOUT bounds
            if (newContainerMask.getMainAccessory() != null && newContainerMask.getMainAccessory().isFreeChildPositioning()) {
                final Point2D nextLayoutXY = oldContainerNode.localToParent(currentLayoutX, currentLayoutY);

                final AbstractJob modifyLayoutX = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutXmeta, nextLayoutXY.getX());
                jobs.add(modifyLayoutX);
                final AbstractJob modifyLayoutY = modifyObjectJobFactory.getJob((FXOMInstance) child, layoutYmeta, nextLayoutXY.getY());
                jobs.add(modifyLayoutY);
            } else {
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
                    if (residentClass != null
                            && residentClass != newContainer.getDeclaredClass()) {
                        jobs.add(removePropertyJobFactory.getJob(p));
                    }
                }
            }
        }
        return jobs;
    }

    private List<FXOMObject> getChildren(final FXOMInstance container) {
        final HierarchyMask mask = designMaskFactory.getMask(container);
        final List<FXOMObject> result = new ArrayList<>();
        if (mask.getMainAccessory() != null) {
            // TabPane => unwrap first Tab CONTENT
            if (TabPane.class.isAssignableFrom(container.getDeclaredClass())) {
                final List<FXOMObject> tabs = mask.getAccessories(mask.getMainAccessory(), false);
                if (tabs.size() >= 1) {
                    final FXOMObject tab = tabs.get(0);
                    final HierarchyMask tabMask = designMaskFactory.getMask(tab);
                    assert tabMask.isAcceptingAccessory(tabMask.getMainAccessory());
                    List<FXOMObject> content = tabMask.getAccessories(tabMask.getMainAccessory(), true);
                    if (!content.isEmpty()) {
                        result.addAll(content);
                    }
                }
            } else {
                result.addAll(mask.getAccessories(mask.getMainAccessory(), true));
            }
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<UnwrapJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UnwrapJob} job
         * @return the job to execute
         */
        public UnwrapJob getJob() {
            return create(UnwrapJob.class, j -> j.setJobParameters());
        }
    }
}
