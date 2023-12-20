/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.loader.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;

public class AbstractExtension<T extends AbstractExtension<Extension>> implements Cloneable {

    private UUID id;

    private ExtensionContentProvider contentProvider;

    private LoadState loadState = LoadState.Unloaded;

    private Set<T> extensions;

    public AbstractExtension(UUID id, ExtensionContentProvider contentProvider) {
        super();
        this.id = id;
        this.contentProvider = contentProvider;
        this.extensions = new HashSet<>();
    }

    protected AbstractExtension() {
        this(null, null);
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public ExtensionContentProvider getContentProvider() {
        return contentProvider;
    }

    protected void setContentProvider(ExtensionContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public void setLoadState(LoadState loadState) {
        this.loadState = loadState;
    }

    public Set<T> getExtensions() {
        return Collections.unmodifiableSet(extensions);
    }

    public void addExtension(T extension) {
        extensions.add(extension);
    }

    public void removeExtension(T extension) {
        extensions.remove(extension);
    }

    protected void setExtensions(Set<T> extensions) {
        this.extensions = extensions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensions, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractExtension other = (AbstractExtension) obj;
        return Objects.equals(extensions, other.extensions) && Objects.equals(id, other.id);
    }

    @Override
    protected AbstractExtension<T> clone() throws CloneNotSupportedException {
        return (AbstractExtension<T>) super.clone();
    }
}
