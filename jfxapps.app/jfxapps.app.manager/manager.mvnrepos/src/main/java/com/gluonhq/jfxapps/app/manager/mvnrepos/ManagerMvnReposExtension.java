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
package com.gluonhq.jfxapps.app.manager.mvnrepos;

import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.app.manager.api.ManagerApiExtension;
import com.gluonhq.jfxapps.app.manager.mvnrepos.controller.EditRepositoryItemController;
import com.gluonhq.jfxapps.app.manager.mvnrepos.controller.RepositoryController;
import com.gluonhq.jfxapps.app.manager.mvnrepos.controller.RepositoryItemController;
import com.gluonhq.jfxapps.app.manager.mvnrepos.i18n.I18NManagerMvnRepos;
import com.gluonhq.jfxapps.app.manager.mvnrepos.model.RepositoryMapperImpl;
import com.gluonhq.jfxapps.app.manager.mvnrepos.model.RepositoryModelController;
import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;

/**
 * The Class ManagerStoreExtension defines the extension for the Store module.
 *
 */
public class ManagerMvnReposExtension implements OpenExtension  {

    /** The Constant ID. */
    public final static UUID ID = UUID.fromString("fa80ba52-4350-4155-b968-1d1f69c9c5eb");


    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    @Override
    public UUID getParentId() {
        return ManagerApiExtension.ID;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    @Override
    public UUID getId() {
        return ID;
    }

    /**
     * Exported context classes.
     *
     * @return the list
     */
    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(
                RepositoryController.class,
                RepositoryItemController.class,
                RepositoryModelController.class,
                EditRepositoryItemController.class,
                I18NManagerMvnRepos.class,
                RepositoryMapperImpl.class
                );
    }

    /**
     * Local context classes.
     *
     * @return the list
     */
    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

}
