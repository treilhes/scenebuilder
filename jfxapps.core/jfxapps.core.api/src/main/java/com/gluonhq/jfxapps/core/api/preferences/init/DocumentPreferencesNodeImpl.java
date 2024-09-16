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
package com.gluonhq.jfxapps.core.api.preferences.init;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.preferences.DocumentPreferencesNode;

@Singleton
public class DocumentPreferencesNodeImpl implements DocumentPreferencesNode {

    private final static Logger logger = LoggerFactory.getLogger(DocumentPreferencesNodeImpl.class);

	// PREFERENCES NODE NAME
    static final String NODE_NAME = "DOCUMENTS"; //NOCHECK

	@Override
	public Preferences getNode() {
		return Preferences.userNodeForPackage(getClass())
				.node(RootPreferencesNodeImpl.SB_RELEASE_NODE).node(NODE_NAME);
	}

	@Override
	public void cleanupCorruptedNodes() {
		// Cleanup document preferences at start time :
		// Remove document preferences node if needed
        try {
            final String[] childrenNames = getNode().childrenNames();
            // Check among the document root chidlren if there is a child
            // which path matches the specified one
            for (String child : childrenNames) {
                final Preferences documentPreferences = getNode().node(child);
                final String nodePath = documentPreferences.get(DocumentPreferencesNode.PATH_PREFERENCE_KEY, null);
                // Each document node defines a path
                // If path is null or empty, this means preferences DB has been corrupted
                if (nodePath == null || nodePath.isEmpty()) {
                    documentPreferences.removeNode();
                }
            }
        } catch (BackingStoreException ex) {
            logger.error("", ex);
        }
	}

	@Override
	public void clearAllDocumentNodes() {
        try {
            final String[] childrenNames = getNode().childrenNames();
            for (String child : childrenNames) {
                getNode().node(child).removeNode();
            }
        } catch (BackingStoreException ex) {
            logger.error("", ex);
        }
	}

}
