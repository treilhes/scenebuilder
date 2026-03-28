/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package org.fxml.serializer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

@ExtendWith({ApplicationExtension.class,MockitoExtension.class})
class FXMLSerializerTest {

    @Test
    void must_contains_import_and_namespace_and_root_object_and_simple_properties() {

        Pane p = new Pane();
        p.setId("xxxxx");
        p.setMaxWidth(500.0);
        p.setMinWidth(500.0);
        p.setTranslateY(50.0);

        p.getChildren().add(new Label("LLLAAABBBEEELLL"));
        p.setClip(new Rectangle(50, 50));

        FXMLSerializer serializer = new FXMLSerializer();
        String fxml = serializer.serialize(p);

        System.out.println(fxml);
        assertNotNull(fxml);
        assertTrue("must contains import declaration",fxml.contains("<?import javafx.scene.layout.Pane?>"));
        assertTrue("must contains pane object", fxml.contains("<Pane "));
        assertTrue("must contains javafx namespace declaration", fxml.contains("xmlns=\"http://javafx.com/javafx/"));
        assertTrue("must contains fx namespace declaration", fxml.contains("xmlns:fx=\"http://javafx.com/fxml/1\""));
        assertTrue("must contains property maxWidth", fxml.contains("maxWidth=\"500.0\""));
        assertTrue("must contains property minWidth", fxml.contains("minWidth=\"500.0\""));
        assertTrue("must contains property translateY", fxml.contains("translateY=\"50.0\""));

    }

}
