package com.oracle.javafx.scenebuilder.core.fxom;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueCharacters;
import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class FXOMScript extends FXOMVirtual {

    private static final String CHARSET_PROPERTY = "charset";
    private static final String SOURCE_PROPERTY = "source";

    FXOMScript(FXOMDocument fxomDocument, GlueElement glueElement, Object sceneGraphObject) {
        super(fxomDocument, glueElement, sceneGraphObject);
    }

    public FXOMScript(FXOMDocument document, String source) {
        super(document, "fx:script");
        getGlueElement().getAttributes().put(SOURCE_PROPERTY, source);
    }


    @Override
    protected void collectDeclaredClasses(Set<Class<?>> result) {
    }

    @Override
    protected void collectProperties(PropertyName propertyName, List<FXOMProperty> result) {
    }

    @Override
    protected void collectNullProperties(List<FXOMPropertyT> result) {
    }

    @Override
    protected void collectPropertiesT(List<FXOMPropertyT> result) {
    }

    @Override
    protected void collectReferences(String source, List<FXOMIntrinsic> result) {
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
    }

    @Override
    protected void collectIncludes(String source, List<FXOMIntrinsic> result) {
    }

    @Override
    protected void collectFxIds(Map<String, FXOMObject> result) {
//        ScriptEngineManager scriptEngineManager = new javax.script.ScriptEngineManager();
//        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(getFxomDocument().getScriptingLanguage());
//        try {
//            Bindings engineBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
//            scriptEngine.eval(getScript());
//        } catch (ScriptException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Override
    protected void collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass, List<FXOMObject> result) {
    }

    @Override
    protected void collectEventHandlers(List<FXOMPropertyT> result) {
    }

    @Override
    public List<FXOMObject> getChildObjects() {
        return Collections.emptyList();
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        // TODO Auto-generated method stub

    }

    public String getSource() {
        return getGlueElement().getAttributes().get(SOURCE_PROPERTY);
    }

    public void setSource(String source) {
        if (source == null) {
            getGlueElement().getAttributes().remove(SOURCE_PROPERTY);
        } else {
            getGlueElement().getAttributes().put(SOURCE_PROPERTY, source);
        }
    }

    @Override
    protected void collectScripts(String source, List<FXOMScript> result) {
        assert result != null;

        if ((source == null) || source.equals(getSource())) {
            result.add(this);
        }
    }

    public String getScript() {
        StringBuilder builder = new StringBuilder();

        getGlueElement().getFront().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });
        getGlueElement().getContent().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });
        getGlueElement().getTail().forEach(g -> {
            if (g instanceof GlueCharacters) {
                builder.append(((GlueCharacters)g).getData());
            }
        });

        return builder.toString();
    }

    public void setScript(String script) {
        getGlueElement().getFront().clear();
        getGlueElement().getContent().clear();
        getGlueElement().getTail().clear();
        getGlueElement().setContentText(script);
    }

    @Override
    protected void collectComments(List<FXOMComment> result) {
        // TODO Auto-generated method stub

    }
}
