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
package com.oracle.javafx.scenebuilder.api.template;

import java.net.URL;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.util.Ordered;

public abstract class AbstractTemplate extends Ordered implements Template {

    private final TemplateGroup group;
    private final URL fxmlUrl;
    private final int width;
    private final int height;
    
    /** Icon illustrating this placeholder. */
    private final URL iconUrl;
    
    /** Icon illustrating this placeholder. double sized */
    private final URL iconX2Url;
    
    private final List<Class<? extends Theme>> themes;

    public AbstractTemplate(TemplateGroup group, String orderKey, String name, URL fxmlUrl, Size size, URL iconUrl, URL iconX2Url, List<Class<? extends Theme>> themes) {
        this(group, orderKey, name, fxmlUrl, size.getWidth(), size.getHeight(), iconUrl, iconX2Url, themes);
    }
    
    public AbstractTemplate(TemplateGroup group, String orderKey, String name, URL fxmlUrl, int width, int height, URL iconUrl, URL iconX2Url, List<Class<? extends Theme>> themes) {
        super(orderKey, name);
        this.group = group;
        this.fxmlUrl = fxmlUrl;
        this.width = width;
        this.height = height;
        this.themes = themes;
        this.iconUrl = iconUrl;
        this.iconX2Url = iconX2Url;
    }

    @Override
    public TemplateGroup getGroup() {
        return group;
    }

    @Override
    public URL getFxmlUrl() {
        return fxmlUrl;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public URL getIconUrl() {
        return iconUrl;
    }

    @Override
    public URL getIconX2Url() {
        return iconX2Url;
    }

    @Override
    public List<Class<? extends Theme>> getThemes() {
        return themes;
    }

}
