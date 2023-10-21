package app.editor.extext;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;

import app.editor.ext.EditorExtExtension;
import app.editor.extext.exported.ExportedEditorExtService;
import app.editor.extext.internal.LocalEditorExtExtService;


public class EditorExtExtExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("00000000-0000-0000-0001-000000000002");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return EditorExtExtension.ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(LocalEditorExtExtService.class);
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(ExportedEditorExtService.class);
    }

    @Override
    public InputStream getLicense() {
        return null;
    }

    @Override
    public InputStream getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getLoadingImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getIconX2() {
        // TODO Auto-generated method stub
        return null;
    }

}
