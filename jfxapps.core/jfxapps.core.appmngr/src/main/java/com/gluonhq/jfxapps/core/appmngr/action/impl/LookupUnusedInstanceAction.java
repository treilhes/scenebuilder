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
package com.gluonhq.jfxapps.core.appmngr.action.impl;

import java.net.URL;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationPrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.ApplicationActionFactory;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;

@ApplicationPrototype("com.gluonhq.jfxapps.core.appmngr.action.impl.LookupUnusedInstanceAction")
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")
//FIXME better to move it in a container related module
public class LookupUnusedInstanceAction extends AbstractAction {

    //public static final String NEWFILE_MENU_ID = "newMenu";
    private static final Logger logger = LoggerFactory.getLogger(LookupUnusedInstanceAction.class);

    private final InstancesManager main;
    private final ApplicationActionFactory applicationActionFactory;
    private final JfxAppPlatform jfxAppPlatform;

    private Consumer<ApplicationInstance> consumer;

    private URL location;

    public LookupUnusedInstanceAction(
    // @formatter:off
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ApplicationActionFactory applicationActionFactory,
            InstancesManager main,
            JfxAppPlatform jfxAppPlatform) {
    // @formatter:on
        super(i18n, extensionFactory);
        this.main = main;
        this.applicationActionFactory = applicationActionFactory;
        this.jfxAppPlatform = jfxAppPlatform;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        final ApplicationInstance locationInstance = location != null ? main.lookupInstance(location) : null;

        if (locationInstance != null) {
            logger.info("Location {} is already opened, focusing", location);
            // location is already opened
            locationInstance.getDocumentWindow().getStage().toFront();
        } else {

            final ApplicationInstance unusedInstance = main.lookupUnusedInstance();

            if (unusedInstance == null) {
                logger.info("Assign {} to new instance", location);
                return applicationActionFactory.newInstance(consumer).perform();
            }

            logger.info("Assign {} to unused instance", location);
            if (consumer != null) {
                consumer.accept(unusedInstance);
            }
        }
        return ActionStatus.DONE;
    }

    public void setParameters(URL location, Consumer<ApplicationInstance> consumer) {
        this.location = location;
        this.consumer = consumer;
    }

}