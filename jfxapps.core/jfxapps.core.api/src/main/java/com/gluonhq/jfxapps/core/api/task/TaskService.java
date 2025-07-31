/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.task;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

@ApplicationSingleton
public class TaskService {

    private final ExecutorService executor;
    private final Set<Task<?>> runningTasks = ConcurrentHashMap.newKeySet();
    private final Set<Service<?>> runningServices = ConcurrentHashMap.newKeySet();

    public TaskService(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public TaskService() {
        this(Runtime.getRuntime().availableProcessors());
    }

    // === TASK HANDLING ===

    public <T> Task<T> submit(Task<T> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        runningTasks.add(task);

        task.setOnSucceeded(e -> {
            runningTasks.remove(task);
            if (onSuccess != null) {
                onSuccess.accept(task.getValue());
            }
        });

        task.setOnFailed(e -> {
            runningTasks.remove(task);
            if (onFailure != null) {
                onFailure.accept(task.getException());
            }
        });

        task.setOnCancelled(e -> runningTasks.remove(task));

        executor.submit(task);
        return task;
    }

    public <T> Task<T> submit(Task<T> task) {
        return submit(task, null, null);
    }

    // === SERVICE HANDLING ===

    public <T> Service<T> register(Service<T> service, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        runningServices.add(service);

        service.setOnSucceeded(e -> {
            runningServices.remove(service);
            if (onSuccess != null) {
                onSuccess.accept(service.getValue());
            }
        });

        service.setOnFailed(e -> {
            runningServices.remove(service);
            if (onFailure != null) {
                onFailure.accept(service.getException());
            }
        });

        service.setOnCancelled(e -> runningServices.remove(service));

        service.start();
        return service;
    }

    public <T> Service<T> register(Service<T> service) {
        return register(service, null, null);
    }

    // === CONTROL ===

    public void cancelAll() {
        for (Task<?> task : runningTasks) {
            task.cancel(true);
        }
        for (Service<?> service : runningServices) {
            service.cancel();
        }
    }

    public void shutdown() {
        cancelAll();
        executor.shutdown();
    }

    public Set<Task<?>> getRunningTasks() {
        return Collections.unmodifiableSet(runningTasks);
    }

    public Set<Service<?>> getRunningServices() {
        return Collections.unmodifiableSet(runningServices);
    }

    public boolean isIdle() {
        return runningTasks.isEmpty() && runningServices.isEmpty();
    }
}
