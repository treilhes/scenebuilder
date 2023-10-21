package app.editor.ext;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;

import app.editor.EditorExtension;
import app.editor.ext.exported.ExportedEditorService;
import app.editor.ext.internal.LocalEditorExtService;


public class EditorExtExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("00000000-0000-0000-0001-000000000001");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return EditorExtension.ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of(LocalEditorExtService.class);
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of(ExportedEditorService.class);
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
