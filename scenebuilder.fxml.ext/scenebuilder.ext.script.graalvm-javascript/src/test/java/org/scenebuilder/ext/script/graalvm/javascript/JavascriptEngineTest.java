package org.scenebuilder.ext.script.graalvm.javascript;

import static org.junit.jupiter.api.Assertions.fail;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;

class JavascriptEngineTest {

    @Test
    void test() {

     // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();

        factory.getEngineFactories().forEach(e -> {
            System.out.println(e.getNames());
            System.out.println(e.getLanguageName());
            System.out.println();
        });
        // create JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from given file - specified by first argument
        //engine.eval(new java.io.FileReader(args[0]));

        fail("Not yet implemented");
    }

}
