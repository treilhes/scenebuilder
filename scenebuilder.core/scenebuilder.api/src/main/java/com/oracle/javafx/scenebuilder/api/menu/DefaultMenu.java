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
        public static final String ID = "fileMenu";
        public static final String NEW_ID = ID + "-new";
        //public static final String NEW_FROM_TEMPLATE_ID = ID + "-newfromtemplate";
        public static final String OPEN_ID = ID + "-open";
        public static final String OPEN_RECENT_ID = ID + "-openrecent";
        public static final String SAVE_ID = ID + "-save";
        public static final String SAVE_AS_ID = ID + "-saveas";
        public static final String REVERT_TO_SAVED_ID = ID + "-reverttosaved";
        public static final String REVEAL_ID = ID + "-reveal";
        public static final String IMPORT_ID = ID + "-import";
        public static final String INCLUDE_ID = ID + "-include";
        public static final String CLOSE_WINDOW_ID = ID + "-closewindow";
        public static final String PREFERENCE_ID = ID + "-preference";
        public static final String QUIT_ID = ID + "-quit";
    }

    public interface Edit {
        public static final String ID = "editMenu";
        public static final String UNDO_ID = ID + "-undo";
        public static final String REDO_ID = ID + "-redo";
        public static final String CUT_ID = ID + "-cut";
        public static final String COPY_ID = ID + "-copy";
        public static final String PASTE_ID = ID + "-paste";
        public static final String PASTE_INTO_ID = ID + "-pasteinto";
        public static final String DUPLICATE_ID = ID + "-duplicate";
        public static final String DELETE_ID = ID + "-delete";
        public static final String SELECT_ALL_ID = ID + "-selectall";
        public static final String SELECT_NONE_ID = ID + "-selectnone";
        public static final String SELECT_PARENT_ID = ID + "-selectparent";
        public static final String SELECT_NEXT_ID = ID + "-selectnext";
        public static final String SELECT_PREVIOUS_ID = ID + "-selectprevious";
        public static final String TRIM_DOC_ID = ID + "-trimdoc";
    }

    public interface View {
        public static final String ID = "viewMenu";
        public static final String SHOW_VIEWS_ID = ID + "-showviews";
        public static final String CONTENT_ID = ID + "-content";
        public static final String PROPERTIES_ID = ID + "-properties";
        public static final String LAYOUT_ID = ID + "-layout";
        public static final String CODE_ID = ID + "-code";
        public static final String LIBRARY_TOGGLE_ID = ID + "-librarytoggle";
        public static final String DOCUMENT_TOGGLE_ID = ID + "-documenttoggle";

        public static final String LEFT_PANE_TOGGLE_ID = ID + "-leftpanetoggle";
        public static final String RIGHT_PANE_TOGGLE_ID = ID + "-rightpanetoggle";
        public static final String OUTLINES_TOGGLE_ID = ID + "-outlinestoggle";
        public static final String SAMPLE_DATA_TOGGLE_ID = ID + "-sampledatatoggle";
        public static final String GUIDES_TOGGLE_ID = ID + "-guidestoggle";
        public static final String ZOOM_ID = ID + "-zoom";
        public static final String SAMPLE_CONTROLLER_ID = ID + "-samplecontroller";
    }

    public interface Insert {
        public static final String ID = "insertMenu";
    }

    public interface Modify {
        public static final String ID = "modifyMenu";
        public static final String FIT_TO_PARENT_ID = ID + "-fittoparent";
        public static final String USE_COMPUTED_SIZE_ID = ID + "-usecomputedsize";
        public static final String GRIDPANE_ID = ID + "-gridpane";
        public static final String EFFECTS_ID = ID + "-effects";
        public static final String POPUP_CONTROL_ID = ID + "-popupcontrol";
        public static final String SCENE_SIZE_ID = ID + "-scenesize";
    }

    public interface Arrange {
        public static final String ID = "arrangeMenu";
        public static final String BRING_TO_FRONT_ID = ID + "-bringtofront";
        public static final String SEND_TO_BACK_ID = ID + "-sendtoback";
        public static final String BRING_FORWARD_ID = ID + "-bringforward";
        public static final String BRING_BACKWARD_ID = ID + "-bringbackward";
        public static final String WRAP_ID = ID + "-wrap";
        public static final String UNWRAP_ID = ID + "-unwrap";
    }

    public interface Preview {
        public static final String ID = "previewMenu";
        public static final String SHOW_IN_WINDOW_ID = ID + "-showinwindow";
        public static final String SHOW_IN_DIALOG_ID = ID + "-showindialog";
        public static final String THEME_ID = ID + "-theme";
        public static final String STYLESHEETS_ID = ID + "-stylesheets";
        public static final String INTERNATIONALIZATION_ID = ID + "-internationalization";
        public static final String PREVIEW_SIZE_ID = ID + "-previewsize";
    }

    public interface Window {
        public static final String ID = "windowMenu";
    }

    public interface Help {
        public static final String ID = "helpMenu";
        public static final String SB_HELP_ID = ID + "-sbhelp";
        public static final String JAVAFX_ID = ID + "-javafx";
        public static final String CONTRIBUTE_ID = ID + "-contribute";
        public static final String CHECK_UPDATE_ID = ID + "-checkupdate";
        public static final String REGISTER_ID = ID + "-register";
        public static final String SHOW_WELCOME_ID = ID + "-showwelcome";
        public static final String ABOUT_ID = ID + "-about";
    }

    @Deprecated
    public interface Debug {
        @Deprecated
        public static final String ID = "debugMenu";
    }
}

