package com.oracle.javafx.scenebuilder.app.settings;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.settings.AbstractSetting;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@Component
public class WindowIconSetting extends AbstractSetting {
	
	public static final String APP_ICON_16 = WindowIconSetting.class.getResource("SceneBuilderLogo_16.png").toString();
    public static final String APP_ICON_32 = WindowIconSetting.class.getResource("SceneBuilderLogo_32.png").toString();

	protected WindowIconSetting() {}
	
	public void setWindowIcon(Alert alert) {
        setWindowIcon((Stage)alert.getDialogPane().getScene().getWindow());
    }
    public void setWindowIcon(Stage stage) {
        Image icon16 = new Image(WindowIconSetting.APP_ICON_16);
        Image icon32 = new Image(WindowIconSetting.APP_ICON_32);
        stage.getIcons().addAll(icon16, icon32);
    }

}
