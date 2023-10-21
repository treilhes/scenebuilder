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
package com.oracle.javafx.scenebuilder.api.subjects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.Subject;

public class SubjectItem<T> extends Observable<T> {
    private final Subject<T> subject;
    private final Observable<T> observable;
    private final OnSetHandler<T> onSet;
    private T lastValue = null;

    @FunctionalInterface
    public interface OnSetHandler<T> {
        void doOnSet(T oldT, T newT);
    }

    @FunctionalInterface
    public interface ObservableBuilder<T> {
        Observable<T> buildObservable(Subject<T> subject);
    }

    public SubjectItem(Subject<T> subject, OnSetHandler<T> onSet, ObservableBuilder<T> observableBuilder) {
        super();
        this.subject = subject;
        this.observable = observableBuilder == null ? subject : observableBuilder.buildObservable(subject);
        this.onSet = onSet;
    }

    public SubjectItem(SubjectItem<T> subjectItem, OnSetHandler<T> onSet, ObservableBuilder<T> observableBuilder) {
        this(subjectItem.subject, onSet, observableBuilder);
    }

    public SubjectItem(Subject<T> subject, ObservableBuilder<T> observableBuilder) {
        this(subject, null, observableBuilder);
    }

    public SubjectItem(SubjectItem<T> subjectItem, ObservableBuilder<T> observableBuilder) {
        this(subjectItem.subject, null, observableBuilder);
    }

    public SubjectItem(Subject<T> subject, OnSetHandler<T> onSet) {
        this(subject, onSet, null);
    }

    public SubjectItem(SubjectItem<T> subjectItem, OnSetHandler<T> onSet) {
        this(subjectItem.subject, onSet, null);
    }

    public SubjectItem(Subject<T> subject) {
        this(subject, null, null);
    }
    public SubjectItem(SubjectItem<T> subjectItem) {
        this(subjectItem.subject, null, null);
    }

    public T get() {
        return lastValue;
    }

    public SubjectItem<T> set(T newT) {
        if (onSet != null) {
            onSet.doOnSet(lastValue, newT);
        }
        lastValue = newT;
        subject.onNext(newT);
        return this;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observable.subscribe(observer);
    }
}
