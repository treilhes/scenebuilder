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
