/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface HierarchyMask {
    @Deprecated
    public enum Accessory {
        // True accessories

        PLACEHOLDER,
        TOOLTIP,
        CONTEXT_MENU,
        CLIP,
        GRAPHIC,
        // Single-valued sub-components treated as accessories
        // TODO(elp) : verify that it is complete
        CONTENT,
        ROOT,
        SCENE,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        CENTER,
        XAXIS,
        YAXIS,
        TREE_COLUMN,
        EXPANDABLE_CONTENT,
        HEADER,
        DP_CONTENT {
                    @Override
                    public String toString() {
                        return "CONTENT"; // NOI18N
                    }
                },
        DP_GRAPHIC {
                    @Override
                    public String toString() {
                        return "GRAPHIC"; // NOI18N
                    }
                },
        // ExpansionPanel
        EXPANDED_CONTENT,
        COLLAPSED_CONTENT,
        // ExpandedPanel
        EX_CONTENT {
            @Override
            public String toString() { return "CONTENT"; }
        }
    }

    public boolean isAcceptingAccessory(Accessory graphic);

    public FXOMObject getAccessory(Accessory graphic);

    public boolean isAcceptingSubComponent();

    public int getSubComponentCount();

    public FXOMObject getSubComponentAtIndex(int i);
}
