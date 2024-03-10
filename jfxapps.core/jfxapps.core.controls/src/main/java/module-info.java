module jfxapps.core.controls {
    exports com.oracle.javafx.scenebuilder.javafx.controls;
    exports com.oracle.javafx.scenebuilder.javafx.controls.paintpicker.rotator;
    exports com.oracle.javafx.scenebuilder.javafx.controls.paintpicker.colorpicker;
    exports com.oracle.javafx.scenebuilder.javafx.controls.paintpicker;
    exports com.oracle.javafx.scenebuilder.javafx.controls.paintpicker.slider;
    exports com.oracle.javafx.scenebuilder.javafx.controls.paintpicker.gradientpicker;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
}