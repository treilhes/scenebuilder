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
package com.oracle.javafx.scenebuilder.template.model;

import java.net.URL;
import java.util.UUID;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gluonhq.jfxapps.core.api.template.Template;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
public class TemplateEntity implements Template{
    @Id
    private UUID id;

    private String orderKey;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private TemplateGroupEntity group;
    private URL fxmlUrl;
    private URL iconUrl;
    private URL iconX2Url;
    private UUID defaultThemeId;

    @Version
    private int version;

    @Override
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    @Override
    public String getOrderKey() {
        return orderKey;
    }
    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public TemplateGroupEntity getGroup() {
        return group;
    }
    public void setGroup(TemplateGroupEntity group) {
        this.group = group;
    }
    @Override
    public URL getFxmlUrl() {
        return fxmlUrl;
    }
    public void setFxmlUrl(URL fxmlUrl) {
        this.fxmlUrl = fxmlUrl;
    }
    @Override
    public URL getIconUrl() {
        return iconUrl;
    }
    public void setIconUrl(URL iconUrl) {
        this.iconUrl = iconUrl;
    }
    @Override
    public URL getIconX2Url() {
        return iconX2Url;
    }
    public void setIconX2Url(URL iconX2Url) {
        this.iconX2Url = iconX2Url;
    }
    @Override
    public UUID getDefaultThemeId() {
        return defaultThemeId;
    }
    public void setDefaultThemeId(UUID defaultThemeId) {
        this.defaultThemeId = defaultThemeId;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }

}
