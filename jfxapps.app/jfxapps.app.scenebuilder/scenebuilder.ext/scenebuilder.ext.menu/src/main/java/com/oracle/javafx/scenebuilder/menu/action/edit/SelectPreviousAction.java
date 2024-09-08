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
package com.oracle.javafx.scenebuilder.menu.action.edit;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.action.Action.ActionStatus;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstanceWindow;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = SelectPreviousAction.MENU_ID,
        targetMenuId = SelectNextAction.MENU_ID,
        label = "menu.title.select.previous",
        positionRequest = PositionRequest.AsNextSibling)

@Accelerator(accelerator = "CTRL+LEFT")

public class SelectPreviousAction extends AbstractAction {

    public final static String MENU_ID = "selectNoneMenu";

    private final ApplicationInstanceWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final FxmlDocumentManager documentManager;
    private final Selection selection;

    public SelectPreviousAction(
            ActionExtensionFactory extensionFactory,
            ApplicationInstanceWindow documentWindow,
            FxmlDocumentManager documentManager,
            InlineEdit inlineEdit,
            Selection selection) {
        super(extensionFactory);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.selection = selection;
    }

    /**
     * Returns true if the selection is single and the container of the selected
     * object container contains a child previous to the selected one.
     *
     * @return if the selection is single and the container of the selected object
     *         container contains a child previous to the selected one.
     */
    @Override
    public boolean canPerform() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        if (fxomDocument == null || fxomDocument.getFxomRoot() == null || selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg.getItems().size() != 1) {
            return false;
        }
        return asg.getSiblings().size() > 1;
//        if (asg instanceof ObjectSelectionGroup) {
//            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
//            final Set<FXOMObject> items = osg.getItems();
//            if (items.size() != 1) {
//                return false;
//            }
//            final FXOMObject selectedObject = items.iterator().next();
//            return selectedObject.getPreviousSlibing() != null;
//        } else if (asg instanceof GridSelectionGroup) {
//            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
//            final Set<Integer> indexes = gsg.getIndexes();
//            if (indexes.size() != 1) {
//                return false;
//            }
//            final int index = indexes.iterator().next();
//            return index > 0;
//        } else {
//            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup(); // NOCHECK
//        }
//        return false;
    }

    /**
     * Performs the select previous control action.
     */
    @Override
    public ActionStatus doPerform() {
        assert canPerform(); // (1)

        selection.selectPrevious();

//        final AbstractSelectionGroup asg = selection.getGroup();
//        if (asg instanceof ObjectSelectionGroup) {
//            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
//            final Set<FXOMObject> items = osg.getItems();
//            assert items.size() == 1; // Because of (1)
//            final FXOMObject selectedObject = items.iterator().next();
//            final FXOMObject previousSibling = selectedObject.getPreviousSlibing();
//            assert previousSibling != null; // Because of (1)
//            selection.select(previousSibling);
//        } else {
//            assert asg instanceof GridSelectionGroup; // Because of (1)
//            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
//            final FXOMObject gridPane = gsg.getHitItem();
//            assert gridPane instanceof FXOMInstance;
//            final Set<Integer> indexes = gsg.getIndexes();
//            assert indexes.size() == 1; // Because of (1)
//            int selectedIndex = indexes.iterator().next();
//            int previousIndex = selectedIndex - 1;
//            assert previousIndex >= 0; // Because of (1)
//            selection.select(gridSelectionGroupFactory.getGroup(gridPane, gsg.getType(), previousIndex));
//        }

        return ActionStatus.DONE;
    }
}