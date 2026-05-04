package com.oracle.javafx.scenebuilder.tools.driver.borderpane;

import com.treilhes.jfxplace.core.api.fxom.dnd.DropJobCustomizer;
import com.treilhes.jfxplace.core.api.fxom.job.base.BatchJob;
import com.treilhes.jfxplace.core.api.fxom.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.fxom.FXOMInstance;
import com.treilhes.jfxplace.core.fxom.util.PropertyName;
import com.treilhes.jfxplace.core.metadata.property.base.EnumerationPropertyMetadata;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

public class BorderPaneDropJobCustomizer implements DropJobCustomizer {

    private final FxomJobsFactory fxomJobsFactory;

    public BorderPaneDropJobCustomizer(FxomJobsFactory fxomJobsFactory) {
        super();
        this.fxomJobsFactory = fxomJobsFactory;
    }

    @Override
    public void customize(BatchJob job, FXOMInstance draggedObject) {
        // We add a job which sets BorderPane.alignment=CENTER on draggedObject
        final FXOMInstance draggedInstance = draggedObject;
        final PropertyName alignmentName
                = new PropertyName("alignment", BorderPane.class); //NOCHECK

        final var alignmentMeta = new EnumerationPropertyMetadata.Builder<Pos, Void>(Pos.class)
                    .name(alignmentName)
                    .nullEquivalent("UNUSED")//NOCHECK
                    .readWrite(true)
                    .build();

        final Job alignmentJob = fxomJobsFactory.modifyObject(draggedInstance, alignmentMeta,
                Pos.CENTER);

        job.addSubJob(alignmentJob);
    }

}
