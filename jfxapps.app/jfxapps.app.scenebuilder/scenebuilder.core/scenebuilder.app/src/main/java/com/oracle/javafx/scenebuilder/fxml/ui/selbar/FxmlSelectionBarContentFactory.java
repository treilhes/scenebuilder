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
package com.oracle.javafx.scenebuilder.fxml.ui.selbar;

import java.util.LinkedList;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.selbar.SelectionBarContentFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.mask.SbAccessory;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.api.mask.SbHierarchyMask;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

@ApplicationInstanceSingleton
public class FxmlSelectionBarContentFactory implements SelectionBarContentFactory {

    private final SbFXOMObjectMask.Factory maskFactory;

    public FxmlSelectionBarContentFactory(SbFXOMObjectMask.Factory maskFactory) {
        super();
        this.maskFactory = maskFactory;
    }

    @Override
    public LinkedList<FXOMObject> buildOrderedPath(FXOMObject omObject) {
        assert FXOMObject.class.isInstance(omObject);
        FXOMObject fxomObject = FXOMObject.class.cast(omObject);

        LinkedList<FXOMObject> list = new LinkedList<FXOMObject>();

        while(fxomObject != null) {
            list.addFirst(fxomObject);
            fxomObject = fxomObject.getParentObject();
        }

        return list;
    }

    @Override
    public BarItem buildItem(FXOMObject omObject) {
        assert FXOMObject.class.isInstance(omObject);
        FXOMObject fxomObject = FXOMObject.class.cast(omObject);

        final var mask = maskFactory.getMask(fxomObject);
        String text = makeEntryText(mask);
        Node graphic = new ImageView(mask.getClassNameIcon());
        return new BarItem(graphic, text);
    }

    private String makeEntryText(SbHierarchyMask<SbAccessory> mask) {
        final StringBuilder result = new StringBuilder();

        result.append(mask.getClassNameInfo());
        final String description = mask.getSingleLineDescription();
        if (description != null) {
            result.append(" : "); //NOCHECK
            result.append(description);
        }
        return result.toString();
    }
}
