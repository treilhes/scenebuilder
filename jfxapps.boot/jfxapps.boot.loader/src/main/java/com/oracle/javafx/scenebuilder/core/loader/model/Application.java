package com.oracle.javafx.scenebuilder.core.loader.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;

public class Application extends AbstractExtension<ApplicationExtension> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Set<Editor> editors;

    public Application(UUID id, ExtensionContentProvider contentProvider) {
        super(id, contentProvider);
        this.editors = new HashSet<>();
    }

    protected Application() {
        this(null, null);
    }

    public Set<Editor> getEditors() {
        return Collections.unmodifiableSet(editors);
    }

    protected void setEditors(Set<Editor> editors) {
        this.editors = editors;
    }

    public void addEditor(Editor editor) {
        editors.add(editor);
    }

    public void removeEditor(Editor editor) {
        editors.remove(editor);
    }

    @Override
    public Application clone() {
        try {
            return (Application) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
