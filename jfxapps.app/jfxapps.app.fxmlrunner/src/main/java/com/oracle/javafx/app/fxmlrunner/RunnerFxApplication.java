package com.oracle.javafx.app.fxmlrunner;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RunnerFxApplication extends Application {

    public RunnerFxApplication() {
        super();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();

        String fxmlPath = params.getRaw().get(0);
        File fxmlFile = new File(fxmlPath);
        Runner.logger.info("Launching {}", fxmlFile);

        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(this.getClass().getClassLoader());
        loader.setLocation(fxmlFile.toURI().toURL());
        loader.setController(this);


        // we change the context classloader because the script manager
        // in FXMLLoader use it to load script engines
        ClassLoader backup = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        Parent gp = loader.load();

        Thread.currentThread().setContextClassLoader(backup);

        Scene scene = new Scene(gp, 1280.0, 900.0, Color.BEIGE);
        primaryStage.setTitle(fxmlFile.getName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}