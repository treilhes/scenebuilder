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
package com.gluonhq.jfxapps.core.api.fs.watcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class FolderTypeTest {

    @Mock
    Watcher watcher;

    @Mock
    FolderFeature mockFeature;

    @Mock
    FolderFeatureHandler mockFeatureHandler;

    /**
     * Test that the folder type is applicable only for paths that match the given predicate.
     */
    @Test
    void must_be_applicable_only_for_given_predicate() {

        var folderType = FolderType.builder()
                .withIsApplicable(p -> Folder.isNamed(p,  "test"))
                .build();

        assertTrue(folderType.isDefinitionApplicable(Path.of("/foo/test")));
        assertFalse(folderType.isDefinitionApplicable(Path.of("/foo/bar")));
    }

    @Test
    void must_create_a_folder() throws URISyntaxException {

        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .build();

        var root = Path.of(FolderTypeTest.class.getResource("").toURI());
        Folder folder = folderType.createFolder(watcher, null, root);
        assertNotNull(folder);
        assertEquals(root, folder.getPath());
    }

    class CustomFolder extends Folder {
        public CustomFolder(Watcher watcher, Folder parent, Path path, List<FolderFeatureHandler> handlers, FolderType type) {
            super(watcher, parent, path, handlers, type);
        }
    }

    /**
     * Test that a custom folder can be created using a custom folder supplier.
     */
    @Test
    void must_create_a_custom_folder() throws URISyntaxException {

        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withFolderSupplier(CustomFolder::new)
                .build();

        var root = Path.of(FolderTypeTest.class.getResource("").toURI());
        Folder folder = folderType.createFolder(watcher, null, root);
        assertNotNull(folder);
        assertEquals(root, folder.getPath());
        assertTrue(folder instanceof CustomFolder);
    }

    @Test
    void must_create_a_file() throws URISyntaxException {

        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withFileType(FileType.generic())
                .build();

        var root = Path.of(FolderTypeTest.class.getResource("").toURI());
        Folder parent = folderType.createFolder(watcher, null, root);

        var filePath = root.resolve("FolderTypeTest.somefile");
        File file = folderType.createFile(parent, filePath);
        assertNotNull(file);
        assertEquals(filePath, file.getPath());
    }

    @Test
    void musnt_create_a_file_with_unsupported_file_type() {
        var folderType = FolderType.builder()
                .build();

        Folder parent = folderType.createFolder(watcher, null, Path.of("."));
        File file = folderType.createFile(parent, Path.of("/foo/test/file.unknown"));
        assertNull(file);
    }

    @Test
    void must_return_a_folder_type_factory() {
        var folderType = FolderType.builder()
                .withIsApplicable(p -> Folder.isNamed(p,  "test"))
                .withThisFolderType()
                .build();

        FolderType.Factory factory = folderType.getFactory();
        assertTrue(factory.findFolderType(Path.of("/foo/test")).isPresent());
        assertFalse(factory.findFolderType(Path.of("/foo/bar")).isPresent());
    }

    @Test
    void must_contains_provided_inclusion_patterns() {
        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withInclusionPatterns("should_include_this")
                .build();

        List<Pattern> patterns = folderType.getInclusionPatterns();
        assertEquals(1, patterns.size());
        assertTrue(patterns.get(0).matcher("should_include_this").matches());
    }

    @Test
    void must_contains_provided_exclusion_patterns() {
        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withExclusionPatterns("should_exclude_this")
                .build();

        List<Pattern> patterns = folderType.getExclusionPatterns();
        assertEquals(1, patterns.size());
        assertTrue(patterns.get(0).matcher("should_exclude_this").matches());
    }

    @Test
    void created_folder_must_contains_feature() {

        Mockito.when(mockFeatureHandler.isApplicable(Mockito.any(Folder.class))).thenReturn(true);
        Mockito.when(mockFeatureHandler.createFeature(Mockito.any(Folder.class))).thenReturn(mockFeature);

        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withFeatureHandler(mockFeatureHandler)
                .build();

        var folder =folderType.createFolder(watcher, null, Path.of("."));
        folder.applyFeatures();
        assertNotNull(folder);
        assertTrue(folder.hasFeature(mockFeature.getClass()));
    }

    @Test
    void created_folder_musnt_contains_feature() {

        Mockito.when(mockFeatureHandler.isApplicable(Mockito.any(Folder.class))).thenReturn(false);
        //Mockito.when(mockFeatureHandler.createFeature(Mockito.any(Folder.class))).thenReturn(mockFeature);

        var folderType = FolderType.builder()
                .withIsApplicable(Folder::any)
                .withFeatureHandler(mockFeatureHandler)
                .build();

        var folder =folderType.createFolder(watcher, null, Path.of("."));
        folder.applyFeatures();
        assertNotNull(folder);
        assertFalse(folder.hasFeature(mockFeature.getClass()));
    }
}
