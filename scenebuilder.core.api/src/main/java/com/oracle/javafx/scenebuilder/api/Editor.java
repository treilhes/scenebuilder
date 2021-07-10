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
import java.util.List;

import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.util.Callback;

public interface Editor {


	/**
     * Returns null or the location of the fxml being edited.
     *
     * @return null or the location of the fxml being edited.
     */
	public URL getFxmlLocation();

	public Selection getSelection();

	//TODO to remove
	public FXOMDocument getFxomDocument();

	public Stage getOwnerWindow();

	//public void setTheme(Class<? extends Theme> themeClass);

	//public Class<? extends Theme> getTheme();

	//public boolean isSelectionNode();

	//public Library getLibrary();

	public boolean canPerformControlAction(ControlAction copy);
	
	public void performEditAction(EditAction editAction);

	public JobManager getJobManager();

	public void performControlAction(ControlAction copy);

	//public Drag getDragController();

	public ContextMenu getContextMenuController();

	public MessageLogger getMessageLog();

	public InlineEdit getInlineEditController();

	public boolean isTextEditingSessionOnGoing();

	public boolean canGetFxmlText();

//	public ObservableValue<FXOMDocument> fxomDocumentProperty();

//	public ObservableValue<String> toolStylesheetProperty();

//	public String getToolStylesheet();

	//public Glossary getGlossary();

	public ErrorReport getErrorReport();

	public ObservableValue<URL> fxmlLocationProperty();

	//public ObservableListValue<File> sceneStyleSheetProperty();

	//public List<File> getSceneStyleSheets();

	public void performIncludeFxml(File fxmlFile);

	public File getIncludedFile();

	public void setPickModeEnabled(boolean b);

	public boolean isPickModeEnabled();

//	public boolean canPerformInsert(LibraryItem item);
//
//	public void performInsert(LibraryItem item);

	//public ObservableValue<Library> libraryProperty();

    public ObservableValue<Boolean> sampleDataEnabledProperty();

    public ObservableValue<Boolean> pickModeEnabledProperty();

    public void textEditingSessionDidBegin(Callback<Void, Boolean> requestSessionEnd);

    public void textEditingSessionDidEnd();

    /**
     * An 'edit' action is an action which modifies the document associated
     * to this editor. It makes the document dirty and pushes a
     * new item on the undo/redo stack.
     */
    public enum EditAction {
        // Candidates for Edit menu
        CUT,
        PASTE,
        PASTE_INTO,
        DUPLICATE,
        DELETE,
        TRIM,
        TOGGLE_FX_ROOT,
        // Candidates for Modify menu
        FIT_TO_PARENT,
        USE_COMPUTED_SIZES,
        ADD_CONTEXT_MENU,
        ADD_TOOLTIP,
        SET_SIZE_335x600,
        SET_SIZE_900x600,
        SET_SIZE_320x240,
        SET_SIZE_640x480,
        SET_SIZE_1280x800,
        SET_SIZE_1920x1080,
        // Candidates for Modify/GridPane menu
        MOVE_ROW_ABOVE,
        MOVE_ROW_BELOW,
        MOVE_COLUMN_BEFORE,
        MOVE_COLUMN_AFTER,
        ADD_ROW_ABOVE,
        ADD_ROW_BELOW,
        ADD_COLUMN_BEFORE,
        ADD_COLUMN_AFTER,
        INCREASE_ROW_SPAN,
        DECREASE_ROW_SPAN,
        INCREASE_COLUMN_SPAN,
        DECREASE_COLUMN_SPAN,
        // Candidates for Arrange menu
        BRING_TO_FRONT,
        SEND_TO_BACK,
        BRING_FORWARD,
        SEND_BACKWARD,
        UNWRAP,
        WRAP_IN_ANCHOR_PANE,
        WRAP_IN_BORDER_PANE,
        WRAP_IN_BUTTON_BAR,
        WRAP_IN_DIALOG_PANE,
        WRAP_IN_FLOW_PANE,
        WRAP_IN_GRID_PANE,
        WRAP_IN_GROUP,
        WRAP_IN_HBOX,
        WRAP_IN_PANE,
        WRAP_IN_SCROLL_PANE,
        WRAP_IN_SPLIT_PANE,
        WRAP_IN_STACK_PANE,
        WRAP_IN_TAB_PANE,
        WRAP_IN_TEXT_FLOW,
        WRAP_IN_TILE_PANE,
        WRAP_IN_TITLED_PANE,
        WRAP_IN_TOOL_BAR,
        WRAP_IN_VBOX,
        WRAP_IN_SCENE,
        WRAP_IN_STAGE
    }

    public boolean canPerformEditAction(EditAction editAction);

    public String getFxmlText(boolean wildcardImports);

    public void initialize();

    public void setFxmlTextAndLocation(String fxmlText, URL fxmlURL, boolean b) throws IOException;

    public void setOwnerWindow(Stage stage);

    public List<FXOMObject> getSelectedObjects();

    public void performImportFxml(File fxmlFile);

    public void performImportMedia(File mediaFile);

    public void setFxmlLocation(URL newLocation);

    public boolean isSampleDataEnabled();

    public String getUndoDescription();

    public boolean canRedo();

    public void redo();

    //public void performSetEffect(Class<? extends Effect> effectClass);

    //public boolean canPerformSetEffect();

    public void undo();

    public String getRedoDescription();

    public boolean canUndo();

    public void performEditIncludedFxml();

    public void performRevealIncludeFxml();

    
}
