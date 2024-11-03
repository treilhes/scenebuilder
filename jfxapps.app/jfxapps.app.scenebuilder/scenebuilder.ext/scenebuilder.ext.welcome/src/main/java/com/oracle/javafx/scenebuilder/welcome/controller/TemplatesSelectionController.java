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

package com.oracle.javafx.scenebuilder.welcome.controller;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class TemplatesSelectionController extends AbstractFxmlController {

    private static final Comparator<TemplateGroup> GROUP_COMPARATOR = Comparator.comparing(TemplateGroup::getOrderKey)
            .thenComparing(Comparator.comparing(TemplateGroup::getName));

    private static final Comparator<Template> TEMPLATE_COMPARATOR = Comparator.comparing(Template::getOrderKey)
            .thenComparing(Comparator.comparing(Template::getName));

    private final static Logger logger = LoggerFactory.getLogger(TemplatesSelectionController.class);

    private final static double TEMPLATE_MAX_WIDTH = 240;
    private final static double TEMPLATE_MAX_HEIGHT = 180;

    private Consumer<Template> onTemplateChosen;

    private List<TemplateGroup> templateGroups;
    private List<Template> templates;

    @FXML
    private VBox templateContainer;

    //@formatter:off
    public TemplatesSelectionController(
            I18N i18n,
            ApplicationEvents sceneBuilderManager,
            ApplicationInstanceEvents applicationInstanceEvents,
            List<TemplateGroup> templateGroups,
            List<Template> templates) {
        //@formatter:on
        super(i18n, sceneBuilderManager, applicationInstanceEvents,
                TemplatesSelectionController.class.getResource("TemplatesSelection.fxml"));
        this.templates = templates;
        this.templateGroups = templateGroups;
    }

    @Override
    public void controllerDidLoadFxml() {
        assert getRoot() != null;
        assert templateContainer != null;

        templateGroups.stream().sorted(GROUP_COMPARATOR).forEachOrdered(tg -> {

            List<Template> subTemplates = templates.stream().filter(t -> t.getGroup().equals(tg))
                    .sorted(TEMPLATE_COMPARATOR).collect(Collectors.toList());

            if (!subTemplates.isEmpty()) {

                addCategoryHeader(tg);

                var templatesContainer = addTemplateListContainer();

                subTemplates.forEach(t -> addTemplateInCategory(templatesContainer, t));
            }
        });
    }

    private TemplateList addTemplateListContainer() {
        var templateList = new TemplateList();
        var templateListNode = templateList.loadNode();
        templateContainer.getChildren().add(templateListNode);
        return templateList;
    }

    private void addCategoryHeader(TemplateGroup templateGroup) {
        var categoryHeaderText = getI18n().getStringOrDefault(templateGroup.getName(), templateGroup.getName());
        var categoryHeader = new CategoryHeader();
        var categoryHeaderNode = categoryHeader.loadNode();

        categoryHeader.getLabel().setText(categoryHeaderText);

        templateContainer.getChildren().add(categoryHeaderNode);
    }

    private void addTemplateInCategory(TemplateList category, Template template) {

        double ratio = 1;

        if (template.getWidth() > template.getHeight()) {
            ratio = template.getWidth() / TEMPLATE_MAX_WIDTH;
        } else {
            ratio = template.getHeight() / TEMPLATE_MAX_HEIGHT;
        }

        double width = template.getWidth() / ratio;
        double height = template.getHeight() / ratio;
        String name = getI18n().getStringOrDefault(template.getName(), template.getName());
        String sizeString = "\n" + template.getWidth() + "x" + template.getHeight();
        String description = getI18n().getStringOrDefault(template.getDescription(), template.getDescription());
        description += sizeString;

        if (template.getIconUrl() != null) {
            TemplateItem item = new TemplateItem();
            var itemNode = item.loadNode();

            var button = item.getButton();
            var image = item.getImage();
            var desc = item.getDescription();

            image.setImage(ImageUtils.getImage(template.getIconUrl()));
            image.setFitWidth(width);
            image.setFitHeight(height);

            button.setUserData(template);
            button.setText(name);
            button.setOnAction(event -> {
                onTemplateChosen.accept((Template) button.getUserData());
            });

            desc.setText(description);

            category.getFlowPane().getChildren().add(itemNode);
        } else {
            logger.error("Template {}/{} has no icon", template.getId(), template.getName());
        }

    }

    public void setOnTemplateChosen(Consumer<Template> onTemplateChosen) {
        this.onTemplateChosen = onTemplateChosen;
    }

    public void clearFromParent() {
        if (templateContainer.getParent() != null && templateContainer.getParent() instanceof Pane pane) {
            pane.getChildren().remove(templateContainer);
        }
    }

    public static class TemplateItem {
        public static final String SOURCE = "TemplatesSelection_TemplateItem.fxml";
        @FXML
        private Button button;
        @FXML
        private ImageView image;
        @FXML
        private Label description;

        public Button getButton() {
            return button;
        }

        public ImageView getImage() {
            return image;
        }

        public Label getDescription() {
            return description;
        }

        public Node loadNode() {
            return (Node) FXMLUtils.load(this, TemplateItem.SOURCE);
        }
    }

    public static class TemplateList {
        public static final String SOURCE = "TemplatesSelection_TemplateList.fxml";
        @FXML
        private FlowPane flowPane;

        public FlowPane getFlowPane() {
            return flowPane;
        }

        public Node loadNode() {
            return (Node) FXMLUtils.load(this, TemplateList.SOURCE);
        }
    }

    public static class CategoryHeader {
        public static final String SOURCE = "TemplatesSelection_CategoryHeader.fxml";
        @FXML
        private Label label;

        public Label getLabel() {
            return label;
        }

        public Node loadNode() {
            return (Node) FXMLUtils.load(this, CategoryHeader.SOURCE);
        }
    }

}
