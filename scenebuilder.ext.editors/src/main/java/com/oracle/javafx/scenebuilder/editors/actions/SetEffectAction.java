package com.oracle.javafx.scenebuilder.editors.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.ModifySelectionJob;

import javafx.scene.Node;
import javafx.scene.effect.Effect;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.set.effect", descriptionKey = "action.description.set.effect")
public class SetEffectAction extends AbstractAction {

    private static Logger logger = LoggerFactory.getLogger(SetEffectAction.class);

    private final Editor editor;

    private final DocumentWindow documentWindow;

    private Class<? extends Effect> effectClass;

    public SetEffectAction(
            @Autowired Api api, 
            @Autowired Editor editor, 
            @Autowired @Lazy DocumentWindow documentWindow) {
        super(api);
        this.editor = editor;
        this.documentWindow = documentWindow;
    }

    public Class<? extends Effect> getEffectClass() {
        return effectClass;
    }

    public void setEffectClass(Class<? extends Effect> effectClass) {
        this.effectClass = effectClass;
    }

    /**
     * Returns true if the 'set effect' action is permitted with the current
     * selection. In other words, returns true if the selection contains only Node
     * objects.
     *
     * @return true if the 'set effect' action is permitted.
     */
    @Override
    public boolean canPerform() {
        Selection selection = getApi().getApiDoc().getSelection();
        return documentWindow != null && documentWindow.getStage().isFocused() && selection.isSelectionNode();
    }

    @Override
    public ActionStatus perform() {
        performSetEffect(getEffectClass());
        return ActionStatus.DONE;
    }

    /**
     * Performs the 'set effect' edit action. This method creates an instance of the
     * specified effect class and sets it in the effect property of the selected
     * objects.
     *
     * @param effectClass class of the effect to be added (never null)
     */
    private void performSetEffect(Class<? extends Effect> effectClass) {
        assert canPerform(); // (1)

        try {
            JobManager mng = getApi().getApiDoc().getJobManager();

            // TODO use a factory here, expecting a noarg constructor is bad
            final Effect effect = effectClass.getDeclaredConstructor().newInstance();

            final PropertyName pn = new PropertyName("effect"); // NOCHECK

            final PropertyMetadata pm = Metadata.getMetadata().queryProperty(Node.class, pn);
            assert pm instanceof ValuePropertyMetadata;
            final ValuePropertyMetadata vpm = (ValuePropertyMetadata) pm;
            final Job job = new ModifySelectionJob(getApi().getContext(), vpm, effect, editor).extend();
            mng.push(job);
        } catch (Exception e) {
            logger.error("Error applying effect {}", effectClass, e);
        }
    }
}