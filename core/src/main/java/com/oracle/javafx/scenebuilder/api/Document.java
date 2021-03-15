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
package com.oracle.javafx.scenebuilder.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface Document {
    //API validated
    boolean isInited();
    boolean isUnused();
    boolean isDocumentDirty();
    boolean hasContent();
    boolean hasName();
    String getName();
    Stage getStage();
    ActionStatus save();
    ActionStatus saveAs();
    void closeWindow();
    void revert();
    
    //API to be validated
	
	void loadFromFile(File fxmlFile) throws IOException;
    void openWindow();
    Editor getEditorController();
    MenuBar getMenuBarController();
    void updatePreferences();
    
    Scene getScene();
    Content getContentPanelController();
    DocumentPanel getDocumentPanelController();
    void updateWithDefaultContent();
    
    
    public static class TitleComparator implements Comparator<Document> {

        @Override
        public int compare(Document d1, Document d2) {
            final int result;

            assert d1 != null;
            assert d2 != null;

            if (d1 == d2) {
                result = 0;
            } else {
                final String t1 = d1.getStage().getTitle();
                final String t2 = d2.getStage().getTitle();
                assert t1 != null;
                assert t2 != null;
                result = t1.compareTo(t2);
            }

            return result;
        }

    }

    void loadFromURL(URL url);
    ActionStatus performCloseAction();
    
    public enum DocumentControlAction {
        COPY,
        SELECT_ALL,
        SELECT_NONE,
        //SAVE_FILE,
        //SAVE_AS_FILE,
        //REVERT_FILE,
        CLOSE_FILE,
        //REVEAL_FILE,
        GOTO_CONTENT,
        GOTO_PROPERTIES,
        GOTO_LAYOUT,
        GOTO_CODE,
        TOGGLE_LIBRARY_PANEL,
        TOGGLE_DOCUMENT_PANEL,
        TOGGLE_CSS_PANEL,
        TOGGLE_LEFT_PANEL,
        TOGGLE_RIGHT_PANEL,
        TOGGLE_OUTLINES_VISIBILITY,
        TOGGLE_GUIDES_VISIBILITY,
        SHOW_PREVIEW_WINDOW,
        SHOW_PREVIEW_DIALOG,
        ADD_SCENE_STYLE_SHEET,
        SET_RESOURCE,
        REMOVE_RESOURCE,
        REVEAL_RESOURCE,
        HELP
    }

    public enum DocumentEditAction {
        DELETE,
        CUT,
        PASTE,
        //IMPORT_FXML,
        //IMPORT_MEDIA,
        //INCLUDE_FXML
    }

    public enum ActionStatus {
        CANCELLED,
        DONE
    }

    void performImportFxml();
    void performIncludeFxml();
    void performRevealAction();
    void performImportMedia();
    boolean isRightPanelVisible();
    void performControlAction(DocumentControlAction toggleRightPanel);
    
}
