/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Actions implements Action {

    List<Action> actions = new ArrayList<>();

    public static Actions startWith(Action firstAction) {
        Objects.requireNonNull(firstAction, "firstAction cannot be null");
        Actions actions = new Actions();
        actions.actions.add(firstAction);
        return actions;
    }

    private Actions() {
    }

    @Override
    public Actions then(Action nextAction) {
        Objects.requireNonNull(nextAction, "nextAction cannot be null");
        actions.add(nextAction);
        return this;
    }

    @Override
    public boolean canPerform() {
        for (Action action : actions) {
            if (!action.canPerform()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ActionStatus perform() {
        for (Action action : actions) {
            var actionStatus = action.perform();
            if (actionStatus != ActionStatus.DONE) {
                return actionStatus;
            }
        }
        return ActionStatus.DONE;
    }

    @Override
    public ActionStatus checkAndPerform() {
        for (Action action : actions) {
            var actionStatus = action.checkAndPerform();
            if (actionStatus != ActionStatus.DONE) {
                return actionStatus;
            }
        }
        return ActionStatus.DONE;
    }

    @Override
    public String getUniqueId() {
        return actions.stream()
                .map(Action::getUniqueId)
                .reduce((a, b) -> a + "\n" + b)
                .orElse(null);
    }

    @Override
    public String getName() {
        return actions.stream()
                .map(Action::getName)
                .reduce((a, b) -> a + "\n" + b)
                .orElse(null);
    }

    @Override
    public String getDescription() {
        return actions.stream()
                .map(Action::getDescription)
                .reduce((a, b) -> a + "\n" + b)
                .orElse(null);
    }

}
