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
package com.gluonhq.jfxapps.core.api.javafx;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;

import javafx.application.Platform;

public interface JfxAppPlatform {

    /**
     * Same as {@link Platform#runOnFxThread(Runnable)}
     * @param runnable
     */
    void runOnFxThread(Runnable runnable);

    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    void runOnFxThreadWithScope(ApplicationInstance scopedDocument, Runnable runnable);
    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope uuid
     * @param runnable the code to run
     */
    void runOnFxThreadWithScope(UUID scopedDocument, Runnable runnable);

    <T> FutureTask<T> callOnFxThreadWithScope(ApplicationInstance scopedDocument, Callable<T> callable);
    <T> FutureTask<T> callOnFxThreadWithScope(UUID scopedDocument, Callable<T> callable);

    /**
     * Same as {@link Platform#runOnFxThread(Runnable)} but will also ensure execution
     * with the currently active {@link ApplicationInstance} scope
     * @param runnable
     */
    <T> FutureTask<T> callOnFxThreadWithActiveScope(Callable<T> callable);


    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    void runWithScope(ApplicationInstance scopedDocument, Runnable runnable);

    <T> T runWithScope(ApplicationInstance scopedDocument, Supplier<T> runnable);

    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope uuid
     * @param runnable       the code to run
     */
    void runWithScope(UUID scopedDocument, Runnable runnable);

    <T> T runWithScope(UUID scopedDocument, Supplier<T> runnable);

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    void runOnThreadWithScope(ApplicationInstance scopedDocument, Runnable runnable);

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    void runOnThreadWithScope(UUID scopedDocument, Runnable runnable);


    /**
     * Same as {@link Platform#runOnFxThread(Runnable)} but will also ensure execution
     * with the currently active {@link ApplicationInstance} scope
     * @param runnable
     */
    void runOnFxThreadWithActiveScope(Runnable runnable);

    void removeScope(ApplicationInstance object);

    void setCurrentScope(ApplicationInstance object);

    void removeScope(Application object);

    void setCurrentScope(Application object);
}
