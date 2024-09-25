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
package com.gluonhq.jfxapps.core.job.manager.action.impl;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;

@ApplicationInstancePrototype("com.gluonhq.jfxapps.core.job.manager.action.impl.UndoAction")
@ActionMeta(
        nameKey = "action.name.undo",
        descriptionKey = "action.description.undo")
public class UndoAction extends AbstractAction {

    public final static String MENU_ID = "undoMenuItem"; //NOCHECK

    private final JobManager jobManager;
    private final ApplicationInstanceEvents documentManager;

    public UndoAction(
        I18N i18n,
        ActionExtensionFactory extensionFactory,
        JobManager jobManager,
        ApplicationInstanceEvents documentManager) {
        super(i18n, extensionFactory);
        this.jobManager = jobManager;
        this.documentManager = documentManager;
    }

    /**
     * Returns null or the description of the action to be undone.
     *
     * @return null or the description of the action to be undone.
     */
    public String getUndoDescription() {
        final StringBuilder result = new StringBuilder();
        result.append(getI18n().getStringOrDefault("menu.title.undo", "menu.title.undo"));
        if (canPerform()) {
            result.append(" "); //NOI18N
            result.append(jobManager.getUndoDescription());
        }
        return result.toString();
    }
    /**
     * Returns true if the undo action is permitted (ie there is something to be
     * undone).
     *
     * @return true if the undo action is permitted.
     */
    @Override
    public boolean canPerform() {
        return jobManager.canUndo();
    }

    /**
     * Performs the undo action.
     */
    @Override
    public ActionStatus doPerform() {
        jobManager.undo();
        assert documentManager.fxomDocument().get().isUpdateOnGoing() == false;

        return ActionStatus.DONE;
    }

}