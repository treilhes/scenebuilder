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
package com.oracle.javafx.scenebuilder.kit.glossary;

import java.net.URL;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Glossary;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The Class AbstractGlossary.
 */
public abstract class AbstractGlossary implements Glossary {

    /** The revision. */
    private final SimpleIntegerProperty revision = new SimpleIntegerProperty();


    /**
     * {@inheritDoc}
     */
    @Override
    public abstract List<String> queryControllerClasses(URL fxmlLocation);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract List<String> queryFxIds(URL fxmlLocation, String controllerClass, Class<?> targetType);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract List<String> queryEventHandlers(URL fxmlLocation, String controllerClass);

    /**
     * Returns the property holding the revision number of this glossary. Glossary
     * class adds +1 to this number each time the glossary content changes.
     *
     * @return the property holding the revision number of this glossary.
     */
    public ReadOnlyIntegerProperty revisionProperty() {
        return revision;
    }

    /**
     * Returns the revision number of this glossary.
     *
     * @return the revision number of this glossary.
     */
    public int getRevision() {
        return revision.get();
    }

    /*
     * For subclasses
     */

    /**
     * Increment revision.
     */
    protected void incrementRevision() {
        revision.set(revision.get() + 1);
    }
}
