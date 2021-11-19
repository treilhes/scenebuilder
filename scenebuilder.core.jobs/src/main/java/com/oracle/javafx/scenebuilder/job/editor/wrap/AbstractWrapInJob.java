/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.job.editor.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.JobUtils;
import com.oracle.javafx.scenebuilder.job.editor.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ToggleFxRootJob;

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
    //private final Selection selection;

    public AbstractWrapInJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        //this.selection = documentManager.selectionDidChange().get().getSelection();
    }

    //TODO find who use this method and make them extend the result
    //TODO remove "ToExtend" to get the original method name (was added to generate compilation errors and find users)
    //TODO or delete if not used
    public static AbstractWrapInJob getWrapInJob(
    		ApplicationContext context,
            Editor editor,
            Class<?> wrappingClass) {

        assert getClassesSupportingWrapping().contains(wrappingClass);
        final AbstractWrapInJob job;
        if (wrappingClass == javafx.scene.layout.AnchorPane.class) {
            job = new WrapInAnchorPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.BorderPane.class) {
            job = new WrapInBorderPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.ButtonBar.class) {
            job = new WrapInButtonBarJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.DialogPane.class) {
            job = new WrapInDialogPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.FlowPane.class) {
            job = new WrapInFlowPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.GridPane.class) {
            job = new WrapInGridPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.Group.class) {
            job = new WrapInGroupJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.HBox.class) {
            job = new WrapInHBoxJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.Pane.class) {
            job = new WrapInPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.ScrollPane.class) {
            job = new WrapInScrollPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.SplitPane.class) {
            job = new WrapInSplitPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.StackPane.class) {
            job = new WrapInStackPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.TabPane.class) {
            job = new WrapInTabPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.text.TextFlow.class) {
            job = new WrapInTextFlowJob(context, editor);
        } else if (wrappingClass == javafx.scene.layout.TilePane.class) {
            job = new WrapInTilePaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.TitledPane.class) {
            job = new WrapInTitledPaneJob(context, editor);
        } else if (wrappingClass == javafx.scene.control.ToolBar.class) {
            job = new WrapInToolBarJob(context, editor);
        } else if (wrappingClass == javafx.scene.Scene.class) {
            job = new WrapInSceneJob(context, editor);
        } else if (wrappingClass == javafx.stage.Stage.class) {
            job = new WrapInStageJob(context, editor);
        } else {
            assert wrappingClass == javafx.scene.layout.VBox.class; // Because of (1)
            job = new WrapInVBoxJob(context, editor);
        }
        return job;
    }

    protected boolean canWrapIn() {
        final Selection selection = getEditorController().getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
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
            if (fxomObject.getSceneGraphObject() instanceof Axis) {
                return false;
            }
        }
        final FXOMObject parent = osg.getAncestor();
        if (parent == null) { // selection == root object
            return true;
        }
        final Object parentSceneGraphObject = parent.getSceneGraphObject();
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

            final Selection selection = getEditorController().getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
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
                        = WrapJobUtils.getContainerPropertyName(oldContainer, children);
                // Retrieve the old container property (already defined and not null)
                final FXOMPropertyC oldContainerProperty
                        = (FXOMPropertyC) oldContainer.getProperties().get(oldContainerPropertyName);
                assert oldContainerProperty != null
                        && oldContainerProperty.getParentInstance() != null;

                // Add the new container to the old container
                final int newContainerIndex = getIndex(oldContainer, children);
                final Job newContainerAddValueJob = new AddPropertyValueJob(getContext(),
                        newContainer,
                        oldContainerProperty,
                        newContainerIndex, getEditorController()).extend();
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
                    final Job fxRootJob = new ToggleFxRootJob(getContext(), getEditorController()).extend();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final Job fxControllerJob
                            = new ModifyFxControllerJob(getContext(), rootObject, null, getEditorController()).extend();
                    result.add(fxControllerJob);
                }
                // Then set the new container as root object
                final Job setDocumentRoot = new SetDocumentRootJob(getContext(),
                        newContainer, getEditorController()).extend();
                result.add(setDocumentRoot);
                // Finally add the fx:controller/fx:root to the new root object
                if (isFxRoot) {
                    final Job fxRootJob = new ToggleFxRootJob(getContext(), getEditorController()).extend();
                    result.add(fxRootJob);
                }
                if (fxController != null) {
                    final Job fxControllerJob
                            = new ModifyFxControllerJob(getContext(), newContainer, fxController, getEditorController()).extend();
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
        return new ObjectSelectionGroup(newObjects, newObjects.iterator().next(), null);
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
            final Job addValueJob = new AddPropertyValueJob(getContext(),
                    child,
                    containerProperty,
                    index++,
                    getEditorController()).extend();
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
            final Job removeValueJob = new RemovePropertyValueJob(getContext(),
                    child,
                    getEditorController()).extend();
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
        final DesignHierarchyMask newContainerMask = new DesignHierarchyMask(newContainer);
        final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);

        for (FXOMObject child : children) {

            // Modify child LAYOUT bounds
            if (newContainerMask.getMainAccessory() != null && newContainerMask.getMainAccessory().isFreeChildPositioning()) {
                assert child.getSceneGraphObject() instanceof Node;
                final Node childNode = (Node) child.getSceneGraphObject();
                final Bounds childBounds = childNode.getLayoutBounds();

                final Point2D point = childNode.localToParent(
                        childBounds.getMinX(), childBounds.getMinY());
                double layoutX = point.getX() - unionOfBounds.getMinX();
                double layoutY = point.getY() - unionOfBounds.getMinY();
                final Job modifyLayoutX = WrapJobUtils.modifyObjectJob(getContext(),
                        (FXOMInstance) child, "layoutX", layoutX, getEditorController());
                jobs.add(modifyLayoutX);
                final Job modifyLayoutY = WrapJobUtils.modifyObjectJob(getContext(),
                        (FXOMInstance) child, "layoutY", layoutY, getEditorController());
                jobs.add(modifyLayoutY);
            } else {
                assert child.getSceneGraphObject() instanceof Node;

                final Job modifyLayoutX = WrapJobUtils.modifyObjectJob(getContext(),
                        (FXOMInstance) child, "layoutX", 0.0, getEditorController());
                jobs.add(modifyLayoutX);
                final Job modifyLayoutY = WrapJobUtils.modifyObjectJob(getContext(),
                        (FXOMInstance) child, "layoutY", 0.0, getEditorController());
                jobs.add(modifyLayoutY);
            }

            // Remove static properties from child
            if (child instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) child;
                for (FXOMProperty p : fxomInstance.getProperties().values()) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    if (residentClass != null) {
                        jobs.add(new RemovePropertyJob(getContext(), p, getEditorController()).extend());
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
            final DesignHierarchyMask oldContainerMask = new DesignHierarchyMask(oldContainer);
            if (oldContainerMask.getMainAccessory() != null && oldContainerMask.getMainAccessory().isFreeChildPositioning()) {
                final Bounds unionOfBounds = WrapJobUtils.getUnionOfBounds(children);
                JobUtils.setLayoutX(newContainer, Node.class, unionOfBounds.getMinX());
                JobUtils.setLayoutY(newContainer, Node.class, unionOfBounds.getMinY());
//            JobUtils.setMinHeight(newContainer, Region.class, unionOfBounds.getHeight());
//            JobUtils.setMinWidth(newContainer, Region.class, unionOfBounds.getMinY());
            }
        }

        // Add static properties to the new container
        // (meaningfull for single selection only)
        if (children.size() == 1) {
            final Metadata metadata = Metadata.getMetadata();
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
        final DesignHierarchyMask mask = new DesignHierarchyMask(container);
        if (mask.isAcceptingSubComponent() == false) {
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

    private static List<Class<?>> classesSupportingWrapping;

    //TODO reactivate {link EditorController#performWrap(java.lang.Class)} after refactoring wrapping feature
    /**
     * Return the list of classes that can be passed to
     * {link EditorController#performWrap(java.lang.Class)}.
     *
     * @return the list of classes.
     */
    public synchronized static Collection<Class<?>> getClassesSupportingWrapping() {
        if (classesSupportingWrapping == null) {
            classesSupportingWrapping = new ArrayList<>();
            classesSupportingWrapping.add(javafx.scene.layout.AnchorPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.BorderPane.class);
            classesSupportingWrapping.add(javafx.scene.control.ButtonBar.class);
            classesSupportingWrapping.add(javafx.scene.control.DialogPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.FlowPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.GridPane.class);
            classesSupportingWrapping.add(javafx.scene.Group.class);
            classesSupportingWrapping.add(javafx.scene.layout.HBox.class);
            classesSupportingWrapping.add(javafx.scene.layout.Pane.class);
            classesSupportingWrapping.add(javafx.scene.control.ScrollPane.class);
            classesSupportingWrapping.add(javafx.scene.control.SplitPane.class);
            classesSupportingWrapping.add(javafx.scene.layout.StackPane.class);
            classesSupportingWrapping.add(javafx.scene.control.TabPane.class);
            classesSupportingWrapping.add(javafx.scene.text.TextFlow.class);
            classesSupportingWrapping.add(javafx.scene.layout.TilePane.class);
            classesSupportingWrapping.add(javafx.scene.control.TitledPane.class);
            classesSupportingWrapping.add(javafx.scene.control.ToolBar.class);
            classesSupportingWrapping.add(javafx.scene.layout.VBox.class);
            classesSupportingWrapping.add(javafx.scene.Scene.class);
            classesSupportingWrapping.add(javafx.stage.Stage.class);
            classesSupportingWrapping = Collections.unmodifiableList(classesSupportingWrapping);
        }

        return classesSupportingWrapping;
    }

}
