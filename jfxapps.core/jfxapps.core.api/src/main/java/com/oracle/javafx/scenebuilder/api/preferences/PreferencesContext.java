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
package com.oracle.javafx.scenebuilder.api.preferences;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

@Singleton
public class PreferencesContext {

    private static final String HASH_PATH_SEPARATOR = "-"; // NOCHECK
    public static final String DEFAULT_DOCUMENT_NODE = "<<<empty>>>"; // NOCHECK

    private final SbContext context;

    private final RootPreferencesNode rootNode;

    private DocumentPreferencesNode documentsNode;

    // @formatter:off
    public PreferencesContext(
            SbContext context,
            RootPreferencesNode rootNode,
            DocumentPreferencesNode documentsNode) {
     // @formatter:on
        this.context = context;
        this.rootNode = rootNode;
        this.documentsNode = documentsNode;
    }

    public boolean isDocumentScope(Class<?> cls) {
        return context.isDocumentScope(cls);
    }

    public boolean isDocumentAlreadyInPathScope() {
        return this.documentsNode.getNode().absolutePath().contains(computeDocumentNodeName());
    }

    public String getCurrentFilePath() {
        final DocumentManager documentManager = activeDocumentManager();
        final FXOMDocument document =  documentManager.fxomDocument().get();
        final URL fxmlLocation = document == null ? null : document.getLocation();

        try {
            if (fxmlLocation != null) {
                final File fxmlFile = new File(fxmlLocation.toURI());
                return fxmlFile.getPath();
            }
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDocumentNameDefined() {
        return getCurrentFilePath() != null;
    }

    public String computeDocumentNodeName() {
        String filePath = getCurrentFilePath();
        if (filePath != null) {
            return generateKey(filePath);
        }
        return DEFAULT_DOCUMENT_NODE;
    }

    private void handleMovedNode(String newDocumentKey)
            throws IOException, BackingStoreException, InvalidPreferencesFormatException {
        // find the document node
        Preferences previousDocumentNode = this.documentsNode.getNode();

        String oldPath = previousDocumentNode.absolutePath();
        String newPath = oldPath.replace(DEFAULT_DOCUMENT_NODE, newDocumentKey);

        if (oldPath.equals(newPath)) {
            return;
        }

        final Preferences newDocumentNode = this.documentsNode.getNode().node(newPath);
        final DocumentPreferencesNode oldNode = this.documentsNode;
        this.documentsNode = new DocumentPreferencesNode() {

            @Override
            public Preferences getNode() {
                return newDocumentNode;
            }

            @Override
            public void clearAllDocumentNodes() {
                oldNode.clearAllDocumentNodes();
            }

            @Override
            public void cleanupCorruptedNodes() {
                oldNode.cleanupCorruptedNodes();
            }
        };
    }

    public DocumentManager activeDocumentManager() {
        return context.getBean(DocumentManager.class);
    }

    public RootPreferencesNode getRootNode() {
        return rootNode;
    }

    public DocumentPreferencesNode getDocumentsNode() {
        try {
            handleMovedNode(computeDocumentNodeName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentsNode;
    }

    public PreferencesContext nodeContext(Object instance, String name) {

        return new PreferencesContext(context, new RootPreferencesNode() {

            @Override
            public Preferences getNode() {
                if (PreferencesContext.this.isDocumentScope(instance.getClass())) {
                    return rootNode.getNode();
                } else {
                    return rootNode.getNode().node(name);
                }
            }
        }, new DocumentPreferencesNode() {

            @Override
            public Preferences getNode() {
                if (PreferencesContext.this.isDocumentScope(instance.getClass())) {
                    return documentsNode.getNode().node(name);
                } else {
                    return documentsNode.getNode();
                }
            }

            @Override
            public void cleanupCorruptedNodes() {
                PreferencesContext.this.getDocumentsNode().cleanupCorruptedNodes();
            }

            @Override
            public void clearAllDocumentNodes() {
                PreferencesContext.this.getDocumentsNode().clearAllDocumentNodes();
            }

        }) {

            @Override
            public boolean isDocumentScope(Class<?> cls) {
                return PreferencesContext.this.isDocumentScope(cls);
            }

            @Override
            public String computeDocumentNodeName() {
                return PreferencesContext.this.computeDocumentNodeName();
            }

            @Override
            public String getCurrentFilePath() {
                return PreferencesContext.this.getCurrentFilePath();
            }
        };
    }

    /**
     * Generates a document node key for the specified document file name.
     * Preferences keys are limited to 80 chars so we cannot use the document path.
     *
     * To eliminate the need to iterate the nodes and compare the contained path to
     * the file path we use a MD5 checksum of the current document path prepended to
     * a substring of the document path. Like this we have predictability and
     * unicity with a decent collision probability
     *
     * we use it as this document key.
     *
     * @param name The document file name
     * @return
     */

    public static String generateKey(String name) {

        String hash = hash(name);
        int maxlength = Preferences.MAX_KEY_LENGTH - hash.length() - HASH_PATH_SEPARATOR.length();
        if (name.length() > maxlength) {
            name = name.substring(name.length() - maxlength);
        }

        return hash + HASH_PATH_SEPARATOR + name;
    }

    private static String hash(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            // return hexadecimal value
            return String.format("%040x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            // MD5 is here no panic
            return null;
        }
    }
}
