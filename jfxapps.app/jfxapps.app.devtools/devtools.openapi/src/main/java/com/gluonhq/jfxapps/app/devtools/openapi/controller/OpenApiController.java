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
package com.gluonhq.jfxapps.app.devtools.openapi.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.devtools.api.ui.Docks;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.loader.extension.ApplicationExtension;
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.api.platform.InternalRestClient;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

@ApplicationInstanceSingleton
@ViewAttachment(
        name = "OpenApis",
        id = "88e8cd7f-9030-4cb0-bd0b-d78add0d4a0c",
        prefDockId = Docks.CENTER_DOCK_ID,
        openOnStart = false,
        selectOnStart = false,
        order = 4000,
        icon = "openapi_tool.png",
        iconX2 = "openapi_tool@2x.png"
        )
public class OpenApiController extends AbstractFxmlViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiController.class);
    private static final String OPENAPI_UI_PATH = "swagger-ui.html";

    @FXML
    VBox rootVbox;

    @FXML
    ChoiceBox<Api> extensionList;

    @FXML
    WebView webView;

    private final InternalRestClient restClient;
    private final ModuleLayerManager moduleLayerManager;
    private final ContextManager contextManager;

    //@formatter:off
    protected OpenApiController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenu,
            InternalRestClient restClient,
            ModuleLayerManager moduleLayerManager,
            ContextManager contextManager) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenu, OpenApiController.class.getResource("OpenApi.fxml"));

        this.restClient = restClient;
        this.moduleLayerManager = moduleLayerManager;
        this.contextManager = contextManager;
    }

    @FXML
    public void initialize() {

    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(OpenApiController.class.getSimpleName());
        getRoot().minWidth(400.0);
        getRoot().minHeight(400.0);
    }

    @Override
    public void onShow() {
        List<Api> apis = getApis();

        extensionList.setOnAction(null);

        extensionList.getItems().clear();
        populateChoiceBox(apis);

     // Bind the action to showApi(Api) on selection
        extensionList.setOnAction(event -> {
            Api selectedApi = extensionList.getSelectionModel().getSelectedItem();
            if (selectedApi != null) {
                showApi(selectedApi);
            }
        });
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

    private void populateChoiceBox(List<Api> apis) {
        apis.forEach(api -> {
            extensionList.getItems().add(api);
            populateChoiceBox(api.getChildren());
        });
    }

    private void showApi(Api selectedApi) {
        webView.getEngine().load(selectedApi.getUrl());
    }

    private List<Api> getApis() {
        var list = new ArrayList<Api>();

        var bootApiUri = getUri(null);
        var bootName = "BOOT - " + bootApiUri.toString();
        var bootApi = new Api(bootName, bootApiUri.toString());
        list.add(bootApi);

        var rootLayer = moduleLayerManager.get(Extension.ROOT_ID);
        var rootContext = contextManager.get(Extension.ROOT_ID);
        var rootApiUri = getUri(Extension.ROOT_ID);
        var name = createDisplayName(rootContext, rootApiUri);
        var rootApi = new Api(name, rootApiUri.toString());
        list.add(rootApi);

        var applications = listApplications(rootLayer);

        populateChildLayers(rootLayer, rootApi, applications, 1);


        for (UUID applicationId : applications) {
            var appLayer = moduleLayerManager.get(applicationId);
            var appContext = contextManager.get(applicationId);
            var appUri = getUri(applicationId);
            var appName = createDisplayName(appContext, appUri);
            var appApi = new Api(appName, appUri.toString());
            list.add(appApi);

            populateChildLayers(appLayer, appApi, List.of(), 1);
        }

        return list;
    }

    private String createDisplayName(JfxAppContext context, URI uri) {
        return context.getLocalBean(Extension.class).getClass().getModule().getName() + " - " + uri.toString();
    }

    private URI getUri(UUID layerId) {
        try {
            return restClient.get(layerId, OPENAPI_UI_PATH).getUri();
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Error while fetching OpenAPI UI URI of " + layerId, e);
            return null;
        }
    }

    private void populateChildLayers(Layer layer, Api rootApi, List<UUID> excluded, int depth) {
        for (var childLayer : layer.getChildren()) {
            if (!excluded.contains(childLayer.getId())) {
                final var context = contextManager.get(childLayer.getId());
                final URI extApiUri = getUri(childLayer.getId());
                final String name = "   ".repeat(depth) + createDisplayName(context, extApiUri);
                var childApi = new Api(name, extApiUri.toString());
                rootApi.addChild(childApi);
                populateChildLayers(childLayer, childApi, List.of(), depth + 1);
            }
        }
    }

    private List<UUID> listApplications(Layer rootLayer) {
        var list = new ArrayList<UUID>();
        for (var layer : rootLayer.getChildren()) {
            var context = contextManager.get(layer.getId());
            var extension = context.getLocalBean(Extension.class);

            if (extension instanceof ApplicationExtension) {
                list.add(layer.getId());
            }
        }
        return list;
    }

    private static class Api {
        private final String name;;
        private final String url;
        private final List<Api> children;

        public Api(String name, String url) {
            this.name = name;
            this.url = url;
            this.children = new ArrayList<>();
        }

        public void addChild(Api api) {
            children.add(api);
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public List<Api> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
