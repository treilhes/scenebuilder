/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.fxom.collector;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMScript;
import com.gluonhq.jfxapps.core.fxom.testutil.FilenameProvider;
import com.gluonhq.jfxapps.core.fxom.testutil.FxmlUtil;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
public class FxScriptCollectorTest {

    @Start
    private void start(Stage stage) {

    }

    @Test
    public void should_return_the_right_number_of_fxscripts() {
        FXOMDocument fxomDocument = FxmlUtil.fromFile(this, FxmlTestInfo.FX_SCRIPTS);

        List<FXOMScript> items = fxomDocument.getFxomRoot().collect(FxCollector.allFxScripts());

        assertEquals(3, items.size());
    }

    @Test
    public void should_return_the_right_number_of_fxincludes_by_source() {
        FXOMDocument fxomDocument = FxmlUtil.fromFile(this, FxmlTestInfo.FX_SCRIPTS);

        String source = "script2.js";
        List<FXOMScript> items = fxomDocument.getFxomRoot().collect(FxCollector.fxScriptBySource(source));

        assertEquals(2, items.size());
    }

    private enum FxmlTestInfo implements FilenameProvider {
        FX_SCRIPTS("fxScripts");

        private String filename;

        FxmlTestInfo(String filename) {
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
