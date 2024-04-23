/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.controls.fxom;

import java.util.Collection;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.FxReferenceCollector;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.ext.FXOMNormalizer;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

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

        final Collection<FXOMProperty> expandedPaneProperties = fxomDocument.getFxomRoot()
                .collect(PropertyCollector.byName(expandedPaneName));

        for (FXOMProperty p : expandedPaneProperties) {
            if (p instanceof FXOMPropertyC) {
                final FXOMPropertyC pc = (FXOMPropertyC) p;
                assert pc.getChildren().isEmpty() == false;
                final FXOMObject v0 = pc.getChildren().get(0);
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

        final FXOMElement parentInstance = p.getParentInstance();
        assert parentInstance != null;
        final FXOMObject titledPane = p.getChildren().get(0);
        assert titledPane.getSceneGraphObject().get() instanceof TitledPane;
        assert titledPane.getFxId() != null;

        final FXOMObject fxomRoot = p.getFxomDocument().getFxomRoot();
        final List<FXOMIntrinsic> references = fxomRoot
                .collect(FxReferenceCollector.fxReferenceBySource(titledPane.getFxId()));
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
