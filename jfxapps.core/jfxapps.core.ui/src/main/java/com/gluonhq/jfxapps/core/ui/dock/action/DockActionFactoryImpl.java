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
package com.gluonhq.jfxapps.core.ui.dock.action;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.ui.DockActionFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockType;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.ChangeDockTypeAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.CloseDockAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.CloseViewAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.MoveToDockAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.ToggleMinimizeDockAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.ToggleViewVisibilityAction;
import com.gluonhq.jfxapps.core.ui.dock.action.impl.UndockViewAction;

@ApplicationInstanceSingleton
public class DockActionFactoryImpl implements DockActionFactory{

    private final ActionFactory actionFactory;

    public DockActionFactoryImpl(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public Action close(Dock dock) {
        return actionFactory.create(CloseDockAction.class, a -> a.setTargetDock(dock));
    }

    @Override
    public Action close(View view) {
        return actionFactory.create(CloseViewAction.class, a -> a.setTargetView(view));
    }

    @Override
    public Action undock(View view) {
        return actionFactory.create(UndockViewAction.class, a -> a.setTargetView(view));
    }

    @Override
    public Action dockInto(UUID dockId) {
        return actionFactory.create(MoveToDockAction.class, a -> a.setTargetDockId(dockId));
    }


    @Override
    public Action toggleMinimized(Dock dock) {
        return actionFactory.create(ToggleMinimizeDockAction.class, a -> a.setTargetDock(dock));
    }

    @Override
    public Action toggleViewVisibility(View view) {
        return actionFactory.create(ToggleViewVisibilityAction.class, a -> a.setView(view));
    }

    @Override
    public Action changeDockType(DockType<?> dockType) {
        return actionFactory.create(ChangeDockTypeAction.class, a -> a.setDockType(dockType));
    }
}
