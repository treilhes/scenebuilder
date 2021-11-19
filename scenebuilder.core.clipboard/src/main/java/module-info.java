import com.oracle.javafx.scenebuilder.core.clipboard.ClipboardExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.clipboard {
    exports com.oracle.javafx.scenebuilder.core.clipboard;
    exports com.oracle.javafx.scenebuilder.core.clipboard.i18n;

    requires scenebuilder.starter;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;


    provides Extension with ClipboardExtension;
}