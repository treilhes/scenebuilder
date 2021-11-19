import com.oracle.javafx.scenebuilder.controls.BaseControlsExtension;
import com.oracle.javafx.scenebuilder.controls.fxom.AccordionStateBackup;
import com.oracle.javafx.scenebuilder.controls.fxom.ClipWeakProperty;
import com.oracle.javafx.scenebuilder.controls.fxom.ExpandedPaneNormalizer;
import com.oracle.javafx.scenebuilder.controls.fxom.ExpandedPaneWeakProperty;
import com.oracle.javafx.scenebuilder.controls.fxom.ImageFileLoader;
import com.oracle.javafx.scenebuilder.controls.fxom.LabelForWeakProperty;
import com.oracle.javafx.scenebuilder.controls.fxom.MediaFileLoader;
import com.oracle.javafx.scenebuilder.controls.fxom.SplitPaneRefresher;
import com.oracle.javafx.scenebuilder.controls.fxom.TabPaneStateBackup;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMNormalizer;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMRefresher;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FileLoader;
import com.oracle.javafx.scenebuilder.core.fxom.ext.TransientStateBackup;
import com.oracle.javafx.scenebuilder.core.fxom.ext.WeakProperty;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.controls {
    exports com.oracle.javafx.scenebuilder.controls.contextmenu;
    exports com.oracle.javafx.scenebuilder.controls;
    //exports com.oracle.javafx.scenebuilder.controls.metadata;
    //exports com.oracle.javafx.scenebuilder.controls.mask;
    //opens com.oracle.javafx.scenebuilder.controls.metadata to spring.core;

    requires scenebuilder.starter;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.graphics;
//    requires javafx.media;
//    requires javafx.swing;
//    requires javafx.web;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.fxom;
    requires scenebuilder.core.metadata;

//    requires spring.beans;
//    requires spring.context;

    provides Extension with BaseControlsExtension;
    provides FXOMNormalizer with ExpandedPaneNormalizer;
    provides FXOMRefresher with SplitPaneRefresher;
    provides TransientStateBackup with AccordionStateBackup, TabPaneStateBackup;
    provides WeakProperty with LabelForWeakProperty,ExpandedPaneWeakProperty,ClipWeakProperty;
    provides FileLoader with ImageFileLoader,MediaFileLoader;
}