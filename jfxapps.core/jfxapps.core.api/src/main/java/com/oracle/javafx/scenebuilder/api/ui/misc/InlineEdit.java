/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.ui.misc;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.util.Callback;

public interface InlineEdit {

    public static final String INLINE_EDITOR_CLASS = "inline-editor"; //NOCHECK

	public enum Type {

        TEXT_AREA, TEXT_FIELD
    }

	/**
     * Helper method to create a TextInputControl using the specified target
     * bounds and initial value.
     *
     * @param type the edit field type
     * @param inlineEditingBounds target
     * @param text initial value
     * @return the editable field
     */
	public TextInputControl createTextInputControl(Type type, Node inlineEditingBounds, String text);

	/**
     * Start an inline editing session. Display the specified TextInputControl
     * within a new popup window at the specified anchor node position.
     *
     * @param editor
     * @param anchor
     * @param requestCommit
     * @param requestRevert
     */
	public void startEditingSession(TextInputControl inlineEditor, Node inlineEditingBounds,
			Callback<String, Boolean> requestCommit, Callback<String, Boolean> requestRevert);

    public boolean isWindowOpened();

    /**
     * Returns true if fxml content being edited can be returned safely.
     * This method will return false if there is a text editing session on-going.
     *
     * @return true if fxml content being edited can be returned safely.
     */
    boolean canGetFxmlText();

    /**
     * Tells this editor that a text editing session has started.
     * The editor controller may invoke the requestSessionEnd() callback
     * if it needs the text editing session to stop. The callback should;
     *   - either stop the text editing session, invoke textEditingSessionDidEnd()
     *     and return true
     *   - either keep the text editing session on-going and return false
     *
     * @param requestSessionEnd Callback that should end the text editing session or return false
     */
    void textEditingSessionDidBegin(Callback<Void, Boolean> requestSessionEnd);

    /**
     * Tells this editor that the text editing session has ended.
     */
    void textEditingSessionDidEnd();

    /**
     * Returns true if a text editing session is currently on going.
     */
    boolean isTextEditingSessionOnGoing();

    /**
     * Returns true if the specified node is part of the main scene and is either a
     * TextInputControl or a ComboBox.
     *
     * @param node the focused node of the main scene
     * @return
     */
    boolean isTextInputControlEditing(Node node);

    /**
     * Gets the text input control from the node.
     * Works only if {@link InlineEdit#isTextInputControlEditing(Node)} return true
     *
     * @param node the focused node of the main scene
     * @return the text input control
     */
    TextInputControl getTextInputControl(Node node);

    /**
     * Returns true if we are editing within a popup window : either the specified
     * node is showing a popup window or the inline editing popup is showing.
     *
     * @param node the focused node of the main scene
     * @return
     */
    boolean isPopupEditing(Node node);
}
