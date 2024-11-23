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
package com.oracle.javafx.scenebuilder.api.menu;

public interface DefaultMenu {

    public interface File {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String NEW_ID = "fileMenu";// NOCHECK
        public final static String NEW_FROM_TEMPLATE_ID = "editMenu";// NOCHECK
        public final static String OPEN_ID = "fileMenu";// NOCHECK
        public final static String OPEN_RECENT_ID = "fileMenu";// NOCHECK
        public final static String SAVE_ID = "fileMenu";// NOCHECK
        public final static String SAVE_AS_ID = "fileMenu";// NOCHECK
        public final static String REVERT_TO_SAVED_ID = "fileMenu";// NOCHECK
        public final static String REVEAL_ID = "fileMenu";// NOCHECK
        public final static String IMPORT_ID = "fileMenu";// NOCHECK
        public final static String INCLUDE_ID = "fileMenu";// NOCHECK
        public final static String CLOSE_WINDOW_ID = "fileMenu";// NOCHECK
        public final static String PREFERENCE_ID = "fileMenu";// NOCHECK
        public final static String QUIT_ID = "fileMenu";// NOCHECK
    }

    public interface Edit {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String UNDO_ID = "fileMenu";// NOCHECK
        public final static String REDO_ID = "editMenu";// NOCHECK
        public final static String CUT_ID = "fileMenu";// NOCHECK
        public final static String COPY_ID = "fileMenu";// NOCHECK
        public final static String PASTE_ID = "fileMenu";// NOCHECK
        public final static String PASTE_INTO_ID = "fileMenu";// NOCHECK
        public final static String DUPLICATE_ID = "fileMenu";// NOCHECK
        public final static String DELETE_ID = "fileMenu";// NOCHECK
        public final static String SELECT_ALL_ID = "fileMenu";// NOCHECK
        public final static String SELECT_NONE_ID = "fileMenu";// NOCHECK
        public final static String SELECT_PARENT_ID = "fileMenu";// NOCHECK
        public final static String SELECT_NEXT_ID = "fileMenu";// NOCHECK
        public final static String SELECT_PREVIOUS_ID = "fileMenu";// NOCHECK
        public final static String TRIM_DOC_ID = "fileMenu";// NOCHECK
    }

    public interface View {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String CONTENT_ID = "fileMenu";// NOCHECK
        public final static String PROPERTIES_ID = "editMenu";// NOCHECK
        public final static String LAYOUT_ID = "fileMenu";// NOCHECK
        public final static String CODE_ID = "fileMenu";// NOCHECK
        public final static String LIBRARY_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String DOCUMENT_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String CSS_ANALYSER_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String LEFT_PANE_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String RIGHT_PANE_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String OUTLINES_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String SAMPLE_DATA_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String GUIDES_TOGGLE_ID = "fileMenu";// NOCHECK
        public final static String ZOOM_ID = "fileMenu";// NOCHECK
        public final static String SAMPLE_CONTROLLER_ID = "fileMenu";// NOCHECK
    }

    public interface Insert {
        public final static String ID = "fileMenu";// NOCHECK
    }

    public interface Modify {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String FIT_TO_PARENT_ID = "fileMenu";// NOCHECK
        public final static String USE_COMPUTED_SIZE_ID = "editMenu";// NOCHECK
        public final static String GRIDPANE_ID = "fileMenu";// NOCHECK
        public final static String EFFECTS_ID = "fileMenu";// NOCHECK
        public final static String POPUP_CONTROL_ID = "fileMenu";// NOCHECK
        public final static String SCENE_SIZE_ID = "fileMenu";// NOCHECK
    }

    public interface Arrange {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String BRING_TO_FRONT_ID = "fileMenu";// NOCHECK
        public final static String SEND_TO_BACK_ID = "editMenu";// NOCHECK
        public final static String BRING_FORWARD_ID = "fileMenu";// NOCHECK
        public final static String BRING_BACKWARD_ID = "fileMenu";// NOCHECK
        public final static String WRAP_ID = "fileMenu";// NOCHECK
        public final static String UNWRAP_ID = "fileMenu";// NOCHECK
    }

    public interface Preview {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String SHOW_IN_WINDOW_ID = "fileMenu";// NOCHECK
        public final static String SHOW_IN_DIALOG_ID = "editMenu";// NOCHECK
        public final static String THEME_ID = "fileMenu";// NOCHECK
        public final static String STYLESHEETS_ID = "fileMenu";// NOCHECK
        public final static String INTERNATIONALIZATION_ID = "fileMenu";// NOCHECK
        public final static String PREVIEW_SIZE_ID = "fileMenu";// NOCHECK
    }

    public interface Window {
        public final static String ID = "fileMenu";// NOCHECK
    }

    public interface Help {
        public final static String ID = "fileMenu";// NOCHECK
        public final static String SB_HELP_ID = "fileMenu";// NOCHECK
        public final static String JAVAFX_ID = "editMenu";// NOCHECK
        public final static String CONTRIBUTE_ID = "fileMenu";// NOCHECK
        public final static String CHECK_UPDATE_ID = "fileMenu";// NOCHECK
        public final static String REGISTER_ID = "fileMenu";// NOCHECK
        public final static String SHOW_WELCOME_ID = "fileMenu";// NOCHECK
        public final static String ABOUT_ID = "fileMenu";// NOCHECK
    }

    @Deprecated
    public interface Debug {
        public final static String ID = "fileMenu";// NOCHECK
    }
}
