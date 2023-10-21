package org.fxml.serializer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import javafx.scene.layout.Pane;

class FXMLSerializerTest {

    @Test
    void test() {
        Pane p = new Pane();
        FXMLSerializer serializer = new FXMLSerializer();
        String value = serializer.serialize(serializer);

        System.out.println(value);
    }

}
