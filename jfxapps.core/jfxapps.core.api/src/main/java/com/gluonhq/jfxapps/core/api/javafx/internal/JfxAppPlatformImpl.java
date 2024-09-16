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
package com.gluonhq.jfxapps.core.api.javafx.internal;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.ScopedExecutor;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;

import javafx.application.Platform;

@ApplicationInstanceSingleton
public final class JfxAppPlatformImpl implements JfxAppPlatform {

    ScopedExecutor<ApplicationInstance> executor;

    ScopedExecutor<Application> application;

    public JfxAppPlatformImpl(JfxAppContext context) {
        super();
        this.executor = context.getApplicationInstanceExecutor();
    }

    /**
     * Same as {@link Platform#runOnFxThread(Runnable)}
     * @param runnable
     */
    @Override
    public void runOnFxThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope
     * @param runnable the code to run
     */
    @Override
    public void runOnFxThreadWithScope(ApplicationInstance scopedDocument, Runnable runnable) {
        runOnFxThreadWithScope(executor.getScopeId(scopedDocument), runnable);
    }
    /**
     * Execute the runnable later on the fx thread
     * @param scopedDocument the document scope uuid
     * @param runnable the code to run
     */
    @Override
    public void runOnFxThreadWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        Platform.runLater(() -> {
            executor.executeRunnable(runnable, scopedDocument);
        });
    }

    @Override
    public <T> FutureTask<T> callOnFxThreadWithScope(ApplicationInstance scopedDocument, Callable<T> callable) {
        return callOnFxThreadWithScope(executor.getScopeId(scopedDocument), callable);
    }
    @Override
    public <T> FutureTask<T> callOnFxThreadWithScope(UUID scopedDocument, Callable<T> callable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");//NOCHECK
        }
        final FutureTask<T> task = new FutureTask<>(callable);
        Platform.runLater(() -> executor.executeRunnable(task, scopedDocument));
        return task;
    }

    /**
     * Same as {@link Platform#runOnFxThread(Runnable)} but will also ensure execution
     * with the currently active {@link ApplicationInstance} scope
     * @param runnable
     */
    @Override
    public <T> FutureTask<T> callOnFxThreadWithActiveScope(Callable<T> callable) {
        return callOnFxThreadWithScope(executor.getActiveScopeId(), callable);
    }


    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    @Override
    public void runWithScope(ApplicationInstance scopedDocument, Runnable runnable) {
        runWithScope(executor.getScopeId(scopedDocument), runnable);
    }

    @Override
    public <T> T runWithScope(ApplicationInstance scopedDocument, Supplier<T> runnable) {
        return runWithScope(executor.getScopeId(scopedDocument), runnable);
    }

    /**
     * Execute the runnable on the same thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope uuid
     * @param runnable       the code to run
     */
    @Override
    public void runWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        executor.executeRunnable(runnable, scopedDocument);
    }

    @Override
    public <T> T runWithScope(UUID scopedDocument, Supplier<T> runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        return executor.executeSupplier(runnable, scopedDocument);
    }

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    @Override
    public void runOnThreadWithScope(ApplicationInstance scopedDocument, Runnable runnable) {
        runOnThreadWithScope(executor.getScopeId(scopedDocument), runnable);
    }

    /**
     * Execute the runnable on a dedicated thread ensuring an unchanging scope
     *
     * @param scopedDocument the document scope
     * @param runnable       the code to run
     */
    @Override
    public void runOnThreadWithScope(UUID scopedDocument, Runnable runnable) {
        if (scopedDocument == null) {
            throw new RuntimeException("Illegal document scope! The scope must be created before using it here");// NOCHECK
        }
        Thread t = new Thread(() -> {
            executor.executeRunnable(runnable, scopedDocument);
        });
        t.run();
    }


    /**
     * Same as {@link Platform#runOnFxThread(Runnable)} but will also ensure execution
     * with the currently active {@link ApplicationInstance} scope
     * @param runnable
     */
    @Override
    public void runOnFxThreadWithActiveScope(Runnable runnable) {
        runOnFxThreadWithScope(executor.getActiveScopeId(), runnable);
    }

    @Override
    public void removeScope(ApplicationInstance object) {
        executor.removeScope(object);
    }

    @Override
    public void setCurrentScope(ApplicationInstance object) {
        executor.setCurrentScope(object);
    }

    @Override
    public void removeScope(Application object) {
        application.removeScope(object);
    }

    @Override
    public void setCurrentScope(Application object) {
        application.setCurrentScope(object);
    }


}
