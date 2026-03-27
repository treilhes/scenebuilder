package com.oracle.javafx.scenebuilder.fxom.merge;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.treilhes.emc4j.boot.api.loader.extension.OpenExtension;

public class ScenebuilderFxomMergeExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("78feb74a-f3e1-46e1-8d94-74feb6769f0b");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public Set<UUID> getMergedExtensions() {
        return Set.of(
                UUID.fromString("033bfca9-6681-497c-b4ce-10ea31080eab"), // fxom.document
                UUID.fromString("07a8af43-755e-4a51-a598-66ac13e3f7a5"), // fxom.jobs
                UUID.fromString("51c14d5d-1f38-4f15-ae5d-c7d493d4e726"), // fxom.editor
                UUID.fromString("285f16bb-9af8-4c60-9ca9-5098a9d6e920"), // fxom.dnd
                UUID.fromString("a112d6e9-4079-4733-96d1-d29b3fef675d") // fxom.selection
                
                );
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
        return List.of();
    }

}
