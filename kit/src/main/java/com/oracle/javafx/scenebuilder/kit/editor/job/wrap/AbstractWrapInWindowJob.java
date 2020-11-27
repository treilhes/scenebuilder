package com.oracle.javafx.scenebuilder.kit.editor.job.wrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.AddPropertyJob;

/**
 * Main class used for the wrap jobs using the new window's SCENE property.
 */
public class AbstractWrapInWindowJob extends AbstractWrapInJob {
    public AbstractWrapInWindowJob(ApplicationContext context, Editor editor) {
        super(context, editor);
    }

    @Override
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

        // Can wrap in SCENE property single selection only
        if (osg.getItems().size() != 1) {
            return false;
        }

        // Selected object must be an instance of javafx.scene.Scene
        for (FXOMObject fxomObject : osg.getItems()) {
            if ((fxomObject.getSceneGraphObject() instanceof javafx.scene.Scene) == false) {
                return false;
            }
        }

        // Selected object must be root object
        final FXOMObject parent = osg.getAncestor();
        if (parent != null) { // selection != root object
            return false;
        }

        return true;
    }

    @Override
    protected List<Job> wrapChildrenJobs(final List<FXOMObject> children) {
        final List<Job> jobs = new ArrayList<>();

        final DesignHierarchyMask newContainerMask = new DesignHierarchyMask(newContainer);
        assert newContainerMask.isAcceptingAccessory(DesignHierarchyMask.Accessory.SCENE);

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName = new PropertyName("scene"); //NOI18N
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        assert children.size() == 1;

        // Add the children to the new container
        jobs.addAll(addChildrenJobs(newContainerProperty, children));

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final Job addPropertyJob = new AddPropertyJob(getContext(), 
                newContainerProperty,
                newContainer,
                -1, getEditorController()).extend();
        jobs.add(addPropertyJob);

        return jobs;
    }
}
