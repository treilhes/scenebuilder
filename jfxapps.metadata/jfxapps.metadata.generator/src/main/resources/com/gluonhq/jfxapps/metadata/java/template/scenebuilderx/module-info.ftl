import com.treilhes.emc4j.boot.api.loader.extension.Extension;
import ${context.targetPackage}.${context.extensionClassSimpleName};

open module ${context.moduleName} {
    exports ${context.targetPackage};
    <#list packages as subPackage>
    exports ${subPackage};
    </#list>

    requires emc4j.boot.api;
    requires emc4j.boot.starter;
    requires transitive jfxplace.fxom.api;

    <#list context.requiredModules as requiredModule>
    requires transitive ${requiredModule};
    </#list>


    provides Extension with ${context.extensionClassSimpleName};
}