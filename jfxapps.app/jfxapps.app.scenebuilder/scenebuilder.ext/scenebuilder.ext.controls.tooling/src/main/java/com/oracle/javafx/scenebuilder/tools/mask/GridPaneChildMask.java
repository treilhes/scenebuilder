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
package com.oracle.javafx.scenebuilder.tools.mask;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

import javafx.scene.layout.GridPane;

/**
 *
 */
@Prototype
public class GridPaneChildMask {

    private final SbMetadata metadata;

    private FXOMElement fxomElement;


    public GridPaneChildMask(SbMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    protected void setupMask(FXOMObject fxomObject) {
        assert fxomObject instanceof FXOMElement;
        assert fxomObject.getParentObject() != null;
        assert fxomObject.getParentObject().getSceneGraphObject().isInstanceOf(GridPane.class);

        fxomElement = (FXOMElement) fxomObject;
    }

    /**
     * Returns the column index for this GridPane child mask.
     *
     * @return the column index
     */
    public int getColumnIndex() {
        return getIndex(GridPaneProperties.COLUMN_INDEX);
    }

    /**
     * Returns the row index for this GridPane child mask.
     *
     * @return the row index
     */
    public int getRowIndex() {
        return getIndex(GridPaneProperties.ROW_INDEX);
    }

    private int getIndex(PropertyName propertyName) {
        int result;
        final var fxomElement = getFxomElement();
        final var vpm = metadata.queryValueProperty(fxomElement, propertyName);
        final var value = vpm.getValueObject(fxomElement);
        // TODO : when DTL-5920 will be fixed, the null check will become unecessary
        if (value == null) {
            result = 0;
        } else {
            assert value instanceof Integer;
            result = ((Integer) value);
        }
        return result;
    }

    protected FXOMElement getFxomElement() {
        return fxomElement;
    }

    @Singleton
    public static final class Factory extends AbstractFactory<GridPaneChildMask> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public GridPaneChildMask getMask(FXOMObject fxomObject) {
            return create(GridPaneChildMask.class, m -> m.setupMask(fxomObject));
        }
    }
}
