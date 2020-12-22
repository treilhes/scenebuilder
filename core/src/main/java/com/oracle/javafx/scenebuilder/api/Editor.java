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
import java.net.URL;

import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

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

	public FXOMDocument getFxomDocument();

	public Stage getOwnerWindow();

	//public void setTheme(Class<? extends Theme> themeClass);

	//public Class<? extends Theme> getTheme();

	public boolean isSelectionNode();

	public Library getLibrary();

	public boolean canPerformControlAction(ControlAction copy);

	public JobManager getJobManager();

	public double getDefaultRootContainerWidth();

	public double getDefaultRootContainerHeight();

	public void performControlAction(ControlAction copy);

	public Drag getDragController();

	public ContextMenu getContextMenuController();

	public MessageLogger getMessageLog();

	public InlineEdit getInlineEditController();

	public boolean isTextEditingSessionOnGoing();

	public boolean canGetFxmlText();

	public ObservableValue<FXOMDocument> fxomDocumentProperty();

//	public ObservableValue<String> toolStylesheetProperty();

//	public String getToolStylesheet();

	public Glossary getGlossary();

	public ErrorReport getErrorReport();

	public ObservableValue<URL> fxmlLocationProperty();

	//public ObservableListValue<File> sceneStyleSheetProperty();

	//public List<File> getSceneStyleSheets();

	public void performIncludeFxml(File fxmlFile);

	public File getIncludedFile();

	public void setPickModeEnabled(boolean b);

	public boolean isPickModeEnabled();

	public boolean canPerformInsert(LibraryItem item);

	public void performInsert(LibraryItem item);

	public ObservableValue<Library> libraryProperty();

    public ObservableValue<Boolean> sampleDataEnabledProperty();

    public boolean is3D();

    public ObservableValue<Boolean> pickModeEnabledProperty();

    public void textEditingSessionDidBegin(Callback<Void, Boolean> requestSessionEnd);

    public void textEditingSessionDidEnd();

    public boolean isNode();

}
