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
package com.gluonhq.jfxapps.core.ui.controller;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.FxThread;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.WindowPreferenceTracker;
import com.gluonhq.jfxapps.core.ui.preferences.document.MaximizedPreference;
import com.gluonhq.jfxapps.core.ui.preferences.document.StageHeightPreference;
import com.gluonhq.jfxapps.core.ui.preferences.document.StageWidthPreference;
import com.gluonhq.jfxapps.core.ui.preferences.document.XPosPreference;
import com.gluonhq.jfxapps.core.ui.preferences.document.YPosPreference;

import jakarta.inject.Provider;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

/**
 *
 */
@ApplicationInstanceSingleton
public class ApplicationWindowTracker implements WindowPreferenceTracker { // , InitializingBean {

    // PREFERENCES
    private final Provider<XPosPreference> xPosPreference;
    private final Provider<YPosPreference> yPosPreference;
    private final Provider<StageHeightPreference> stageHeightPreference;
    private final Provider<StageWidthPreference> stageWidthPreference;
    private final Provider<MaximizedPreference> maximizedWindowPreference;

    /*
     * DocumentWindowController
     */
    private MainInstanceWindow windowInstance;

    private ChangeListener<? super Number> xPropertyListener;
    private ChangeListener<? super Number> yPropertyListener;
    private ChangeListener<? super Double> xPosPreferenceListener;
    private ChangeListener<? super Double> yPosPreferenceListener;
    private ChangeListener<? super Number> heightPropertyListener;
    private ChangeListener<? super Double> stageHeightPreferenceListener;
    private ChangeListener<? super Number> widthPropertyListener;
    private ChangeListener<? super Double> stageWidthPreferenceListener;
    private ChangeListener<? super Boolean> maximizedPropertyListener;
    private ChangeListener<? super Boolean> maximizedPreferenceListener;

    // @formatter:off
    public ApplicationWindowTracker(
            //SceneBuilderManager sceneBuilderManager,
            //IconSetting iconSetting,
            Provider<XPosPreference> xPos,
            Provider<YPosPreference> yPos,
            Provider<StageHeightPreference> stageHeight,
            Provider<StageWidthPreference> stageWidth,
            Provider<MaximizedPreference> maximizedWindow) {

        // @formatter:on

        // preferences
        this.xPosPreference = xPos;
        this.yPosPreference = yPos;
        this.stageHeightPreference = stageHeight;
        this.stageWidthPreference = stageWidth;
        this.maximizedWindowPreference = maximizedWindow;
    }

    @Override
    @FxThread
    public void initialize(MainInstanceWindow windowInstance) {
        this.windowInstance = windowInstance;
        System.out.println( this + "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuNNNNNNNNNNNNNNNNNNNNNNN");
        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuNNNNNNNNNNNNNNNNNNNNNNN");
        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuNNNNNNNNNNNNNNNNNNNNNNN");
        xPropertyListener = (ob, o, n) -> {
            if (!windowInstance.getStage().isMaximized()) {
                xPosPreference.get().setValue(n.doubleValue());
            }
        };
        xPosPreferenceListener = (ob, o, n) -> windowInstance.getStage().setX(n);

        yPropertyListener = (ob, o, n) -> {
            if (!windowInstance.getStage().isMaximized()) {
                yPosPreference.get().setValue(n.doubleValue());
            }
        };
        yPosPreferenceListener = (ob, o, n) -> windowInstance.getStage().setY(n);

        heightPropertyListener = (ob, o, n) -> {
            if (!windowInstance.getStage().isMaximized()) {
                stageHeightPreference.get().setValue(n.doubleValue());
            }
        };
        stageHeightPreferenceListener = (ob, o, n) -> windowInstance.getStage().setHeight(n);

        widthPropertyListener = (ob, o, n) -> {
            if (!windowInstance.getStage().isMaximized()) {
                stageWidthPreference.get().setValue(n.doubleValue());
            }
        };
        stageWidthPreferenceListener = (ob, o, n) -> windowInstance.getStage().setWidth(n);

        maximizedPropertyListener = (ob, o, n) -> {
            maximizedWindowPreference.get().setValue(n);
            if (!n) {
                windowInstance.getStage().setWidth(stageWidthPreference.get().getValue());
                windowInstance.getStage().setHeight(stageHeightPreference.get().getValue());
            }
        };
        maximizedPreferenceListener = (ob, o, n) -> {
            // System.out.println("MAXIMIZING " + n);
            windowInstance.getStage().setMaximized(n);
        };

//      getStage().maximizedProperty().addListener((ob, o, n) -> System.out.println("MAX" + n));
//      getStage().maximizedProperty().addListener((n) -> System.out.println("MAXINV" + n));

        if (stageHeightPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setHeight(stageHeightPreference.get().getValue());
        }
        if (stageWidthPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setWidth(stageWidthPreference.get().getValue());
        }
        if (xPosPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setX(xPosPreference.get().getValue());
        }
        if (yPosPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setY(yPosPreference.get().getValue());
        }
        if (maximizedWindowPreference.get().isValid()) {
            windowInstance.getStage().setMaximized(maximizedWindowPreference.get().getValue());
        }

    }

    @Override
    public void onClose() {
        untrackMaximizedOnly();
    }

    @Override
    public void apply() {
//      getStage().maximizedProperty().addListener((ob, o, n) -> System.out.println("MAX" + n));
//      getStage().maximizedProperty().addListener((n) -> System.out.println("MAXINV" + n));

        if (stageHeightPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setHeight(stageHeightPreference.get().getValue());
        }
        if (stageWidthPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setWidth(stageWidthPreference.get().getValue());
        }
        if (xPosPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setX(xPosPreference.get().getValue());
        }
        if (yPosPreference.get().isValid() && !maximizedWindowPreference.get().getValue()) {
            windowInstance.getStage().setY(yPosPreference.get().getValue());
        }
        if (maximizedWindowPreference.get().isValid()) {
            windowInstance.getStage().setMaximized(maximizedWindowPreference.get().getValue());
        }
    }

    @Override
    public void track() {
        // Add stage x and y listeners
        windowInstance.getStage().xProperty().addListener(xPropertyListener);
        xPosPreference.get().getObservableValue().addListener(xPosPreferenceListener);

        windowInstance.getStage().yProperty().addListener(yPropertyListener);
        yPosPreference.get().getObservableValue().addListener(yPosPreferenceListener);

        // Add stage height and width listeners
        windowInstance.getStage().heightProperty().addListener(heightPropertyListener);
        stageHeightPreference.get().getObservableValue().addListener(stageHeightPreferenceListener);

        windowInstance.getStage().widthProperty().addListener(widthPropertyListener);
        stageWidthPreference.get().getObservableValue().addListener(stageWidthPreferenceListener);

        windowInstance.getStage().maximizedProperty().addListener(maximizedPropertyListener);
        maximizedWindowPreference.get().getObservableValue().addListener(maximizedPreferenceListener);
    }

    @Override
    public void untrack() {
        // Remove stage x and y listeners
        windowInstance.getStage().xProperty().removeListener(xPropertyListener);
        xPosPreference.get().getObservableValue().removeListener(xPosPreferenceListener);

        windowInstance.getStage().yProperty().removeListener(yPropertyListener);
        yPosPreference.get().getObservableValue().removeListener(yPosPreferenceListener);

        // Remove stage height and width listeners
        windowInstance.getStage().heightProperty().removeListener(heightPropertyListener);
        stageHeightPreference.get().getObservableValue().removeListener(stageHeightPreferenceListener);

        windowInstance.getStage().widthProperty().removeListener(widthPropertyListener);
        stageWidthPreference.get().getObservableValue().removeListener(stageWidthPreferenceListener);

        windowInstance.getStage().maximizedProperty().removeListener(maximizedPropertyListener);
        maximizedWindowPreference.get().getObservableValue().removeListener(maximizedPreferenceListener);
    }

    /**
     * Used before closing the window to keep the real size and position of the
     * window as preferences. If the window is in the maximized state then
     * x,y,width,height match the screen size So opening the same document in
     * another instance will size the window with the screen values and with the
     * maximized state. When unmaximizing the size of the window will stay the same
     */
    private void untrackMaximizedOnly() {
        Stage stage = windowInstance.getStage();
        stage.maximizedProperty().removeListener(maximizedPropertyListener);
        maximizedWindowPreference.get().getObservableValue().removeListener(maximizedPreferenceListener);
        stage.setMaximized(false);
    }


}
