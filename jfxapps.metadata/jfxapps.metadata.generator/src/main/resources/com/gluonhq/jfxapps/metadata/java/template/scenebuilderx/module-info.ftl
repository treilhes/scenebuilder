import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import ${context.targetPackage}.${context.extensionClassSimpleName};

open module ${context.moduleName} {
    exports ${context.targetPackage};
    <#list packages as subPackage>
    exports ${subPackage};
    </#list>

    requires jfxapps.boot.api;
    requires jfxapps.boot.starter;
    requires transitive jfxapps.core.metadata;
    requires transitive jfxapps.core.fxom;

    <#list context.requiredModules as requiredModule>
    requires transitive ${requiredModule};
    </#list>


    provides Extension with ${context.extensionClassSimpleName};
}