import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMNormalizer;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMRefresher;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FileLoader;
import com.oracle.javafx.scenebuilder.core.fxom.ext.LoaderCapabilitiesManager;
import com.oracle.javafx.scenebuilder.core.fxom.ext.TransientStateBackup;
import com.oracle.javafx.scenebuilder.core.fxom.ext.WeakProperty;

open module scenebuilder.core.fxom {

    exports com.oracle.javafx.scenebuilder.core.fxom;
    exports com.oracle.javafx.scenebuilder.core.fxom.glue;
    exports com.oracle.javafx.scenebuilder.core.fxom.sampledata;
    exports com.oracle.javafx.scenebuilder.core.fxom.ext;
    exports com.oracle.javafx.scenebuilder.core.fxom.util;

//    requires javafx.fxml;
//    requires transitive javafx.graphics;
//    requires javafx.controls;
//    requires java.xml;
//    requires javafx.media;
//    requires java.desktop;
//    requires org.slf4j;
    requires scenebuilder.starter;
    requires scenebuilder.core.utils;
    requires java.scripting;

    uses FXOMNormalizer;
    uses FXOMRefresher;
    uses TransientStateBackup;
    uses WeakProperty;
    uses FileLoader;
    uses LoaderCapabilitiesManager;
}