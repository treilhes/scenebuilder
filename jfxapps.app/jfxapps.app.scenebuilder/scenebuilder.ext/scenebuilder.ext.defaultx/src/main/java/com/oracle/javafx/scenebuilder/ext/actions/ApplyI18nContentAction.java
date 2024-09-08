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
package com.oracle.javafx.scenebuilder.ext.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.jar.analysis.report", descriptionKey = "action.description.show.jar.analysis.report")
public class ApplyI18nContentAction extends AbstractAction implements InitWithDocument {

    private ApplyI18nContentConfig config;

    private final DocumentManager documentManager;

    //@formatter:off
    public ApplyI18nContentAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            DocumentManager documentManager) {
      //@formatter:on
        super(i18n, extensionFactory);
        this.documentManager = documentManager;
    }

    public synchronized ApplyI18nContentConfig getActionConfig() {
        if (config == null) {
            config = new ApplyI18nContentConfig();
        }
        return config;
    }

    public synchronized void resetActionConfig() {
        config = null;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        assert getActionConfig() != null;
        documentManager.i18nResourceConfig().set(getActionConfig());
        return ActionStatus.DONE;
    }

    public static class ApplyI18nContentConfig implements I18nResourceProvider {
        private List<ResourceBundle> bundles = new ArrayList<>();

        @Override
        public List<ResourceBundle> getBundles() {
            return bundles;
        }

    }

    @Override
    public void initWithDocument() {
        checkAndPerform();
    }
}
