import org.scenebuilder.ext.script.graalvm.javascript.GraalVmJavascriptEngineExtension;

import com.treilhes.emc4j.boot.api.loader.extension.Extension;

open module scenebuilder.ext.script.graalvm.javascript {
    exports org.scenebuilder.ext.script.graalvm.javascript;
    exports org.scenebuilder.ext.script.graalvm.javascript.i18n;

    requires transitive scenebuilder.api;
    //requires transitive scenebuilder.core.extension.api;
    requires transitive org.scenebuilder.ext.script.graalvm.javascript.runtime;
    requires java.scripting;

    provides Extension with GraalVmJavascriptEngineExtension;
}