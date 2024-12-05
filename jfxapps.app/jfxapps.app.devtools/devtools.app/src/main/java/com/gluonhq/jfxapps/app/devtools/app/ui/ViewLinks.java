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
package com.gluonhq.jfxapps.app.devtools.app.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.DockActionFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractPanelController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;

import jakarta.annotation.PostConstruct;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class ViewLinks extends AbstractPanelController {

    private static final Logger logger = LoggerFactory.getLogger(ViewLinks.class);

    private final I18N i18n;
    private final DockViewController dockViewController;
    private final DockActionFactory dockActionFactory;
    private final VBox vBox = new VBox();
    protected ViewLinks(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents applicationInstanceEvents,
            DockViewController dockViewController,
            DockActionFactory dockActionFactory) {
        super(applicationEvents, applicationInstanceEvents);
        this.i18n = i18n;
        this.dockViewController = dockViewController;
        this.dockActionFactory = dockActionFactory;
    }

    @PostConstruct
    public void init() {
        setRoot(vBox);

        vBox.setAlignment(Pos.TOP_CENTER);

        dockViewController.getViewItems().stream()
        .filter(Predicate.not(ViewAttachment::isDebug))
        .sorted(Comparator.comparing(view -> view.getOrder()))
        .forEach(vi -> {

            String displayName = i18n.getStringOrDefault(vi.getName(), vi.getName());
            var action = dockActionFactory.toggleViewVisibility(vi.getViewClass());

            URL icon = vi.getIconX2();
            if (icon == null) {
                icon = View.VIEW_ICON_MISSING;
            }
            try {
                Image image = new Image(icon.openStream());
                ImageView imageView = new ImageView(image);
                imageView.onMouseClickedProperty().set(e -> action.perform());
                Tooltip.install(imageView, new Tooltip(displayName));
                vBox.getChildren().add(imageView);
            } catch (IOException e) {
                logger.error("Unable to iconize view {}", vi.getId(), e);
            }

        });
    }
}
