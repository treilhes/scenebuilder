package com.oracle.javafx.scenebuilder.controls.metadata.fx;

import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_CONTROLS;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

@Component
public class FxIncludeElementMetadata extends ComponentClassMetadata<FXOMIntrinsic> {
    protected FxIncludeElementMetadata() {
        super(FXOMIntrinsic.class, null);
//            getProperties().add(valueCatalog.AnchorPane_bottomAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_leftAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_rightAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_topAnchorPropertyMetadata);

//        getProperties().add(NodeMetadata.baselineOffsetPropertyMetadata.build());
//        getProperties().add(NodeMetadata.boundsInLocalPropertyMetadata.build());
//        getProperties().add(NodeMetadata.boundsInParentPropertyMetadata.build());
//        getProperties().add(NodeMetadata.contentBiasPropertyMetadata.build());
//        getProperties().add(NodeMetadata.effectiveNodeOrientationPropertyMetadata.build());
//        getProperties().add(NodeMetadata.layoutBoundsPropertyMetadata.build());
//        getProperties().add(NodeMetadata.layoutXPropertyMetadata.build());
//        getProperties().add(NodeMetadata.layoutYPropertyMetadata.build());
//
//        getProperties().add(valueCatalog.AnchorPane_AnchorPropertyGroupMetadata);
//
//
//
//        getProperties().add(valueCatalog.BorderPane_alignmentPropertyMetadata);
//        getProperties().add(valueCatalog.FlowPane_marginPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_columnIndexPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_columnSpanPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_halignmentPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_hgrowPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_rowIndexPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_rowSpanPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_valignmentPropertyMetadata);
//        getProperties().add(valueCatalog.GridPane_vgrowPropertyMetadata);
//        getProperties().add(valueCatalog.HBox_hgrowPropertyMetadata);
//        getProperties().add(valueCatalog.StackPane_alignmentPropertyMetadata);
//        getProperties().add(valueCatalog.TilePane_alignmentPropertyMetadata);
//        getProperties().add(valueCatalog.VBox_vgrowPropertyMetadata);
//
//        getProperties().add(valueCatalog.maxHeight_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.maxWidth_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.minHeight_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.minWidth_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.prefHeight_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.prefWidth_COMPUTED_PropertyMetadata);
//        getProperties().add(valueCatalog.rotatePropertyMetadata);
//        getProperties().add(valueCatalog.rotationAxisPropertyMetadata);
//        getProperties().add(valueCatalog.scaleXPropertyMetadata);
//        getProperties().add(valueCatalog.scaleYPropertyMetadata);
//        getProperties().add(valueCatalog.scaleZPropertyMetadata);
//        getProperties().add(valueCatalog.translateXPropertyMetadata);
//        getProperties().add(valueCatalog.translateYPropertyMetadata);
//        getProperties().add(valueCatalog.translateZPropertyMetadata);
//
//
//
//
//        getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
//
//        getProperties().add(valueCatalog.snapToPixelPropertyMetadata);
//
//        getProperties().add(valueCatalog.includeFxmlPropertyMetadata);

        // TODO associated image are only valid for Type.FX_INCLUDE
        // other types does not have associated images
        // FX_REFERENCE,
        // FX_COPY,
        // UNDEFINED
        getQualifiers().put(Qualifier.HIDDEN,
                new Qualifier(null, null, null, getClass().getResource("nodeicons/Included.png"),
                        getClass().getResource("nodeicons/Included@2x.png"), TAG_CONTROLS));
    }
}