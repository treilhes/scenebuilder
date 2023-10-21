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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.scenebuilder.fxml.api.SbEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.control.effect.EffectProvider;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.fxml.selection.job.ModifySelectionJob;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Effect;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(nameKey = "action.name.set.effect", descriptionKey = "action.description.set.effect")
public class SetEffectAction extends AbstractAction {

    private static Logger logger = LoggerFactory.getLogger(SetEffectAction.class);

    private final EditorInstanceWindow documentWindow;
    private final JobManager jobManager;
    private final Selection selection;
    private final ModifySelectionJob.Factory modifySelectionJobFactory;
    private final IMetadata metadata;

    private Class<? extends Effect> effectClass;

    public SetEffectAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            Selection selection,
            IMetadata metadata,
            ModifySelectionJob.Factory modifySelectionJobFactory,
            @Autowired SbEditor editor,

            @Autowired @Lazy EditorInstanceWindow documentWindow) {
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    public class MenuProvider implements MenuItemProvider {

        private final static String SET_EFFECTS_MENU_ID = "setEffect";

        private final MenuBuilder menuBuilder;
        private final ActionFactory actionFactory;
        private final List<Class<? extends Effect>> effects;

        public MenuProvider(
                MenuBuilder menuBuilder,
                ActionFactory actionFactory,
                List<EffectProvider> effectProviders) {
            this.menuBuilder = menuBuilder;
            this.actionFactory = actionFactory;
            this.effects = effectProviders.stream().flatMap(p -> p.effects().stream()).collect(Collectors.toList());
        }

        @Override
        public List<MenuItemAttachment> menuItems() {
            return Arrays.asList(
                    MenuItemAttachment.create(menuBuilder.separator().build(), DefaultMenu.MODIFY_MENU_ID, PositionRequest.AsLastChild),
                    new SetEffectsMenuItemAttachment());
        }

        public class SetEffectsMenuItemAttachment implements MenuItemAttachment {

            private Menu menu = null;

            public SetEffectsMenuItemAttachment() {
                super();
            }

            @Override
            public String getTargetId() {
                return DefaultMenu.MODIFY_MENU_ID;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return PositionRequest.AsLastChild;
            }

            @Override
            public MenuItem getMenuItem() {

                if (menu != null) {
                    return menu;
                }

                menu = menuBuilder.menu().withId(SET_EFFECTS_MENU_ID).withTitle("menu.title.add.effect").build();

                for (Class<? extends Effect> c : effects) {
                    SetEffectAction action = actionFactory.create(SetEffectAction.class);
                    action.setEffectClass(c);
                    MenuItem mi = menuBuilder.menuItem().withTitle(c.getSimpleName()).withAction(action).build();
                    menu.getItems().add(mi);
                }
                return menu;
            }

        }

    }
}