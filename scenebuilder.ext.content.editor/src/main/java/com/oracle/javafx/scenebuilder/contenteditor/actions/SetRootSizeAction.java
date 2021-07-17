package com.oracle.javafx.scenebuilder.contenteditor.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.job.editor.UsePredefinedSizeJob;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.set.root.size", descriptionKey = "action.description.set.root.size")
public class SetRootSizeAction extends AbstractAction {

    private final Editor editor;
    
    private Size size;

    public SetRootSizeAction(
            @Autowired Api api, 
            @Autowired Editor editor) {
        super(api);
        this.editor = editor;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public boolean canPerform() {
        if (size == null) {
            return false;
        }
        
        final Job job = new UsePredefinedSizeJob(getApi().getContext(), editor, size).extend();
        return job.isExecutable();
    }

    @Override
    public ActionStatus perform() {
        final Job job = new UsePredefinedSizeJob(getApi().getContext(), editor, size).extend();
        getApi().getApiDoc().getJobManager().push(job);
        return ActionStatus.DONE;
    }
}