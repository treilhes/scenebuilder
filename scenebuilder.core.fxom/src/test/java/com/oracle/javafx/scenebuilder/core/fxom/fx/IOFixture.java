package com.oracle.javafx.scenebuilder.core.fxom.fx;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMSaver;
import com.oracle.javafx.scenebuilder.core.fxom.fx.script.FxomFxScriptTagTest;

import javafx.fxml.FXMLLoader;

public class IOFixture {

    public static final String JFX_VERSION = "xxx";

    public static void testIsLoadableByJavafx(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(owner.getClass().getResource(fileName));
            loader.load(stream);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testIsFxomLoadable(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)){
            new FXOMDocument(new String(stream.readAllBytes()), null, IOFixture.class.getClassLoader(), null);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testIsFxomSerializable(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)){
            FXOMDocument fxomDocument = new FXOMDocument(new String(stream.readAllBytes()), null, FxomFxScriptTagTest.class.getClassLoader(), null);
            new FXOMSaver().save(fxomDocument);
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }

    public static void testSerializedIsEqualToSource(Object owner, String fileName, boolean failureExpected) {
        try (var stream = owner.getClass().getResourceAsStream(fileName)){
            String content = new String(stream.readAllBytes());
            FXOMDocument fxomDocument = new FXOMDocument(content, null, IOFixture.class.getClassLoader(), null);
            String serializedContent = new FXOMSaver().save(fxomDocument, JFX_VERSION);
            assertNotNull(serializedContent);
            assertEquals(content.trim(), serializedContent.trim());
        } catch (IOException e) {
            if (!failureExpected) {
                fail(e);
            }
        }
    }
}
