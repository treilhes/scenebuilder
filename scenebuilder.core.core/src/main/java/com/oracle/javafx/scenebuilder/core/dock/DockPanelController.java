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
package com.oracle.javafx.scenebuilder.core.dock;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.DockContext;
import com.oracle.javafx.scenebuilder.api.dock.DockType;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.dock.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager.DockRequest;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.DockMinimizedPreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockDockTypePreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DockPanelController implements Dock {

    private static final Logger logger = LoggerFactory.getLogger(DockPanelController.class);

    private final DockManager dockManager;
    private final List<DockType<?>> dockTypes;
    private final LastDockUuidPreference lastDockUuidPreference;
    private final LastDockDockTypePreference lastDockDockTypePreference;
    private final DockMinimizedPreference dockMinimizedPreference;

    private @Getter @Setter UUID id;
    private @Getter VBox content;

    private final ObservableMap<View, DockContext<?>> views = FXCollections.observableHashMap();
    private SceneBuilderWindow parentWindow;

    @SuppressWarnings("rawtypes")
    private ObjectProperty<DockType> dockTypeProperty;
    private ObjectProperty<View> focusedProperty;
    private BooleanProperty minimizedProperty;
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty();

    // @formatter:off
    public DockPanelController(
            DockManager dockManager,
            ViewManager viewManager,
            LastDockUuidPreference lastDockUuidPreference,
            LastDockDockTypePreference lastDockDockTypePreference,
            DockMinimizedPreference dockMinimizedPreference,
            List<DockType<?>> dockTypes) {
     // @formatter:on

        this.id = UUID.randomUUID();
        this.dockManager = dockManager;
        this.lastDockUuidPreference = lastDockUuidPreference;
        this.lastDockDockTypePreference = lastDockDockTypePreference;
        this.dockMinimizedPreference = dockMinimizedPreference;
        this.dockTypes = dockTypes;
        this.content = new VBox();

        VBox.setVgrow(this.content, Priority.ALWAYS);

        assert dockTypes != null && !dockTypes.isEmpty();

        Optional<DockType<?>> def = dockTypes.stream().filter(dt -> DockTypeSplitV.class.isInstance(dt)).findFirst();
        dockTypeProperty().set(def.orElse(dockTypes.get(0)));

        viewManager.dock()
                .filter(dr -> dr.getTarget().equals(this.getId()))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(dr -> viewAdded(dr));

        viewManager.undock()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(v -> viewDeleted(v));
    }

    public void notifyDockCreated() {
        dockManager.dockCreated().onNext(this);
    }

    private void updateViews() {
        views.replaceAll((v, d) -> {
            d.getDisposer().dispose();
            return dockTypeProperty().get().computeView(d);
        });
    }

    private void updateDockView(boolean minimized) {
        assert dockTypeProperty().isNotNull().get();
        getContent().getChildren().clear();

        if (views.isEmpty()) {
            return;
        }

        Node dockContent = null;

        if (minimized) {
            dockContent = computeMinimizedIcons();
        } else {
            dockContent = dockTypeProperty().get().computeRoot(views.values());//, focused);
        }

        VBox.setVgrow(dockContent, Priority.ALWAYS);
        getContent().getChildren().add(dockContent);
    }

    /**
     * @return
     */
    private Node computeMinimizedIcons() {

        final Pane icons;
        if (getId().equals(Dock.BOTTOM_DOCK_UUID)) {
            icons = new HBox();
        } else {
            icons = new VBox();
        }

        views.values().forEach(d -> {
            ViewAttachment va = d.getViewAttachment();
            URL icon = va.getIcon();
            if (icon == null) {
                icon = View.VIEW_ICON_MISSING;
            }
            try {
                Image image = new Image(icon.openStream());
                ImageView imageView = new ImageView(image);
                Button button = new Button();
                button.setGraphic(imageView);
                icons.getChildren().add(button);

                button.setOnAction(e -> {
                    setMinimized(false);
                    setFocused(d.getView());
                });
            } catch (IOException e) {
                logger.error("Unable to iconize view {}", d.getView().getId(), e);
            }
        });
        return icons;
    }

    private void viewDeleted(View view) {
        assert view != null;
        assert dockTypeProperty().isNotNull().get();
        assert dockTypes.size() > 0;

        DockContext<?> dockContext = views.get(view);
        if (dockContext != null) {
            dockContext.getDisposer().dispose();
            views.remove(view);
            view.parentDockProperty().set(null);
            viewDeleted(dockContext.getView());

            SbPlatform.runForDocumentLater(() -> {
                updateDockView(isMinimized());
            });
        }

        visibleProperty.set(!views.isEmpty());
    }

    private void viewAdded(DockRequest dr) {

        View view = dr.getSource();
        ViewAttachment viewAttachment = dr.getViewAttachment();
        boolean select = dr.isSelect();

        assert view != null;
        assert dockTypeProperty().isNotNull().get();
        assert dockTypes.size() > 0;

        lastDockUuidPreference.put(view.getId(), this.getId());
        lastDockUuidPreference.writeToJavaPreferences();
        view.parentDockProperty().set(this);

        SbPlatform.runForDocumentLater(() -> {
            DockContext initialContext = new DockContext(view, viewAttachment, null, null, null);
            var dockContext = dockTypeProperty().get().computeView(initialContext);
            views.put(view, dockContext);
            updateDockView(isMinimized());
            if (select) {
                focusedProperty().set(view);
            }
            visibleProperty.set(!views.isEmpty());
        });
    }

    @Override
    public boolean isWindow() {
        return parentWindow != null;
    }

    @Override
    public SceneBuilderWindow getParentWindow() {
        return parentWindow;
    }

    protected void setParentWindow(SceneBuilderWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    @Override
    public Collection<View> getViews() {
        return views.keySet();
    }

    @Override
    public BooleanProperty minimizedProperty() {
        if (minimizedProperty == null) {
            minimizedProperty = new SimpleBooleanProperty() {

                @Override
                public void set(boolean minimized) {
                    dockMinimizedPreference.getValue().put(DockPanelController.this.getId(), minimized);
                    updateViews();
                    updateDockView(minimized);
                    super.set(minimized);
                }

            };
        }
        return minimizedProperty;
    }

    @Override
    public ObjectProperty<View> focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(View focused) {
                    if (focused != null) {
                        DockContext<?> dockContext = views.get(focused);
                        dockTypeProperty().get().focusedProperty().set(dockContext);
                    }
                    super.set(focused);
                }

            };
        }

        return focusedProperty;
    }

    @Override
    public ObjectProperty<DockType> dockTypeProperty() {
        if (dockTypeProperty == null) {
            dockTypeProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(DockType dockType) {
                    if (dockType != null) {
                        lastDockDockTypePreference.getValue().put(DockPanelController.this.getId(), dockType.getNameKey());
                    }
                    super.set(dockType);
                    updateViews();
                    updateDockView(isMinimized());
                }

            };
        }

        return dockTypeProperty;
    }

    @Override
    public ReadOnlyBooleanProperty visibleProperty() {
        return visibleProperty;
    }
}
