package com.oracle.javafx.scenebuilder.tools.job;

import com.oracle.javafx.scenebuilder.metadata.javafx.javafx.scene.NodeMetadata;
import com.oracle.javafx.scenebuilder.tools.job.node.ModifyCacheHintJob;
import com.oracle.javafx.scenebuilder.tools.job.togglegroup.ModifySelectionToggleGroupJob;
import com.treilhes.emc4j.boot.api.context.annotation.OverrideBean;
import com.treilhes.emc4j.boot.api.context.annotation.OverridedBeanAware;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.metadata.property.ValuePropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.base.EnumerationPropertyMetadata;
import com.treilhes.jfxplace.core.metadata.property.value.ToggleGroupPropertyMetadata;

// TODO NEW: to be tested
@OverrideBean(SelectionJobsFactory.class)
public class SelectionJobsOverride implements OverridedBeanAware<SelectionJobsFactory> {

    private SelectionJobsFactory overridedBean;
    private final ModifySelectionToggleGroupJob.Factory modifySelectionToggleGroupJobFactory;
    private final ModifyCacheHintJob.Factory modifyCacheHintJobFactory;

    public SelectionJobsOverride(
            ModifySelectionToggleGroupJob.Factory modifySelectionToggleGroupJobFactory,
            ModifyCacheHintJob.Factory modifyCacheHintJobFactory) {
        this.modifySelectionToggleGroupJobFactory = modifySelectionToggleGroupJobFactory;
        this.modifyCacheHintJobFactory = modifyCacheHintJobFactory;
    }

    public Job modifySelection(ValuePropertyMetadata<?> propertyMetadata, Object newValue) {

        Job job = switch (propertyMetadata) {
            case ToggleGroupPropertyMetadata<?> _ -> modifySelectionToggleGroupJobFactory.getJob(newValue.toString());
            case EnumerationPropertyMetadata<?> epm -> {
                if (epm == NodeMetadata.cacheHintPropertyMetadata) {
                    yield modifyCacheHintJobFactory.getJob(propertyMetadata, newValue);
                }
                yield null;
            }
            default -> null;
        };

        if (job != null) {
            return job;
        } else {
            return overridedBean.modifySelection(propertyMetadata, newValue);
        }

    }



    @Override
    public void setOverridedBean(SelectionJobsFactory overridedBean) {
        this.overridedBean = overridedBean;
    }

}
