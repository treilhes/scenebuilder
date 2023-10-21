package com.oracle.javafx.scenebuilder.core.loader.editora;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.loader.api.Application;

@Component
public class EditorASomeClass {

    @SuppressWarnings("exports")
    public EditorASomeClass(Application application) {
        super();
        System.out.println(this.getClass().getName());

        System.out.println("Application param : " + application.getContext());
    }

}
