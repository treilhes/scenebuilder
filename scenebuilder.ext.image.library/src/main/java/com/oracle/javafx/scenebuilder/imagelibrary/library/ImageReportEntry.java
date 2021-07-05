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
package com.oracle.javafx.scenebuilder.imagelibrary.library;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.library.ReportEntry;

/**
 *
 * 
 */
public class ImageReportEntry implements ReportEntry, Cloneable {
    
    public enum Type {
        NONE,
        IMAGE,
        FONT_ICONS
    }
    private final String name;
    private final Status status;
    private final Throwable exception;
    private final String resourceName;
    private final Type type;

    private String fontName;
    private final List<Integer> unicodePoints = new ArrayList<>();
    private BoundingBox boundingBox;
    
    public ImageReportEntry(String name, Status status, Throwable exception, Type type, String resourceName) {
        assert name != null;
        assert (exception == null) || (status != Status.OK);
        
        this.name = name;
        this.status = status;
        this.exception = exception;
        this.resourceName = resourceName;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    public String getResourceName() {
        return resourceName;
    }

    /*
     * Object
     */
    
    public Type getType() {
        return type;
    }
    
    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public List<Integer> getUnicodePoints() {
        return unicodePoints;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        switch(status) {
            case OK:
                //assert klass != null;
                //sb.append(klass.getCanonicalName());
                sb.append(" - OK"); //NOCHECK
                break;
            case KO:
                assert exception != null;
                sb.append(name);
                sb.append(" - KO - "); //NOCHECK
                sb.append(exception.getMessage());
                break;
            case IGNORED:
                sb.append(name);
                sb.append(" - IGNORED"); //NOCHECK
                break;
            default:
                throw new IllegalStateException("Unexpected status " + status); //NOCHECK
        }
        
        return sb.toString();
    }

    @Override
    protected ImageReportEntry clone() {
        ImageReportEntry cloned = new ImageReportEntry(name, status, exception, type, resourceName);
        cloned.setFontName(this.getFontName());
        cloned.setBoundingBox(this.getBoundingBox());
        return cloned;
    }
    
    
}
