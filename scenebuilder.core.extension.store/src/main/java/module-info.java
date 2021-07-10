import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.extstore.ExtensionStoreExtension;

open module scenebuilder.core.extension.store {
    exports com.oracle.javafx.scenebuilder.extstore.fs;
    exports com.oracle.javafx.scenebuilder.extstore;
    exports com.oracle.javafx.scenebuilder.extstore.i18n;

    requires javafx.graphics;
    requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    
    provides Extension with ExtensionStoreExtension;
    uses Extension;
}