module jfxapps.core.controls {
    exports com.gluonhq.jfxapps.core.controls;
    exports com.gluonhq.jfxapps.core.controls.paintpicker.rotator;
    exports com.gluonhq.jfxapps.core.controls.paintpicker.colorpicker;
    exports com.gluonhq.jfxapps.core.controls.paintpicker;
    exports com.gluonhq.jfxapps.core.controls.paintpicker.slider;
    exports com.gluonhq.jfxapps.core.controls.paintpicker.gradientpicker;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
}