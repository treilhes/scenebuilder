import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import ${context.targetPackage}.${context.extensionClassSimpleName};

open module ${context.moduleName} {
    exports ${context.targetPackage};
    <#list packages as subPackage>
    exports ${subPackage};
    </#list>

    requires jfxapps.boot.loader;
    requires jfxapps.boot.starter;
    requires jfxapps.core.metadata;
    requires jfxapps.core.fxom;

    <#list context.requiredModules as requiredModule>
    requires transitive ${requiredModule};
    </#list>


    provides Extension with ${context.extensionClassSimpleName};
}