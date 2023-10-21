import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.ext.ExtExtension;

module it.app.ext {
    exports app.ext;
    exports app.ext.api;
    exports app.ext.exported;

    opens app.ext.internal to spring.beans;

    requires scenebuilder.boot.loader;
    requires it.app.root;

    provides Extension with ExtExtension;
}