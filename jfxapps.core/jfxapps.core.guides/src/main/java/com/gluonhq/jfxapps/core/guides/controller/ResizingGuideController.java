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

package com.gluonhq.jfxapps.core.guides.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.guide.ResizingGuide;
import com.gluonhq.jfxapps.core.guides.preference.AlignmentGuidesColorPreference;
import com.gluonhq.jfxapps.core.guides.preference.GuidesEnabledPreference;
import com.gluonhq.jfxapps.core.guides.segment.AbstractSegment;
import com.gluonhq.jfxapps.core.guides.segment.HorizontalSegment;
import com.gluonhq.jfxapps.core.guides.segment.SegmentIndex;
import com.gluonhq.jfxapps.core.guides.segment.VerticalSegment;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 *
 */
@ApplicationInstanceSingleton
public class ResizingGuideController implements ResizingGuide {

    private final double MATCH_DISTANCE = 6.0;
    private final double CHROME_SIDE_LENGTH = 4.0;
    private final double DELTA = CHROME_SIDE_LENGTH + 2;

    private final GuidesEnabledPreference guidesEnabledPreference;

    private SegmentIndex widthIndex;
    private SegmentIndex heightIndex;
    private final ResizingGuideRenderer renderer;
    private double suggestedWidth;
    private double suggestedHeight;
    private boolean enabled;

    public ResizingGuideController(
            AlignmentGuidesColorPreference alignmentGuidesColorPreference,
            GuidesEnabledPreference guidesEnabledPreference) {
        this.guidesEnabledPreference = guidesEnabledPreference;
        this.renderer = new ResizingGuideRenderer(alignmentGuidesColorPreference, CHROME_SIDE_LENGTH);

        guidesEnabledPreference.getObservableValue().subscribe(b -> {
            if (!b && this.renderer != null) {
                clear();
            }
        });
    }

    @Override
    public void initialize(boolean matchWidth, boolean matchHeight) {
        if (this.renderer != null) {
            clear();
        }

        if (matchWidth) {
            widthIndex = new SegmentIndex();
        } else {
            widthIndex = null;
        }
        if (matchHeight) {
            heightIndex = new SegmentIndex();
        } else {
            heightIndex = null;
        }
    }

    @Override
    public void addSampleBounds(Node node) {
        assert node != null;
        assert node.getScene() != null;

        final Bounds layoutBounds = node.getLayoutBounds();
        final Bounds boundsInScene = node.localToScene(layoutBounds, true /* rootScene */);
        final double minX = boundsInScene.getMinX();
        final double minY = boundsInScene.getMinY();
        final double maxX = boundsInScene.getMaxX();
        final double maxY = boundsInScene.getMaxY();

        if ((widthIndex != null) && (minX < maxX)) {
            widthIndex.addSegment(new HorizontalSegment(minX, maxX, minY - DELTA));
        }
        if ((heightIndex != null) && (minY < maxY)) {
            heightIndex.addSegment(new VerticalSegment(minX - DELTA, minY, maxY));
        }
    }

    @Override
    public void clear() {
        renderer.setSegments(Collections.emptyList());
    }

    @Override
    public void match(Bounds targetBounds) {

        if (!isEnabled()) {
            return;
        }

        final List<AbstractSegment> matchingSegments = new ArrayList<>();

        final double targetWidth = targetBounds.getWidth();
        if (widthIndex == null) {
            suggestedWidth = targetWidth;
        } else {
            final List<AbstractSegment> matchingWidthSegments
                    = widthIndex.match(targetWidth, MATCH_DISTANCE);
            if (matchingWidthSegments.isEmpty()) {
                suggestedWidth = targetWidth;
            } else {
                suggestedWidth = matchingWidthSegments.get(0).getLength();
                matchingSegments.addAll(matchingWidthSegments);
            }
        }

        final double targetHeight = targetBounds.getHeight();
        if (heightIndex == null) {
            suggestedHeight = targetHeight;
        } else {
            final List<AbstractSegment> matchingHeightSegments
                    = heightIndex.match(targetHeight, MATCH_DISTANCE);
            if (matchingHeightSegments.isEmpty()) {
                suggestedHeight = targetHeight;
            } else {
                suggestedHeight = matchingHeightSegments.get(0).getLength();
                matchingSegments.addAll(matchingHeightSegments);
            }
        }

        renderer.setSegments(matchingSegments);
    }


    @Override
    public double getSuggestedWidth() {
        return suggestedWidth;
    }

    @Override
    public double getSuggestedHeight() {
        return suggestedHeight;
    }

    @Override
    public Group getGuideGroup() {
        return renderer.getGuideGroup();
    }

    @Override
    public boolean isEnabled() {
        return guidesEnabledPreference.getValue() && enabled;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void enable() {
        enabled = true;
    }
}
