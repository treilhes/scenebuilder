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
package com.oracle.javafx.scenebuilder.core.ui.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.preferences.type.DoublePreference;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.VBox;

/**
 * This class keep track of the dock divider position and set the the according preference value.
 * SplitPanes update dividers for each items change so divider's listener must be updated accordingly on each change
 * This class must handle 3 possible states:
 * - Deleted : when the dock is empty, the host is removed from the split pane and no tracking must be done
 * - Visible : when the dock contains some view(s), the host exists in split pane children and tracking must be active
 * - Minimized : when the dock is minimized, the divider must be frozen at a minimal position, the divider position preference must be frozen at the last unminimized value and must be restored when minimization is finished
 */
public class InnerDockManager {

    private static final Logger logger = LoggerFactory.getLogger(InnerDockManager.class);
    private static final double MINIMIZED_SIZE = 36;

    public enum DividerPosition{
        BEFORE,
        AFTER
    }

    private final JfxAppPlatform jfxAppPlatform;
    private final Dock dock;
    private final VBox dockHost;
    private final SplitPane splitPane;
    private final DividerPosition dividerPosition;
    private final DoublePreference dividerPositionPreference;

    private ChangeListener<? super Number> dividerListener;
    private ChangeListener<? super Double> preferenceListener;

    private Divider trackedDivider;

    //double dividerMinimizedValue;

    public InnerDockManager(JfxAppPlatform jfxAppPlatform ,Dock dock, VBox dockHost, SplitPane splitPane, DividerPosition dividerPosition,
            DoublePreference dividerPositionPreference) {
        super();
        this.jfxAppPlatform = jfxAppPlatform;
        this.dock = dock;
        this.dockHost = dockHost;
        this.splitPane = splitPane;
        this.dividerPosition = dividerPosition;
        this.dividerPositionPreference = dividerPositionPreference;

        //dividerMinimizedValue = dividerPosition == DividerPosition.AFTER ? 0 : 1;

        initialize();
    }

    private void initialize() {
        dock.minimizedProperty().addListener((ob, o, n) -> {
            setMinimized(n);
        });
        splitPane.getDividers().addListener((Change<? extends Divider> c) -> {
            untrackPreference();

            int numItems = splitPane.getItems().size();
            int numDividers = splitPane.getDividers().size();
            if (numDividers == numItems - 1 ) { // discarding intermediate changes
                Divider divider = findCurrentDivider();

                if (divider != null) {
                    if (dock.isMinimized()) {
                        double computeMinDividerPosition = computeMinDividerPosition();

                        logger.debug("DivEvent: Setting {}/{} dividerPositionPreference position to minmized value {}", splitPane.getId(), dockHost.getId(), computeMinDividerPosition);
                        divider.setPosition(computeMinDividerPosition);
                    } else {
                        logger.debug("DivEvent: Setting {}/{} dividerPositionPreference position to {}", splitPane.getId(), dockHost.getId(), dividerPositionPreference.getValue());
                        divider.setPosition(dividerPositionPreference.getValue());
                    }

                    trackPreference(divider);
                }
            }


        });
    }

    public void setMinimized(boolean minimize) {
        if (minimize) {
            minimize();
        } else {
            maximize();
        }
    }

    public void minimize() {

        Divider divider = findCurrentDivider();

        if (divider != null) {

            untrackPreference();
            dock.setMinimized(true);
            double computeMinDividerPosition = computeMinDividerPosition();

            logger.debug("Setting {}/{} dividerPositionPreference position to minmized value {}", splitPane.getId(), dockHost.getId(), computeMinDividerPosition);

            divider.positionProperty().addListener((ob, o, n) -> divider.setPosition(computeMinDividerPosition));
            divider.setPosition(computeMinDividerPosition);

        }

    }

    public void maximize() {
        Divider divider = findCurrentDivider();

        if (divider != null) {
            double position = dividerPositionPreference.getValue();

            trackPreference(divider);
            dock.setMinimized(false);

            jfxAppPlatform.runOnFxThread(() -> {
                logger.debug("Delayed setting divider position to {} of divider {}", position, divider);
                divider.setPosition(position);
            });
        }
    }

    public void trackPreference() {

        Divider divider = findCurrentDivider();

        if (divider != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Enabled divider position tracking for divider index {}/{}", splitPane.getDividers().indexOf(divider), divider);
            }
            trackPreference(divider);
        }


    }

    public void untrackPreference() {

        if (trackedDivider != null || preferenceListener != null) {
            logger.debug("Disabled {}/{} divider position tracking for divider {}", splitPane.getId(), dockHost.getId(), trackedDivider);

            if (trackedDivider != null && dividerListener != null) {
                trackedDivider.positionProperty().removeListener(dividerListener);
            }
            if (preferenceListener != null) {
                dividerPositionPreference.getObservableValue().removeListener(preferenceListener);
            }
            dividerListener = null;
            preferenceListener = null;
            trackedDivider = null;
        }

    }

    private void trackPreference(Divider divider) {

        logger.debug("Enabled {}/{} divider position tracking for divider {}", splitPane.getId(), dockHost.getId(), divider);

        dividerListener = (ob, o, n) -> {
            if (dock.isMinimized()) {
                divider.setPosition(computeMinDividerPosition());
            } else {
                logger.debug("Setting {}/{} dividerPositionPreference position to {}", splitPane.getId(), dockHost.getId(), n.doubleValue());
                dividerPositionPreference.setValue(n.doubleValue());
            }
        };

        preferenceListener = (ob, o, n) -> {
            logger.debug("Setting {}/{} divider position to {} of divider {}", splitPane.getId(), dockHost.getId(), n.doubleValue(), divider);
            divider.setPosition(n);
        };

        trackedDivider = divider;
        trackedDivider.positionProperty().addListener(dividerListener);
        dividerPositionPreference.getObservableValue().addListener(preferenceListener);
    }



    public void applyPreference() {
        if (trackedDivider != null) {
            logger.debug("Applied divider position preference {}", dividerPositionPreference.getValue());
            trackedDivider.setPosition(dividerPositionPreference.getValue());
        }
    }

    private Divider findCurrentDivider() {
        var dividers = splitPane.getDividers();
        int dockHostIndex = splitPane.getItems().indexOf(dockHost);
        boolean visible = dockHostIndex != -1;

        if (visible) {
            int dividerIndex = dividerPosition == DividerPosition.AFTER ? dockHostIndex : dockHostIndex - 1;
            return dividers.get(dividerIndex);
        }

        return null;
    }

    private double computeMinDividerPosition() {
        if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
            if (dividerPosition == DividerPosition.AFTER) {
                return MINIMIZED_SIZE / splitPane.getWidth();
            } else {
                return 1.0 - MINIMIZED_SIZE / splitPane.getWidth();
            }
        } else {
            if (dividerPosition == DividerPosition.AFTER) {
                return MINIMIZED_SIZE / splitPane.getHeight();
            } else {
                return 1.0 - MINIMIZED_SIZE / splitPane.getHeight();
            }
        }
    }
}
