package com.oracle.javafx.scenebuilder.core.loader.appext1;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.loader.api.Application;

@Component
public class AppExt1SomeClass {

    @SuppressWarnings("exports")
    public AppExt1SomeClass(Application application) {
        super();
        System.out.println(this.getClass().getName());

        System.out.println("Application param : " + application.getContext());
    }

}
