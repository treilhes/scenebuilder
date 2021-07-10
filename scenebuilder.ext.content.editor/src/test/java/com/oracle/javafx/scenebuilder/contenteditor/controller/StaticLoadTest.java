package com.oracle.javafx.scenebuilder.contenteditor.controller;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.test.JfxInitializer;

/**
 * Unit test for {@link com.oracle.javafx.scenebuilder.core.util.Deprecation#setStaticLoad(javafx.fxml.FXMLLoader, boolean) }
 */
@ExtendWith(MockitoExtension.class)
public class StaticLoadTest {

	private static I18N i18nTest = new I18N(new ArrayList<>()) {
		@Override
		public String get(String key) {
			return "fake";
		}
	};

    private boolean thrown;

//    @Mock
//    private Library library;
    @Mock
    private FileSystem fileSystem;
    private DocumentManager docManager = new DocumentManager.DocumentManagerImpl();

    @BeforeAll
    public static void initJFX() {
        JfxInitializer.initialize();
    }

    @Test
    public void testStaticLoadWithoutEventHandler() throws IOException {
        thrown = false;
        EditorController editorController = new EditorController(MockObjects.buildApiMock(), null, null);
        final URL fxmlURL = StaticLoadTest.class.getResource("testStaticLoadWithoutEventHandler.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    }

    @Test
    public void testStaticLoad() throws IOException {
        thrown = false;
        EditorController editorController = new EditorController(MockObjects.buildApiMock(), null, null);
        final URL fxmlURL = StaticLoadTest.class.getResource("testStaticLoad.fxml");
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        } catch (IOException e) {
           thrown = true;
        }

        assertFalse(thrown);
    }
}
