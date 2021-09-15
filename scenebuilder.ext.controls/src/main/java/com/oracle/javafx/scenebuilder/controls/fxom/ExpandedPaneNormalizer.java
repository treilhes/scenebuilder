package com.oracle.javafx.scenebuilder.controls.fxom;

import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FXOMNormalizer;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

import javafx.scene.control.TitledPane;

//@formatter:off
/**
 * 
 * We look for the following pattern:<br/>
 * <br/>
 * &lt;Accordion><br/>
 * &nbsp;&nbsp;&nbsp;&lt;expandedPane><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;TitledPane fx:id="x1" text="B"> ... &lt;/TitledPane><br/>
 * &nbsp;&nbsp;&nbsp;&lt;/expandedPane><br/>
 * &nbsp;&nbsp;&nbsp;&lt;panes><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;TitledPane text="A"> ... &lt;/TitledPane><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;fx:reference source="x1" /><br/>
 * &nbsp;&nbsp;&nbsp;&lt;/panes><br/>
 * &lt;/Accordion><br/>
 * <br/>
 * 
 * and transform it as:<br/>
 * <br/>
 * &lt;Accordion><br/>
 * &nbsp;&nbsp;&nbsp;&lt;panes><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;TitledPane text="A"> ... &lt;/TitledPane><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;TitledPane text="B"> ... &lt;/TitledPane><br/>
 * &nbsp;&nbsp;&nbsp;&lt;/panes><br/>
 * &lt;/Accordion><br/>
 * <br/>
 * 
 */
//@formatter:on
public class ExpandedPaneNormalizer implements FXOMNormalizer {

    private static final PropertyName expandedPaneName = new PropertyName("expandedPane");

    @Override
    public int normalize(FXOMDocument fxomDocument) {
        int changeCount = 0;

        final List<FXOMProperty> expandedPaneProperties = fxomDocument.getFxomRoot()
                .collectProperties(expandedPaneName);

        for (FXOMProperty p : expandedPaneProperties) {
            if (p instanceof FXOMPropertyC) {
                final FXOMPropertyC pc = (FXOMPropertyC) p;
                assert pc.getValues().isEmpty() == false;
                final FXOMObject v0 = pc.getValues().get(0);
                if (v0 instanceof FXOMInstance) {
                    normalizeExpandedPaneProperty(pc);
                } else {
                    assert v0 instanceof FXOMIntrinsic;
                    p.removeFromParentInstance();
                }
            } else {
                assert p instanceof FXOMPropertyT;
                final FXOMPropertyT pt = (FXOMPropertyT) p;
                assert pt.getValue().equals("$null");
                p.removeFromParentInstance();
            }

            changeCount++;
        }
        return changeCount;
    }

    private void normalizeExpandedPaneProperty(FXOMPropertyC p) {

        assert p != null;

        // @formatter:off
        /*
         * 
         * <Accordion>                           // p.getParentInstance()
         *   <expandedPane>                      // p
         *     <TitledPane fx:id="x1" text="B">  // p.getValues().get(0)
         *       ...
         *     </TitledPane>
         *   </expandedPane>
         *   <panes>                             // reference.getParentProperty()         
         *     <TitledPane text="A">
         *       ...
         *     </TitledPane>
         *     <fx:reference source="x1" />      // reference
         *   </panes>
         * </Accordion>
         * 
         */
        // @formatter:on

        final FXOMInstance parentInstance = p.getParentInstance();
        assert parentInstance != null;
        final FXOMObject titledPane = p.getValues().get(0);
        assert titledPane.getSceneGraphObject() instanceof TitledPane;
        assert titledPane.getFxId() != null;

        final FXOMObject fxomRoot = p.getFxomDocument().getFxomRoot();
        final List<FXOMIntrinsic> references = fxomRoot.collectReferences(titledPane.getFxId());
        assert references.size() == 1;
        final FXOMIntrinsic reference = references.get(0);
        assert reference.getSource().equals(titledPane.getFxId());
        assert reference.getParentObject() == parentInstance;
        final int referenceIndex = reference.getIndexInParentProperty();

        p.removeFromParentInstance();
        titledPane.removeFromParentProperty();
        titledPane.addToParentProperty(referenceIndex, reference.getParentProperty());
        reference.removeFromParentProperty();
    }
}
