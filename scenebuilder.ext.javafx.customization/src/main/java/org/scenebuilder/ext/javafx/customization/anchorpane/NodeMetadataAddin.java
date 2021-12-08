package org.scenebuilder.ext.javafx.customization.anchorpane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.metadata.javafx.hidden.NodeMetadata;

@Component
public class NodeMetadataAddin {

    public final PropertyName AnchorPane_anchorsGroupName = new PropertyName("anchorsGroup",
            javafx.scene.layout.AnchorPane.class); // NOCHECK

    public NodeMetadataAddin(@Autowired NodeMetadata nodeMetadata) {
        super();

        final AnchorPropertyGroupMetadata AnchorPane_AnchorPropertyGroupMetadata = new AnchorPropertyGroupMetadata.Builder()
                .withName(AnchorPane_anchorsGroupName)
                .withTopAnchorProperty(nodeMetadata.AnchorPane_topAnchorPropertyMetadata)
                .withRightAnchorProperty(nodeMetadata.AnchorPane_rightAnchorPropertyMetadata)
                .withBottomAnchorProperty(nodeMetadata.AnchorPane_bottomAnchorPropertyMetadata)
                .withLeftAnchorProperty(nodeMetadata.AnchorPane_leftAnchorPropertyMetadata)
                .withInspectorPath(new InspectorPath("Layout", "AnchorPane COnst", 0))
                .build();

        nodeMetadata.getProperties().add(AnchorPane_AnchorPropertyGroupMetadata);

//        nodeMetadata.getProperties().removeAll(Arrays.asList(
//                nodeMetadata.AnchorPane_topAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_rightAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_bottomAnchorPropertyMetadata,
//                nodeMetadata.AnchorPane_leftAnchorPropertyMetadata
//                ));

        nodeMetadata.getProperties().remove(nodeMetadata.AnchorPane_topAnchorPropertyMetadata);
        nodeMetadata.getProperties().remove(nodeMetadata.AnchorPane_rightAnchorPropertyMetadata);
        nodeMetadata.getProperties().remove(nodeMetadata.AnchorPane_bottomAnchorPropertyMetadata);
        nodeMetadata.getProperties().remove(nodeMetadata.AnchorPane_leftAnchorPropertyMetadata);



    }


}
