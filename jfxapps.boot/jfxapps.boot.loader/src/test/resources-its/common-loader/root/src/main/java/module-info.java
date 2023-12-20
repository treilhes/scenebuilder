import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.root.RootExtension;

module it.app.root {

    exports app.root;
    exports app.root.api;
    opens app.root.internal to spring.beans;

    requires scenebuilder.boot.loader;

    provides Extension with RootExtension;
}