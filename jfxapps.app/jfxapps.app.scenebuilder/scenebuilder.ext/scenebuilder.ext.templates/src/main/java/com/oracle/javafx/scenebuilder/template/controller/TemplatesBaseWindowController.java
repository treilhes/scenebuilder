/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.template.controller;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public abstract class TemplatesBaseWindowController extends AbstractFxmlWindowController {

    private final static double TEMPLATE_MAX_WIDTH = 240;
    private final static double TEMPLATE_MAX_HEIGHT = 180;

    private Consumer<Template> onTemplateChosen;

    private List<TemplateGroup> templateGroups;

    private List<Template> templates;

    @FXML
    private VBox templateContainer;

    public TemplatesBaseWindowController(SceneBuilderManager sceneBuilderManager, IconSetting iconSetting, URL fxmlURL,
            ResourceBundle resources, SceneBuilderWindow owner, List<TemplateGroup> templateGroups,
            List<Template> templates) {
        super(sceneBuilderManager, iconSetting, fxmlURL, resources, owner);
        this.templates = templates;
        this.templateGroups = templateGroups;

    }

    @Override
    public void onCloseRequest() {
        getStage().hide();
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;
    }

    private void buildTemplateCategory(TemplateGroup templateGroup) {

        TemplateHeader header = new TemplateHeader();
        templateContainer.getChildren().add((Node) FXMLUtils.load(header, TemplateHeader.SOURCE));

        header.getLabel().setText(I18N.getStringOrDefault(templateGroup.getName(), templateGroup.getName()));

    }

    private void buildTemplate(TemplateCategoryContent category, Template template) {
        Node itemNode = null;
        double ratio = 1;

        if (template.getWidth() > template.getHeight()) {
            ratio = template.getWidth() / TEMPLATE_MAX_WIDTH;
        } else {
            ratio = template.getHeight() / TEMPLATE_MAX_HEIGHT;
        }

        double width = template.getWidth() / ratio;
        double height = template.getHeight() / ratio;

        String sizeString = " " + template.getWidth() + "x" + template.getHeight();

        if (template.getFxmlUrl() != null && template.getIconUrl() != null) {
            TemplateItem item = new TemplateItem();
            itemNode = (Node) FXMLUtils.load(item, TemplateItem.SOURCE);

            item.getImage().setImage(ImageUtils.getImage(template.getIconUrl()));
            item.getButton().setUserData(template);
            item.getButton().setText(I18N.getStringOrDefault(template.getName(), template.getName()) + sizeString);

            item.getImage().setFitWidth(width);
            item.getImage().setFitHeight(height);
        } else {
            TemplateEmptyItem item = new TemplateEmptyItem();
            itemNode = (Node) FXMLUtils.load(item, TemplateEmptyItem.SOURCE);
            item.getButton().setUserData(template);
            item.getButton().setText(I18N.getStringOrDefault(template.getName(), template.getName()) + sizeString);

            item.getRectangle().setWidth(width);
            item.getRectangle().setHeight(height);
        }

        category.getFlowPane().getChildren().add(itemNode);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert templateContainer != null;

        templateGroups.stream().sorted(Comparator.comparing(TemplateGroup::getOrderKey)
                .thenComparing(Comparator.comparing(TemplateGroup::getName))).forEachOrdered(tg -> {

                    List<Template> subTemplates = templates.stream().filter(t -> t.getGroup().equals(tg))
                            .sorted(Comparator.comparing(Template::getOrderKey)
                                    .thenComparing(Comparator.comparing(Template::getName)))
                            .collect(Collectors.toList());

                    if (!subTemplates.isEmpty()) {

                        buildTemplateCategory(tg);

                        TemplateCategoryContent category = new TemplateCategoryContent();
                        templateContainer.getChildren()
                                .add((Node) FXMLUtils.load(category, TemplateCategoryContent.SOURCE));

                        subTemplates.stream().filter(t -> t.getGroup().equals(tg))
                                .sorted(Comparator.comparing(Template::getOrderKey)
                                        .thenComparing(Comparator.comparing(Template::getName)))
                                .forEachOrdered(t -> buildTemplate(category, t));
                    }
                });
    }

    protected void setupTemplateButtonHandlers() {
        setupTemplateButtonHandlers(templateContainer);
    }

    private void setupTemplateButtonHandlers(Parent templateContainer) {
        for (Node child : templateContainer.getChildrenUnmodifiable()) {
            if (!(child instanceof Button) && child instanceof Parent) {
                setupTemplateButtonHandlers((Parent) child);
            }
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnAction(event -> {
                    getStage().hide();
                    onTemplateChosen.accept((Template) button.getUserData());
                });
            }
        }
    }

    public void setOnTemplateChosen(Consumer<Template> onTemplateChosen) {
        this.onTemplateChosen = onTemplateChosen;
    }

    public static class TemplateItem {
        public static final String SOURCE = "TemplateItem.fxml";
        @FXML
        private Button button;
        @FXML
        private ImageView image;

        public Button getButton() {
            return button;
        }

        public ImageView getImage() {
            return image;
        }

    }

    public static class TemplateEmptyItem {
        public static final String SOURCE = "TemplateEmptyItem.fxml";
        @FXML
        private Button button;
        @FXML
        private Rectangle rectangle;

        public Button getButton() {
            return button;
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

    }

    public static class TemplateCategoryContent {
        public static final String SOURCE = "TemplateCategoryContent.fxml";
        @FXML
        private FlowPane flowPane;

        public FlowPane getFlowPane() {
            return flowPane;
        }

    }

    public static class TemplateHeader {
        public static final String SOURCE = "TemplateHeader.fxml";
        @FXML
        private Label label;

        public Label getLabel() {
            return label;
        }

    }
}
