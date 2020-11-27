package com.oracle.javafx.scenebuilder.api;

/**
 * A 'control' action does not modify the document. It only changes a
 * state or a mode in this editor.
 */
public enum ControlAction {
    // Candidates for Edit menu
    COPY,
    SELECT_ALL,
    SELECT_NONE,
    SELECT_PARENT,
    SELECT_NEXT,
    SELECT_PREVIOUS,
    EDIT_INCLUDED_FILE,
    REVEAL_INCLUDED_FILE,
    TOGGLE_CSS_SELECTION,
    TOGGLE_SAMPLE_DATA
}
