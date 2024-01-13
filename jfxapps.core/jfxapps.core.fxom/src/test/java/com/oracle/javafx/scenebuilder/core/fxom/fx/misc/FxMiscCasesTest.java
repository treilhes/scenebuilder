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
package com.oracle.javafx.scenebuilder.core.fxom.fx.misc;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMScript;
import com.oracle.javafx.scenebuilder.core.fxom.fx.CloneFixture;
import com.oracle.javafx.scenebuilder.core.fxom.fx.IOFixture;

import javafx.stage.Stage;

/**
 * Unit test for {@link FXOMScript}
 */
@ExtendWith(ApplicationExtension.class)
@SetSystemProperty(key = "javafx.allowjs", value = "true")
public class FxMiscCasesTest {

    private static final boolean FAILURE_EXPECTED = true;
    private static final String JFX_VERSION = "xxx";

    private enum Case {
        NESTED_DEFINE_INCLUDE_REF_WITH_BUTTONS("nested_define_include_ref_with_buttons.fxml")
        ;

        String fileName;
        boolean failureExpected;
        Case(String fileName){
            this(fileName, false);
        }
        Case(String fileName, boolean failureExpected){
            this.fileName = fileName;
            this.failureExpected = failureExpected;
        }
        String getFileName() {
            return this.fileName;
        }
        boolean isFailureExpected() {
            return this.failureExpected;
        }
    }

    @Start
    private void start(Stage stage) {
    }

    @ParameterizedTest
    @EnumSource(Case.class)
    public void testIsLoadableByJavafx(Case testCase) {
        IOFixture.testIsLoadableByJavafx(this, testCase.getFileName(), testCase.isFailureExpected());
    }

    @ParameterizedTest
    @EnumSource(Case.class)
    public void testIsFxomLoadable(Case testCase) {
        IOFixture.testIsFxomLoadable(this, testCase.getFileName(), testCase.isFailureExpected());
    }

    @ParameterizedTest
    @EnumSource(Case.class)
    public void testIsFxomSerializable(Case testCase) {
        IOFixture.testIsFxomSerializable(this, testCase.getFileName(), testCase.isFailureExpected());
    }

    @ParameterizedTest
    @EnumSource(Case.class)
    public void testSerializedIsEqualToSource(Case testCase) {
        IOFixture.testSerializedIsEqualToSource(this, testCase.getFileName(), testCase.isFailureExpected());
    }

    @ParameterizedTest
    @EnumSource(value = Case.class)
    public void testIsCloneable(Case testCase) {
        CloneFixture.testIsCloneable(this, testCase.getFileName(), testCase.isFailureExpected());
    }
}