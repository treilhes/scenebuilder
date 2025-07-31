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
package com.gluonhq.jfxapps.app.manager.store.ui.root;

import java.util.stream.Collectors;

import com.gluonhq.jfxapps.app.manager.store.model.Application;
import com.gluonhq.jfxapps.app.manager.store.model.ApplicationController;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.api.loader.OpenCommandEvent;
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.core.api.i18n.I18N;

@ApplicationInstanceSingleton
public class RootModelControllerImpl implements ApplicationController{

    private final RegistryManager registryManager;
    private final I18N i18n;
    private RootModel<Application, Application> model;
    private ApplicationManager appManager;

    public RootModelControllerImpl(I18N i18n, RegistryManager registryManager, ApplicationManager appManager) {
        this.i18n = i18n;
        this.registryManager = registryManager;
        this.appManager = appManager;
    }

    public RootModel<Application, Application> load() {

        model = new RootModel<>();

        var rootInfo = registryManager.applicationInfo(Extension.ROOT_ID);

        model.getItem().set(new Application(rootInfo, i18n));

        var apps = registryManager.listApplicationsInfo();

        var sourceItems = apps.stream()
                .filter(a -> !Extension.ROOT_ID.equals(a.getUuid()))
                .map(a -> new Application(a, i18n))
                .collect(Collectors.partitioningBy(a -> a.infoProperty().get().isInstalled()));

        model.getAvailables().setAll(sourceItems.get(false));
        model.getInstalled().setAll(sourceItems.get(true));

        return model;
    }

    @Override
    public void install(Application item) {
        registryManager.install(item.infoProperty().get());
        item.installedProperty().set(true);
        model.getAvailables().remove(item);
        model.getInstalled().add(0, item);
    }

    @Override
    public void uninstall(Application item) {
        registryManager.uninstall(item.infoProperty().get());
        item.installedProperty().set(false);
        model.getInstalled().remove(item);
        model.getAvailables().add(0, item);
    }

    public void update(Application application) {
        registryManager.update(application.infoProperty().get());
        application.versionProperty().set(application.nextVersionProperty().get());
    }

    @Override
    public void launch(Application application) {
        var uuid = application.infoProperty().get().getUuid();
        var openCommandEvent = new OpenCommandEvent(uuid, null);
        appManager.startApplication(uuid);
        appManager.send(openCommandEvent);
    }
}
