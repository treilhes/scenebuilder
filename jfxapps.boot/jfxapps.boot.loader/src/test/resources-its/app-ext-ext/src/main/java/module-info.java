import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.extext.ExtExtExtension;

module it.app.extext {
    exports app.extext;
    exports app.extext.api;
    exports app.extext.exported;

    opens app.extext.internal to spring.beans;

    requires scenebuilder.boot.loader;
    requires it.app.ext;

    provides Extension with ExtExtExtension;
}