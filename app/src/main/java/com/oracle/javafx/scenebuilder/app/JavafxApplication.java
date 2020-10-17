package com.oracle.javafx.scenebuilder.app;


import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Below class is based on Josh Long tutorial "Spring with Javafx"
 * video: https://spring.io/blog/2019/01/16/spring-tips-javafx
 * source: https://github.com/spring-tips/javafx
 * @author ptreilhes
 *
 */
public class JavafxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer = new ApplicationContextInitializer<GenericApplicationContext>() {
            @Override
            public void initialize(GenericApplicationContext genericApplicationContext) {
                genericApplicationContext.registerBean(Application.class, () -> JavafxApplication.this);
                genericApplicationContext.registerBean(Parameters.class, () -> getParameters());
                genericApplicationContext.registerBean(HostServices.class, () -> getHostServices());
            }
        };

        this.context = new SpringApplicationBuilder().sources(this.getClass())
        		.initializers(initializer)
                .build().run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	// we can't use injection here so publish an event
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        this.context.close();
        Platform.exit();
    }

    public class StageReadyEvent extends ApplicationEvent {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Stage getStage() {
            return Stage.class.cast(getSource());
        }

        public StageReadyEvent(Object source) {
            super(source);
        }
    }
}
