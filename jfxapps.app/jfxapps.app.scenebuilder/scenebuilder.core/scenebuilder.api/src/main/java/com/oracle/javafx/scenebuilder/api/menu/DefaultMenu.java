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
        public final static String ID = "fileMenu";
        public final static String NEW_ID = ID + "-new";
        public final static String NEW_FROM_TEMPLATE_ID = ID + "-newfromtemplate";
        public final static String OPEN_ID = ID + "-open";
        public final static String OPEN_RECENT_ID = ID + "-openrecent";
        public final static String SAVE_ID = ID + "-save";
        public final static String SAVE_AS_ID = ID + "-saveas";
        public final static String REVERT_TO_SAVED_ID = ID + "-reverttosaved";
        public final static String REVEAL_ID = ID + "-reveal";
        public final static String IMPORT_ID = ID + "-import";
        public final static String INCLUDE_ID = ID + "-include";
        public final static String CLOSE_WINDOW_ID = ID + "-closewindow";
        public final static String PREFERENCE_ID = ID + "-preference";
        public final static String QUIT_ID = ID + "-quit";
    }

    public interface Edit {
        public final static String ID = "editMenu";
        public final static String UNDO_ID = ID + "-undo";
        public final static String REDO_ID = ID + "-redo";
        public final static String CUT_ID = ID + "-cut";
        public final static String COPY_ID = ID + "-copy";
        public final static String PASTE_ID = ID + "-paste";
        public final static String PASTE_INTO_ID = ID + "-pasteinto";
        public final static String DUPLICATE_ID = ID + "-duplicate";
        public final static String DELETE_ID = ID + "-delete";
        public final static String SELECT_ALL_ID = ID + "-selectall";
        public final static String SELECT_NONE_ID = ID + "-selectnone";
        public final static String SELECT_PARENT_ID = ID + "-selectparent";
        public final static String SELECT_NEXT_ID = ID + "-selectnext";
        public final static String SELECT_PREVIOUS_ID = ID + "-selectprevious";
        public final static String TRIM_DOC_ID = ID + "-trimdoc";
    }

    public interface View {
        public final static String ID = "viewMenu";
        public final static String CONTENT_ID = ID + "-content";
        public final static String PROPERTIES_ID = ID + "-properties";
        public final static String LAYOUT_ID = ID + "-layout";
        public final static String CODE_ID = ID + "-code";
        public final static String LIBRARY_TOGGLE_ID = ID + "-librarytoggle";
        public final static String DOCUMENT_TOGGLE_ID = ID + "-documenttoggle";
        public final static String CSS_ANALYSER_TOGGLE_ID = ID + "-cssanalysertoggle";
        public final static String LEFT_PANE_TOGGLE_ID = ID + "-leftpanetoggle";
        public final static String RIGHT_PANE_TOGGLE_ID = ID + "-rightpanetoggle";
        public final static String OUTLINES_TOGGLE_ID = ID + "-outlinestoggle";
        public final static String SAMPLE_DATA_TOGGLE_ID = ID + "-sampledatatoggle";
        public final static String GUIDES_TOGGLE_ID = ID + "-guidestoggle";
        public final static String ZOOM_ID = ID + "-zoom";
        public final static String SAMPLE_CONTROLLER_ID = ID + "-samplecontroller";
    }

    public interface Insert {
        public final static String ID = "insertMenu";
    }

    public interface Modify {
        public final static String ID = "modifyMenu";
        public final static String FIT_TO_PARENT_ID = ID + "-fittoparent";
        public final static String USE_COMPUTED_SIZE_ID = ID + "-usecomputedsize";
        public final static String GRIDPANE_ID = ID + "-gridpane";
        public final static String EFFECTS_ID = ID + "-effects";
        public final static String POPUP_CONTROL_ID = ID + "-popupcontrol";
        public final static String SCENE_SIZE_ID = ID + "-scenesize";
    }

    public interface Arrange {
        public final static String ID = "arrangeMenu";
        public final static String BRING_TO_FRONT_ID = ID + "-bringtofront";
        public final static String SEND_TO_BACK_ID = ID + "-sendtoback";
        public final static String BRING_FORWARD_ID = ID + "-bringforward";
        public final static String BRING_BACKWARD_ID = ID + "-bringbackward";
        public final static String WRAP_ID = ID + "-wrap";
        public final static String UNWRAP_ID = ID + "-unwrap";
    }

    public interface Preview {
        public final static String ID = "previewMenu";
        public final static String SHOW_IN_WINDOW_ID = ID + "-showinwindow";
        public final static String SHOW_IN_DIALOG_ID = ID + "-showindialog";
        public final static String THEME_ID = ID + "-theme";
        public final static String STYLESHEETS_ID = ID + "-stylesheets";
        public final static String INTERNATIONALIZATION_ID = ID + "-internationalization";
        public final static String PREVIEW_SIZE_ID = ID + "-previewsize";
    }

    public interface Window {
        public final static String ID = "windowMenu";
    }

    public interface Help {
        public final static String ID = "helpMenu";
        public final static String SB_HELP_ID = ID + "-sbhelp";
        public final static String JAVAFX_ID = ID + "-javafx";
        public final static String CONTRIBUTE_ID = ID + "-contribute";
        public final static String CHECK_UPDATE_ID = ID + "-checkupdate";
        public final static String REGISTER_ID = ID + "-register";
        public final static String SHOW_WELCOME_ID = ID + "-showwelcome";
        public final static String ABOUT_ID = ID + "-about";
    }

    @Deprecated
    public interface Debug {
        public final static String ID = "debugMenu";
    }
}

