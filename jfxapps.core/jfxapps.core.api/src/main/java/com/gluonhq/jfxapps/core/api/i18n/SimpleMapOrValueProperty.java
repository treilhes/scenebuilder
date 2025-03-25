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
package com.gluonhq.jfxapps.core.api.i18n;

import java.util.Map;
import java.util.WeakHashMap;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

/**
 * This class is a bit tricky and has been created in order to be able to bind
 * i18n strings to the UI. It is used in the {@link I18N} class.<br>
 * <br>
 * the issue it solves is the following:<br>
 * <br>
 * node1.node2=Hello World<br>
 * node1.node2.node3=Hello World 2<br>
 * <br>
 * Both keys share the same prefix. When the values are stored in a map of map
 * you end up with the level "node2" being a value and a map at the same
 * time.<br>
 * <br>
 * This class can hold both a value {@link #setSimpleValue(Object)} and is also
 * a map at the same time. It is used in the {@link I18N} class to store the
 * i18n keys and values.<br>
 * <br>
 * When using a binding expression like <b>${controller.i18n.node1.node2}</b> in
 * a FXML file, when updating the {@link I18N} locale using
 * {@link I18N#changeLocale(java.util.Locale)} it is expected that the value is
 * updated with the new localized value. So events must be propagated to allow
 * updating the binding accordingly. In order to do so, the {@link #notifier}
 * property is used. It is a simple integer property that is incremented each
 * time a change is made to the {@link #simpleValue} property propagating this
 * event to the parent map listeners.<br>
 * <br>
 * Another point to take into account is the fact that this class will never return null if the key is absent
 * When calling {@link #get(Object)} it will always return a {@link SimpleMapOrValueProperty} instance. This is
 * because the class ensure that even missing keys can be binded to. This is done by creating a new instance of
 * {@link SimpleMapOrValueProperty} when the key is not found. This is done in the {@link #get(Object)} method.<br>
 * <br>
 * This class has an issue. When controllers are created and destroyed, the
 * bindings are not removed. This is probably because the {@link #notifier} property is
 * not removed from the listeners. This is a memory leak. This is not a big issue
 * as the number of controllers is limited, the memory leak is not that big and occurs when switching locale in
 * I18N. It is still an issue that should be fixed.<br>
 */
//FIXME This class has a memory leak. The notifier property is not removed from the listeners when the controller is destroyed.
public class SimpleMapOrValueProperty extends SimpleMapProperty<String, SimpleMapOrValueProperty> {

    private IntegerProperty notifier = new SimpleIntegerProperty(Integer.MIN_VALUE);
    private ObjectProperty<Object> simpleValue = new SimpleObjectProperty<>();

    private final String key;
    private final String keyPath;
    private SimpleMapOrValueProperty parent;

    private final Map<InvalidationListener, InvalidationListener> notifierInvalidationListener = new WeakHashMap<>();
    private final Map<ChangeListener<? super ObservableMap<String, SimpleMapOrValueProperty>>, ChangeListener<Number>> notifierChangeListener = new WeakHashMap<>();
    private final Map<MapChangeListener<? super String, ? super SimpleMapOrValueProperty>, ChangeListener<Number>> notifierMapChangeListener = new WeakHashMap<>();

    public SimpleMapOrValueProperty(SimpleMapOrValueProperty parent, String key) {
        super(FXCollections.observableHashMap());
        this.parent = parent;
        this.key = key;
        this.keyPath = parent == null || parent.keyPath == null || parent.keyPath.isBlank() ? key : parent.keyPath + "." + key;

        simpleValue.addListener((ob, o, n) -> {
            if (parent != null) {
                parent.notifyChildChanged(this);
            }
        });
    }

    @Override
    public SimpleMapOrValueProperty get(Object key) {
        if (key == null) {
            return super.get(key);
        }
        var keyString = key.toString();

        var value = super.get(keyString);
        if (value != null) {
            return value;
        }

        var simpleMapOrValueProperty = new SimpleMapOrValueProperty(this, keyString);
        put(keyString, simpleMapOrValueProperty);

        return simpleMapOrValueProperty;

    }

    public Object getSimpleValue() {
        return simpleValue.get();
    }

    public ObjectProperty<Object> simpleValueProperty() {
        return simpleValue;
    }

    public void setSimpleValue(Object value) {
        this.simpleValue.set(value);
    }

    @Override
    public StringBinding asString() {
        return this.simpleValue.asString();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        super.removeListener(listener);
        super.addListener(listener);

        var notifierListener = notifierInvalidationListener.get(listener);

        if (notifierListener != null) {
            return;
        }

        notifierListener = (o) -> {
            listener.invalidated(this);
        };

        notifierInvalidationListener.put(listener, notifierListener);

        this.notifier.addListener(notifierListener); // Forward the event to the listener.
    }

    @Override
    public void addListener(ChangeListener<? super ObservableMap<String, SimpleMapOrValueProperty>> listener) {
        //        if (key.equals("") || key.equals("manager") || key.equals("source")
        //                || key.equals("edit") || key.equals("groupid")) {
        //            System.out.println("add change listener " + key);
        //        }
        super.removeListener(listener);
        super.addListener(listener);

        var notifierListener = notifierChangeListener.get(listener);

        if (notifierListener != null) {
            return;
        }

        notifierListener = (o, ov, nv) -> {
            listener.changed(this, null, null);
        };

        notifierChangeListener.put(listener, notifierListener);

        this.notifier.addListener(notifierListener); // Forward the event to the listener.
    }

    @Override
    public void addListener(MapChangeListener<? super String, ? super SimpleMapOrValueProperty> listener) {
        //        if (key.equals("") || key.equals("manager") || key.equals("source")
        //                || key.equals("edit") || key.equals("groupid")) {
        //            System.out.println("add change listener " + key);
        //        }
        super.removeListener(listener);
        super.addListener(listener);

        var notifierListener = notifierMapChangeListener.get(listener);

        if (notifierListener != null) {
            return;
        }

        notifierListener = (o, ov, nv) -> {
            listener.onChanged(new Change<>(this) {

                @Override
                public boolean wasAdded() {
                    return false;
                }

                @Override
                public boolean wasRemoved() {
                    return false;
                }

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public SimpleMapOrValueProperty getValueAdded() {
                    return null;
                }

                @Override
                public SimpleMapOrValueProperty getValueRemoved() {
                    return null;
                }
            });
        };

        notifierMapChangeListener.put(listener, notifierListener);

        this.notifier.addListener(notifierListener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        super.removeListener(listener);
        var notifierListener = notifierInvalidationListener.get(listener);
        if (notifierListener != null) {
            this.notifier.removeListener(notifierListener);
        }
    }

    @Override
    public void removeListener(ChangeListener<? super ObservableMap<String, SimpleMapOrValueProperty>> listener) {
        super.removeListener(listener);
        var notifierListener = notifierChangeListener.get(listener);
        if (notifierListener != null) {
            this.notifier.removeListener(notifierListener);
        }
    }

    @Override
    public void removeListener(MapChangeListener<? super String, ? super SimpleMapOrValueProperty> listener) {
        super.removeListener(listener);
        var notifierListener = notifierMapChangeListener.get(listener);
        if (notifierListener != null) {
            this.notifier.removeListener(notifierListener);
        }
    }

    void notifyChildChanged(SimpleMapOrValueProperty simpleMapOrValueProperty) {
        notifier.set(notifier.get() + 1);
        if (parent != null) {
            parent.notifyChildChanged(simpleMapOrValueProperty);
        }
    }

    @Override
    public String toString() {
        return simpleValue.get() == null ? "${" + keyPath + "}" : simpleValue.get().toString();
    }

}