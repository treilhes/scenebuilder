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
package com.gluonhq.jfxapps.app.manager.store.model;

import java.net.URL;

import com.gluonhq.jfxapps.boot.api.registry.model.ApplicationInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Application {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty version = new SimpleStringProperty();
    private ObjectProperty<URL> image = new SimpleObjectProperty<>();

    private ObjectProperty<ApplicationInfo> info = new SimpleObjectProperty<>();

    public Application(ApplicationInfo info) {
        this.infoProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                update(nv);
            }
        });
        this.info.set(info);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty versionProperty() {
        return version;
    }

    public ObjectProperty<URL> imageProperty() {
        return image;
    }

    public ObjectProperty<ApplicationInfo> infoProperty() {
        return info;
    }

    public boolean match(String searchTerm) {

        if (searchTerm == null || searchTerm.isEmpty() || info.get() == null) {
            return true;
        }
        var data = info.get();

        var lowerCaseSearch=searchTerm.toLowerCase();
        var title = data.getTitle() == null ? "" : data.getTitle();
        //return getI18n().getStringOrDefault(title, title).toLowerCase().contains(lowerCaseSearch);
        return true;

    }

    private void update(ApplicationInfo source) {
        this.name.set(source.getTitle());
        this.description.set(source.getText());
        this.version.set(source.getVersion());
        this.image.set(source.getImage());

    }
}
