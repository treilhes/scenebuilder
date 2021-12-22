package com.oracle.javafx.scenebuilder.core.fxom.glue;

public class GlueComment extends GlueElement {

    public static final String COMMENT_TAG_NAME = "fx:comment"; // in place of !--

    public GlueComment(GlueDocument document, GlueElement template) {
        super(document, COMMENT_TAG_NAME, template);
        // TODO Auto-generated constructor stub
    }

    public GlueComment(GlueDocument document, int indentDepth, boolean preset) {
        super(document, COMMENT_TAG_NAME, indentDepth, preset);
        // TODO Auto-generated constructor stub
    }

    public GlueComment(GlueDocument document) {
        super(document, COMMENT_TAG_NAME);
        // TODO Auto-generated constructor stub
    }



}
