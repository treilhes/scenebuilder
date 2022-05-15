package com.oracle.javafx.scenebuilder.core.fxom;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueComment;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class FXOMComment extends FXOMVirtual {

    FXOMComment(FXOMDocument fxomDocument, GlueElement glueElement, String comment) {
        super(fxomDocument, glueElement, null);
        setComment(comment);
    }

    FXOMComment(FXOMDocument fxomDocument, String comment) {
        this(fxomDocument, new GlueComment(fxomDocument.getGlue()), comment);
    }

    @Override
    protected void collectDeclaredClasses(Set<Class<?>> result) {
        // nothing to collect here
    }

    @Override
    protected void collectProperties(PropertyName propertyName, List<FXOMProperty> result) {
        // nothing to collect here
    }

    @Override
    protected void collectNullProperties(List<FXOMPropertyT> result) {
        // nothing to collect here
    }

    @Override
    protected void collectPropertiesT(List<FXOMPropertyT> result) {
        // nothing to collect here
    }

    @Override
    protected void collectReferences(String source, List<FXOMIntrinsic> result) {
        // nothing to collect here
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
        // nothing to collect here
    }

    @Override
    protected void collectIncludes(String source, List<FXOMIntrinsic> result) {
        // nothing to collect here
    }

    @Override
    protected void collectFxIds(Map<String, FXOMObject> result) {
        // nothing to collect here
    }

    @Override
    protected void collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass, List<FXOMObject> result) {
        // nothing to collect here
    }

    @Override
    protected void collectEventHandlers(List<FXOMPropertyT> result) {
        // nothing to collect here
    }

    @Override
    public List<FXOMObject> getChildObjects() {
        return Collections.emptyList();
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        // nothing to do here
    }

    @Override
    protected void collectScripts(String source, List<FXOMScript> result) {
        // nothing to collect here
    }

    @Override
    protected void collectComments(List<FXOMComment> result) {
        assert result != null;
        result.add(this);
    }

    public String getComment() {
//        StringBuilder builder = new StringBuilder();
//
//        getGlueElement().getFront().forEach(g -> {
//            if (g instanceof GlueCharacters) {
//                builder.append(((GlueCharacters)g).getData());
//            }
//        });
//        getGlueElement().getContent().forEach(g -> {
//            if (g instanceof GlueCharacters) {
//                builder.append(((GlueCharacters)g).getData());
//            }
//        });
//        getGlueElement().getTail().forEach(g -> {
//            if (g instanceof GlueCharacters) {
//                builder.append(((GlueCharacters)g).getData());
//            }
//        });
//
//        return builder.toString();
        return getGlueElement().getContentText();
    }

    public void setComment(String comment) {
//        getGlueElement().getFront().clear();
//        getGlueElement().getContent().clear();
//        getGlueElement().getTail().clear();
        getGlueElement().setContentText(comment);
    }
}
