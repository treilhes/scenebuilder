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
package com.oracle.javafx.scenebuilder.api.di;

import java.util.UUID;
import java.util.function.Supplier;

import com.oracle.javafx.scenebuilder.core.context.Document;
import com.oracle.javafx.scenebuilder.core.context.DocumentScope;

import javafx.application.Platform;

public final class SbPlatform {

    /**
     * Same as {@link Platform#runOnFxThread(Runnable)}
     * @param runnable
     */
    public static void runOnFxThread(Runnable runnable) {
        Platform.runLater(runnable);
    }



    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    public static void runOnFxThreadWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = DocumentScope.getScopeId(scopedDocument);
        runOnFxThreadWithScope(documentUuid, runnable);
    }
    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope uuid
     * @param runnable the code to run
     */
    public static void runOnFxThreadWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        Platform.runLater(() -> {
            DocumentScope.executeRunnable(runnable, scopedDocument);
        });
    }


    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    public static void runWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = DocumentScope.getScopeId(scopedDocument);
        runWithScope(documentUuid, runnable);
    }

    public static <T> T runWithScope(Document scopedDocument, Supplier<T> runnable) {
        UUID documentUuid = DocumentScope.getScopeId(scopedDocument);
        return runWithScope(documentUuid, runnable);
    }

    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope uuid
     * @param runnable       the code to run
     */
    public static void runWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        DocumentScope.executeRunnable(runnable, scopedDocument);
    }

    public static <T> T runWithScope(UUID scopedDocument, Supplier<T> runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        return DocumentScope.executeSupplier(runnable, scopedDocument);
    }

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    public static void runOnThreadWithScope(Document scopedDocument, Runnable runnable) {
        UUID documentUuid = DocumentScope.getScopeId(scopedDocument);
        runOnThreadWithScope(documentUuid, runnable);

    }

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    public static void runOnThreadWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        Thread t = new Thread(() -> {
            DocumentScope.executeRunnable(runnable, scopedDocument);
        });
        t.run();
    }


    /**
     * Same as {@link Platform#runOnFxThread(Runnable)} but will also ensure execution
     * with the currently active {@link Document} scope
     * @param runnable
     */
    public static void runOnFxThreadWithActiveScope(Runnable runnable) {
        runOnFxThreadWithScope(DocumentScope.getActiveScopeUUID(), runnable);
    }
}
