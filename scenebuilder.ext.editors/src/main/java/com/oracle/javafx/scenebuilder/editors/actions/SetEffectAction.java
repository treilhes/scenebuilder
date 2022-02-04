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
package com.oracle.javafx.scenebuilder.editors.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.selection.job.ModifySelectionJob;

import javafx.scene.Node;
import javafx.scene.effect.Effect;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.set.effect", descriptionKey = "action.description.set.effect")
public class SetEffectAction extends AbstractAction {

    private static Logger logger = LoggerFactory.getLogger(SetEffectAction.class);

    private final DocumentWindow documentWindow;
    private final JobManager jobManager;
    private final Selection selection;
    private final ModifySelectionJob.Factory modifySelectionJobFactory;
    private final Metadata metadata;

    private Class<? extends Effect> effectClass;

    public SetEffectAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            Selection selection,
            Metadata metadata,
            ModifySelectionJob.Factory modifySelectionJobFactory,
            @Autowired Editor editor,

            @Autowired @Lazy DocumentWindow documentWindow) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.selection = selection;
        this.metadata = metadata;
        this.modifySelectionJobFactory = modifySelectionJobFactory;
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
        return documentWindow != null && documentWindow.getStage().isFocused() && selection.isSelectionNode();
    }

    @Override
    public ActionStatus doPerform() {
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

            // TODO use a factory here, expecting a noarg constructor is bad
            final Effect effect = effectClass.getDeclaredConstructor().newInstance();

            final PropertyName pn = new PropertyName("effect"); // NOCHECK

            final PropertyMetadata pm = metadata.queryProperty(Node.class, pn);
            assert pm instanceof ValuePropertyMetadata;
            final ValuePropertyMetadata vpm = (ValuePropertyMetadata) pm;
            final AbstractJob job = modifySelectionJobFactory.getJob(vpm, effect);
            jobManager.push(job);
        } catch (Exception e) {
            logger.error("Error applying effect {}", effectClass, e);
        }
    }
}