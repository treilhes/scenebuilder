package org.scenebuilder.ext.script.graalvm.javascript.runtime;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavascriptEngine {

    private final static String ENGINE_NAME = "graal.js";

    public void setup() {
        System.setProperty("polyglot.js.nashorn-compat", "true");
    }

    public void evaluate(String source) throws ScriptException {
        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
        graalEngine.eval(source);
    }
}
