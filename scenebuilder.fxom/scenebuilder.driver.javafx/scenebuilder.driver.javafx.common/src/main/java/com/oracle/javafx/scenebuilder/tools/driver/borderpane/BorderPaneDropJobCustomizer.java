package com.oracle.javafx.scenebuilder.tools.driver.borderpane;

import com.gluonhq.jfxapps.core.api.fxom.dnd.DropJobCustomizer;
import com.gluonhq.jfxapps.core.api.fxom.job.base.BatchJob;
import com.gluonhq.jfxapps.core.api.fxom.jobs.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.scenebuilder.metadata.property.value.EnumerationPropertyMetadata;

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
