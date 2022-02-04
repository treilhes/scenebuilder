package com.oracle.javafx.scenebuilder.controls.fxom;

import java.util.List;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMRefresher;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleArrayPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ListValuePropertyMetadata;

import javafx.scene.control.SplitPane;

/**
 * The case of SplitPane.dividerPositions property
 * -----------------------------------------------
 *
 * When user adds a child to a SplitPane, this adds a new entry in
 * SplitPane.children property but also adds a new value to
 * SplitPane.dividerPositions by side-effect.
 *
 * The change in SplitPane.dividerPositions is performed at scene graph
 * level by FX. Thus it is unseen by FXOM.
 *
 * So in that case we perform a special operation which copies value of
 * SplitPane.dividerPositions into FXOMProperty representing
 * dividerPositions in FXOM.
 */
public class SplitPaneRefresher implements FXOMRefresher {

    @Override
    public void refresh(FXOMDocument document) {
        final FXOMObject fxomRoot = document.getFxomRoot();
        if (fxomRoot != null) {
            final Metadata metadata
                    = Api.get().getMetadata();
            final PropertyName dividerPositionsName
                    = new PropertyName("dividerPositions");
            final List<FXOMObject> candidates
                    = fxomRoot.collectObjectWithSceneGraphObjectClass(SplitPane.class);

            for (FXOMObject fxomObject : candidates) {
                if (fxomObject instanceof FXOMInstance) {
                    final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
                    assert fxomInstance.getSceneGraphObject() instanceof SplitPane;
                    final SplitPane splitPane
                            = (SplitPane) fxomInstance.getSceneGraphObject();
                    splitPane.layout();
                    final ValuePropertyMetadata vpm
                            = metadata.queryValueProperty(fxomInstance, dividerPositionsName);
                    assert vpm instanceof ListValuePropertyMetadata
                            : "vpm.getClass()=" + vpm.getClass().getSimpleName();
                    final DoubleArrayPropertyMetadata davpm
                            = (DoubleArrayPropertyMetadata) vpm;
                    davpm.synchronizeWithSceneGraphObject(fxomInstance);
                }
            }
        }
    }

}
