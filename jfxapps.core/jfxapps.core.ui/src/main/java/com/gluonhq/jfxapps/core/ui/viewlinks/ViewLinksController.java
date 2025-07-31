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
package com.gluonhq.jfxapps.core.ui.viewlinks;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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
import com.gluonhq.jfxapps.core.api.ui.controller.misc.ViewLinks;

import jakarta.annotation.PostConstruct;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class ViewLinksController extends AbstractPanelController implements ViewLinks {

    private static final Logger logger = LoggerFactory.getLogger(ViewLinksController.class);

    //private final I18N i18n;
    private final DockViewController dockViewController;
    private final DockActionFactory dockActionFactory;
    private final VBox vBox = new VBox();
    private BiFunction<ViewAttachment, I18N, Node> linkCreator;
    private UnaryOperator<Region> regionCustomizer;
    private boolean populated;

    protected ViewLinksController(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents applicationInstanceEvents,
            DockViewController dockViewController,
            DockActionFactory dockActionFactory) {
        super(i18n, applicationEvents, applicationInstanceEvents, null);
        //this.i18n = i18n;
        this.dockViewController = dockViewController;
        this.dockActionFactory = dockActionFactory;
    }

    @PostConstruct
    public void init() {
        setRoot(vBox);
        vBox.setAlignment(Pos.TOP_CENTER);
    }

    private void populateViewLinks() {

        if (regionCustomizer != null) {
            regionCustomizer.apply(vBox);
        }

        dockViewController.getViewItems().stream()
        .filter(Predicate.not(ViewAttachment::isDebug))
        .sorted(Comparator.comparing(view -> view.getOrder()))
        .forEach(vi -> {

            var action = dockActionFactory.toggleViewVisibility(vi.getViewClass());
            var displayName = getI18n().getStringOrDefault(vi.getName(), vi.getName());

            final Node node;
            if (linkCreator != null) {
                node = linkCreator.apply(vi, getI18n());
            } else {

                URL icon = vi.getIconX2();
                if (icon == null) {
                    icon = View.VIEW_ICON_MISSING;
                }
                try {
                    Image image = new Image(icon.openStream());
                    ImageView imageView = new ImageView(image);
                    node = imageView;
                } catch (IOException e) {
                    logger.error("Unable to iconize view {}", vi.getId(), e);
                    return;
                }
            }

            if (node != null) {
                node.onMouseClickedProperty().set(e -> action.perform());
                Tooltip.install(node, new Tooltip(displayName));
                vBox.getChildren().add(node);
            }
        });
    }

    @Override
    public Parent getRoot() {

        if (!populated) {
            populateViewLinks();
            populated = true;
        }

        return super.getRoot();
    }

    @Override
    public void setLinkCreator(BiFunction<ViewAttachment, I18N, Node> linkCreator) {
        this.linkCreator = linkCreator;
    }

    @Override
    public void setRegionCustomizer(UnaryOperator<Region> regionCustomizer) {
        this.regionCustomizer = regionCustomizer;
    }

    @Override
    public void controllerDidLoadFxml() {
    }

}
