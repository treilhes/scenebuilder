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
package org.scenebuilder.ext.script;

import java.io.IOException;

import org.scenebuilder.ext.script.preference.global.StaticLoadPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.emc4j.boot.api.context.annotation.PreferedConstructor;
import com.gluonhq.jfxapps.core.api.fxom.FxomDocumentFactory;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.ext.LoaderCapabilitiesManager;

@ApplicationInstanceSingleton
public class LoaderCapabilitiesManagerImpl implements LoaderCapabilitiesManager, InitWithDocument {

    private static Logger logger = LoggerFactory.getLogger(LoaderCapabilitiesManagerImpl.class);

    private static boolean staticLoad = true;

    public LoaderCapabilitiesManagerImpl() {

    }

    @PreferedConstructor
    public LoaderCapabilitiesManagerImpl(
            FxomDocumentFactory fxomDocumentFactory,
            StaticLoadPreference staticLoadPreference,
            ApplicationInstanceEvents docManager) {

        staticLoadPreference.getObservableValue().addListener((ob, o, n) -> {
            setStaticLoadingEnabled(n);
            FXOMDocument fxomDocument = docManager.fxomDocument().get();

            if (fxomDocument != null) {
                try {
                    FXOMDocument clone = fxomDocumentFactory.newDocument(fxomDocument.getFxmlText(false),
                                fxomDocument.getLocation(),
                                fxomDocument.getClassLoader(),
                                fxomDocument.getResources());
                    docManager.fxomDocument().set(clone);
                } catch (IOException e) {
                    logger.error("Unable to update document after changing loader capabilities" , e);
                }
            }

        });
    }

    @Override
    public boolean isStaticLoadingEnabled() {
        return staticLoad;
    }

    @Override
    public void setStaticLoadingEnabled(boolean staticLoad) {
        LoaderCapabilitiesManagerImpl.staticLoad = staticLoad;
    }

    @Override
    public void initWithDocument() {
        // TODO Auto-generated method stub

    }

}
