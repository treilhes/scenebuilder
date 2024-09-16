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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

@ApplicationInstanceSingleton
public final class JfxAppScheduler extends Scheduler {

    JfxAppScheduler() {
    }

    @Override
    public Worker createWorker() {
        return new JavaFxWorker();
    }

    private static class JavaFxWorker extends Worker implements Runnable {
        private volatile QueuedRunnable head = new QueuedRunnable(null);
        private final AtomicReference<QueuedRunnable> tail = new AtomicReference<>(head);

        private static class QueuedRunnable extends AtomicReference<QueuedRunnable> implements Disposable, Runnable {
            private volatile Runnable action;

            private QueuedRunnable(Runnable action) {
                this.action = action;
            }

            @Override
            public void dispose() {
                action = null;
            }

            @Override
            public boolean isDisposed() {
                return action == null;
            }

            @Override
            public void run() {
                Runnable action = this.action;
                if (action != null) {
                    action.run();
                }
                this.action = null;
            }
        }

        @Override
        public void dispose() {
            tail.set(null);
            QueuedRunnable qr = this.head;
            while (qr != null) {
                qr.dispose();
                qr = qr.getAndSet(null);
            }
        }

        @Override
        public boolean isDisposed() {
            return tail.get() == null;
        }

        @Override
        public Disposable schedule(final Runnable action, long delayTime, TimeUnit unit) {
            long delay = Math.max(0, unit.toMillis(delayTime));

            final QueuedRunnable queuedRunnable = new QueuedRunnable(action);
            if (delay == 0) { // delay is too small for the java fx timer, schedule it without delay
                return schedule(queuedRunnable);
            }

            final Timeline timer = new Timeline(
                    new KeyFrame(Duration.millis(delay), event -> schedule(queuedRunnable)));
            timer.play();

            return Disposable.fromRunnable(() -> {
                queuedRunnable.dispose();
                timer.stop();
            });
        }

        @Override
        public Disposable schedule(final Runnable action) {
            if (isDisposed()) {
                return Disposable.disposed();
            }

            final QueuedRunnable queuedRunnable = action instanceof QueuedRunnable ? (QueuedRunnable) action
                    : new QueuedRunnable(action);

            QueuedRunnable tailPivot;
            do {
                tailPivot = tail.get();
            } while (tailPivot != null && !tailPivot.compareAndSet(null, queuedRunnable));

            if (tailPivot == null) {
                queuedRunnable.dispose();
            } else {
                tail.compareAndSet(tailPivot, queuedRunnable);
                if (tailPivot == head) {
                    if (Platform.isFxApplicationThread()) {
                        run();
                    } else {
                        Platform.runLater(this);
                    }
                }
            }
            return queuedRunnable;
        }

        @Override
        public void run() {
            for (QueuedRunnable qr = head.get(); qr != null; qr = qr.get()) {
                qr.run();
                head = qr;
            }
        }
    }
}
