import ${package}.${extensionName};
import com.oracle.javafx.scenebuilder.extension.Extension;

open module ${moduleName} {
    exports ${package};
    <#list packages as subPackage>
    exports ${subPackage};
    </#list>

    requires scenebuilder.starter;
    requires scenebuilder.core.extension.api;
    requires transitive scenebuilder.core.fxom;
    requires transitive scenebuilder.core.metadata;

    <#list moduleRequires as moduleRequire>
    requires ${moduleRequire};
    </#list>


    provides Extension with ${extensionName};
}