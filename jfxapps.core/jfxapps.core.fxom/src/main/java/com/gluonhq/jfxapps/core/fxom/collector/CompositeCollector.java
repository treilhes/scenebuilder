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
package com.gluonhq.jfxapps.core.fxom.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;

public class CompositeCollector implements FXOMCollector<List<FXOMCollector<?>>> {

    public static CompositeCollector of(FXOMCollector<?>... collectors ) {
        return new CompositeCollector(List.of(collectors));
    }

    public static CompositeCollector of(Collection<FXOMCollector<?>> collectors ) {
        return new CompositeCollector(new ArrayList<>(collectors));
    }

    private final List<FXOMCollector<?>> collectors;
    private final Strategy strategy;

    public CompositeCollector(List<FXOMCollector<?>> collectors) {
        super();
        this.collectors = collectors;
        this.strategy = aggregateStrategy();
    }

    @Override
    public Strategy collectionStrategy() {
        return strategy;
    }

    @Override
    public void collect(FXOMObject object) {
        collectors.forEach(c -> c.accept(object));
    }

    @Override
    public void collect(FXOMProperty property) {
        collectors.forEach(c -> c.accept(property));
    }

    @Override
    public List<FXOMCollector<?>> getCollected() {
        return collectors;
    }

    private Strategy aggregateStrategy() {

        boolean object = false;
        boolean property = false;
        for (FXOMCollector<?> c : collectors) {
            switch (c.collectionStrategy()) {
                case OBJECT_AND_PROPERTY:
                    return Strategy.OBJECT_AND_PROPERTY;
                case OBJECT: {
                    object = true;
                    break;
                }
                case PROPERTY: {
                    property = true;
                    break;
                }
            }
        }

        if (object && property) {
            return Strategy.OBJECT_AND_PROPERTY;
        } else if (object) {
            return Strategy.OBJECT;
        } else {
            return Strategy.PROPERTY;
        }
    }

    public boolean isEmpty() {
        return collectors.isEmpty();
    }
}
