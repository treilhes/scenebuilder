/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.dnd;

import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.Node;
import javafx.scene.input.TransferMode;

public interface DragSource {

    Set<? extends FXOMObject> getDraggedObjects();

    String makeDropJobDescription();

    FXOMObject getHitObject();

    double getHitX();

    double getHitY();

    /**
     * Checks if the drag source contains at least one element.
     *
     * @return true, if the drag source contains at least one element.
     */
    boolean isEmpty();

    /**
     * Checks if the drag source contains a single element.
     *
     * @return true, if the drag source contains a single element.
     */
    boolean isSingle();

    /**
     * Checks if the drag source contains objects of the same type.
     *
     * @return true, if the drag source contains objects of the same type.
     */
    boolean isSingleType();

    /**
     * Checks if the drag source contains objects of the same provided type.
     *
     * @param type the type to check
     * @return true, if the drag source contains objects of the same provided type.
     */
    boolean isSingleType(Class<?> type);

//    // FIXME specialization for fxml move to fxml api
//    @Deprecated
//    boolean isSingleImageViewOnly();
//
//    // FIXME specialization for fxml move to fxml api
//    @Deprecated
//    boolean isSingleTooltipOnly();
//
//    // FIXME specialization for fxml move to fxml api
//    @Deprecated
//    boolean isSingleContextMenuOnly();
//
//    // FIXME specialization for fxml move to fxml api
//    @Deprecated
//    boolean isNodeOnly();

    Node makeShadow();

    boolean isAcceptable();

    TransferMode getTransferMode();
}
