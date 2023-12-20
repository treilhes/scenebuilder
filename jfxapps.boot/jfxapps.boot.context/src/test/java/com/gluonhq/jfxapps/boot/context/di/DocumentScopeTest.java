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
package com.gluonhq.jfxapps.boot.context.di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.DocumentScope;
import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.context.impl.ContextManagerImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentScopeTest.
 */
// TODO maybe add some multithreaded tests
public class DocumentScopeTest {

    /** The context. */
    private SbContext context;

    /**
     * Setup context.
     */
    @BeforeEach
    void setupContext() {
        ContextManager mng = new ContextManagerImpl();
        Class<?>[] classes = { FakeDocument.class, DocumentScopedObject.class };
        this.context = mng.create(null, UUID.randomUUID(), classes, List.of(), null);
    }

    /**
     * Document scope must change.
     */
    @Test
    void documentScopeMustChange() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        assertEquals(f1, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);

        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f2, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);

        FakeDocument f3 = context.getBean(FakeDocument.class);
        assertEquals(f3, DocumentScope.getActiveScope(), "Scope must be the current document scope");
        DocumentScope.setCurrentScope(null);


    }

    /**
     * Document must be the same instance.
     */
    @Test
    void documentMustBeTheSameInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertEquals(f1, f2, "Document must be the same instance");
        assertEquals(f1.getDocumentScopedObject(), f2.getDocumentScopedObject(), "DocumentScopedObject must be the same instance");
    }

    /**
     * Document nested object must be the same instance.
     */
    @Test
    void documentNestedObjectMustBeTheSameInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        DocumentScopedObject scoped = context.getBean(DocumentScopedObject.class);
        assertEquals(f1.getDocumentScopedObject(), scoped, "DocumentScopedObject must be the same instance");
    }

    /**
     * Document must be another instance.
     */
    @Test
    void documentMustBeAnotherInstance() {
        FakeDocument f1 = context.getBean(FakeDocument.class);
        DocumentScope.setCurrentScope(null);// will create a new document scope
        FakeDocument f2 = context.getBean(FakeDocument.class);
        assertNotEquals(f1, f2, "Document must be another instance");
        assertNotEquals(f1.getDocumentScopedObject(), f2.getDocumentScopedObject(), "DocumentScopedObject must be another instance");
    }

}
