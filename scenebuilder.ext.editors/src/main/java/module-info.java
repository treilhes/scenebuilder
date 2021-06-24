import com.oracle.javafx.scenebuilder.editors.DefaultEditorsExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.editors {
    exports com.oracle.javafx.scenebuilder.editors.control.paintpicker.slider;
    exports com.oracle.javafx.scenebuilder.editors.control;
    exports com.oracle.javafx.scenebuilder.editors.control.effectpicker.editors;
    exports com.oracle.javafx.scenebuilder.editors.control.paintpicker;
    exports com.oracle.javafx.scenebuilder.editors;
    exports com.oracle.javafx.scenebuilder.editors.popupeditors;
    exports com.oracle.javafx.scenebuilder.editors.control.paintpicker.colorpicker;
    exports com.oracle.javafx.scenebuilder.editors.control.paintpicker.rotator;
    exports com.oracle.javafx.scenebuilder.editors.control.paintpicker.gradientpicker;
    exports com.oracle.javafx.scenebuilder.editors.control.effectpicker;

    requires io.reactivex.rxjava2;
    requires java.logging;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with DefaultEditorsExtension;
}