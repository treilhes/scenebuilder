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
package com.oracle.javafx.scenebuilder.controllibrary.library.builtin;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;

/**
 *
 *
 */
public class LibraryItemImpl implements LibraryItem {


    private final String fxmlText;
    private final String name;
    private final Qualifier qualifier;

    public LibraryItemImpl(String name, Qualifier qualifier, String fxmlText) {
        assert name != null;
        assert qualifier != null;
        assert fxmlText != null;

        this.name = name;
        this.qualifier = qualifier;
        this.fxmlText = fxmlText;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSection() {
        return qualifier.getCategory();
    }

    @Override
    public String getFxmlText() {
        return fxmlText;
    }

    @Override
    public URL getIconURL() {
        return qualifier.getIconUrl();
    }
    

    public Qualifier getQualifier() {
        return qualifier;
    }

    @Override
    public FXOMDocument instantiate(ClassLoader classloader) {
        FXOMDocument result;

        try {
            result = new FXOMDocument(fxmlText, null, classloader, null);
        } catch(Error|IOException x) {
            x.printStackTrace();
            result = null;
        }

        return result;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.qualifier);
        hash = 67 * hash + Objects.hashCode(this.fxmlText);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LibraryItemImpl other = (LibraryItemImpl) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.qualifier, other.qualifier)) {
            return false;
        }
        return Objects.equals(this.fxmlText, other.fxmlText);
    }

    /*
     * Object
     */

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getClass().getSimpleName());
        result.append('[');
        result.append(getName());
        result.append(']');

        return result.toString();
    }
}
