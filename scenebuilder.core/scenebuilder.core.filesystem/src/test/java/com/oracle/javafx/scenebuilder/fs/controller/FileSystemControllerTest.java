/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.fs.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oracle.javafx.scenebuilder.api.fs.DocumentLoader;
import com.oracle.javafx.scenebuilder.api.fs.DocumentLoaderProvider;
import com.oracle.javafx.scenebuilder.api.fs.DocumentSaver;
import com.oracle.javafx.scenebuilder.api.fs.DocumentSaverProvider;
import com.oracle.javafx.scenebuilder.api.fs.LoaderSelector;
import com.oracle.javafx.scenebuilder.api.fs.SaverSelector;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;

@ExtendWith(MockitoExtension.class)
class FileSystemControllerTest {

    private SceneBuilderManager sceneBuilderManager = new SceneBuilderManager.SceneBuilderManagerImpl();
    @Mock
    DocumentSaver saver1;
    @Mock
    DocumentSaver saver2;
    @Mock
    DocumentSaver saver3;
    DocumentSaverProvider savers1 = () -> List.of(saver1, saver2);
    DocumentSaverProvider savers2 = () -> List.of(saver3);

    @Mock
    DocumentLoader loader1;
    @Mock
    DocumentLoader loader2;
    @Mock
    DocumentLoader loader3;
    DocumentLoaderProvider loaders1 = () -> List.of(loader1, loader2);
    DocumentLoaderProvider loaders2 = () -> List.of(loader3);

    @Test
    void should_call_CanLoad_on_all_loaders_and_return_null(@TempDir Path tempDir) throws IOException {
        URL tempFile = tempDir.resolve("file.txt").toUri().toURL();

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(loaders1, loaders2), List.of(), null);
        OMDocument<?> doc = fsc.load(tempFile, null, false);

        Mockito.verify(loader1).canLoad(tempFile);
        Mockito.verify(loader2).canLoad(tempFile);
        Mockito.verify(loader3).canLoad(tempFile);

        assertNull(doc);
    }

    @Test
    void should_do_a_direct_call_to_load(@TempDir Path tempDir) throws IOException {
        URL tempFile = tempDir.resolve("file.txt").toUri().toURL();

        LoaderSelector doNotCallLoaderSelector = new LoaderSelector() {
            @Override
            public DocumentLoader select(List<DocumentLoader> loaders) {
                return null;
            }
        };
        doNotCallLoaderSelector = Mockito.spy(doNotCallLoaderSelector);
        Mockito.when(loader1.canLoad(tempFile)).thenReturn(true);

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(loaders1, loaders2), List.of(), null);
        fsc.load(tempFile, doNotCallLoaderSelector, false);

        Mockito.verify(doNotCallLoaderSelector, never()).select(Mockito.anyList());
        Mockito.verify(loader1).load(tempFile, Mockito.any(), false);
    }

    @Test
    void should_call_loader_selector_to_load_with_loader3(@TempDir Path tempDir) throws IOException {
        URL tempFile = tempDir.resolve("file.txt").toUri().toURL();

        LoaderSelector mustCallLoaderSelector = new LoaderSelector() {
            @Override
            public DocumentLoader select(List<DocumentLoader> loaders) {
                return loader3;
            }
        };

        mustCallLoaderSelector = Mockito.spy(mustCallLoaderSelector);

        Mockito.when(loader1.canLoad(tempFile)).thenReturn(true);
        Mockito.when(loader3.canLoad(tempFile)).thenReturn(true);

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(loaders1, loaders2), List.of(), null);
        fsc.load(tempFile, mustCallLoaderSelector, false);

        Mockito.verify(mustCallLoaderSelector).select(Mockito.anyList());
        Mockito.verify(loader3).load(tempFile, Mockito.any() ,false);
    }

    @Test
    void should_call_CanSave_on_all_savers_and_return_false(@TempDir Path tempDir) {
        OMDocument<?> doc = null;
        Path tempFile = tempDir.resolve("file.txt");

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(), List.of(savers1, savers2), null);
        boolean saved = fsc.save(doc,tempFile, null);

        Mockito.verify(saver1).canSave(doc);
        Mockito.verify(saver2).canSave(doc);
        Mockito.verify(saver3).canSave(doc);

        assertFalse(saved);
    }

    @Test
    void should_do_a_direct_call_to_save(@TempDir Path tempDir) {
        OMDocument<?> doc = null;
        Path tempFile = tempDir.resolve("file.txt");

        SaverSelector doNotCallSaverSelector = new SaverSelector() {
            @Override
            public DocumentSaver select(List<DocumentSaver> loaders) {
                return null;
            }
        };
        doNotCallSaverSelector = Mockito.spy(doNotCallSaverSelector);
        Mockito.when(saver1.canSave(doc)).thenReturn(true);

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(), List.of(savers1, savers2), null);
        fsc.save(doc, tempFile, doNotCallSaverSelector);

        Mockito.verify(doNotCallSaverSelector, never()).select(Mockito.anyList());
        Mockito.verify(saver1).save(doc, tempFile);
    }

    @Test
    void should_call_saver_selector_to_save_with_saver3(@TempDir Path tempDir) {
        OMDocument<?> doc = null;
        Path tempFile = tempDir.resolve("file.txt");

        SaverSelector mustCallSaverSelector = new SaverSelector() {
            @Override
            public DocumentSaver select(List<DocumentSaver> loaders) {
                return saver3;
            }
        };
        mustCallSaverSelector = Mockito.spy(mustCallSaverSelector);

        Mockito.when(saver1.canSave(doc)).thenReturn(true);
        Mockito.when(saver3.canSave(doc)).thenReturn(true);

        FileSystemController fsc = new FileSystemController(sceneBuilderManager, List.of(), List.of(savers1, savers2), null);
        fsc.save(doc, tempFile, mustCallSaverSelector);

        Mockito.verify(mustCallSaverSelector).select(Mockito.anyList());
        Mockito.verify(saver3).save(doc, tempFile);
    }
}
