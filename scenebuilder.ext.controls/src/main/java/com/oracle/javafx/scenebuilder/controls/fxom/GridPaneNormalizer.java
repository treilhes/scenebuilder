package com.oracle.javafx.scenebuilder.controls.fxom;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMNormalizer;
import com.oracle.javafx.scenebuilder.core.fxom.util.Deprecation;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ColumnConstraintsListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.RowConstraintsListPropertyMetadata;

import javafx.scene.layout.GridPane;

public class GridPaneNormalizer implements FXOMNormalizer {

    private final static ColumnConstraintsListPropertyMetadata columnConstraintsMeta = new ColumnConstraintsListPropertyMetadata.Builder().build();
    private final static RowConstraintsListPropertyMetadata rowConstraintsMeta = new RowConstraintsListPropertyMetadata.Builder().build();

    @Override
    public int normalize(FXOMDocument fxomDocument) {
        int changeCount = 0;
        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        for (FXOMObject fxomGridPane : fxomRoot.collectObjectWithSceneGraphObjectClass(GridPane.class)) {
            normalizeGridPane(fxomGridPane);
            changeCount++;
        }
        return changeCount;
    }

    private void normalizeGridPane(FXOMObject fxomGridPane) {
        assert fxomGridPane instanceof FXOMInstance;
        assert fxomGridPane.getSceneGraphObject() instanceof GridPane;

        final GridPane gridPane = (GridPane) fxomGridPane.getSceneGraphObject();
        final int columnCount = Deprecation.getGridPaneColumnCount(gridPane);
        final int rowCount = Deprecation.getGridPaneRowCount(gridPane);
        columnConstraintsMeta.unpack((FXOMInstance) fxomGridPane, columnCount);
        rowConstraintsMeta.unpack((FXOMInstance) fxomGridPane, rowCount);
    }
}
