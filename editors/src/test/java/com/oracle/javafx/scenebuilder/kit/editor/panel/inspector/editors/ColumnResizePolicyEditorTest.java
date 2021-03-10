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
package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TreeTableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.editors.control.ColumnResizePolicyEditor;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

@ExtendWith(ApplicationExtension.class)
public class ColumnResizePolicyEditorTest {

    static {
        I18N.initForTest();
    }
    
    static TableViewResizePolicyPropertyMetadata someTableViewResizePolicyProp() {
        return new TableViewResizePolicyPropertyMetadata(new PropertyName("tv"), true,
                TableView.UNCONSTRAINED_RESIZE_POLICY, null);
    }

    static TreeTableViewResizePolicyPropertyMetadata someTreeTableViewResizePolicyProp() {
        return new TreeTableViewResizePolicyPropertyMetadata(new PropertyName("ttv"), true,
                TreeTableView.UNCONSTRAINED_RESIZE_POLICY, null);
    }

    @Test
    public void shouldCreateAnEmptyInstance() {
        
        ColumnResizePolicyEditor o = new ColumnResizePolicyEditor(MockObjects.buildApiMock());
        
        assertNotNull(o);
    }

    @Test
    public void shouldCreateAnEmptyMenu() {
        
        ColumnResizePolicyEditor o = new ColumnResizePolicyEditor(MockObjects.buildApiMock());
        
        assertNotNull(o.getMenu());
    }

    @Test
    public void shouldResetTheInstanceForTableView() {
        
        ColumnResizePolicyEditor o = new ColumnResizePolicyEditor(MockObjects.buildApiMock());
        
        o.reset(someTableViewResizePolicyProp(), null);
    }
    
    @Test
    public void shouldResetTheInstanceForTreeTableView() {
        
        ColumnResizePolicyEditor o = new ColumnResizePolicyEditor(MockObjects.buildApiMock());
        
        o.reset(someTreeTableViewResizePolicyProp(), null);
    }

}